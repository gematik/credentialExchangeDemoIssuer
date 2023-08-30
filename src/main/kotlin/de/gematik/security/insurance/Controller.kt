package de.gematik.security.insurance

import de.gematik.security.credentialExchangeLib.connection.WsConnection
import de.gematik.security.credentialExchangeLib.crypto.ProofType
import de.gematik.security.credentialExchangeLib.json
import de.gematik.security.credentialExchangeLib.protocols.*
import de.gematik.security.credentialIssuer
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.websocket.*
import kotlinx.serialization.json.encodeToJsonElement
import kotlinx.serialization.json.jsonObject
import mu.KotlinLogging
import java.net.URI
import java.time.ZonedDateTime
import java.util.*

object Controller {

    private val logger = KotlinLogging.logger {}

    private val sixMonth = 1000L * 60 * 60 * 24 * 180

    val port = 8091

    var lastCallingRemoteAddress: String? = null

    fun start(wait: Boolean = false) {
        CredentialExchangeIssuerProtocol.listen(WsConnection, host = "0.0.0.0", port = port) {
            lastCallingRemoteAddress =
                ((it.connection as WsConnection).session as DefaultWebSocketServerSession).call.request.local.remoteAddress
            while (true) {
                val message = it.receive()
                if (!handleIncomingMessage(it, message)) break
            }
        }
        embeddedServer(Netty, host = "0.0.0.0", port = 8080) {
            configureTemplating()
            configureRouting()
        }.start(wait)
    }

    suspend fun handleIncomingMessage(protocolInstance: CredentialExchangeIssuerProtocol, message: LdObject): Boolean {

        return when {
            message.type.contains("Close") -> false // close connection
            message.type.contains("Invitation") -> handleInvitation(protocolInstance, message as Invitation)
            message.type.contains("CredentialRequest") -> handleCredentialRequest(
                protocolInstance,
                message as CredentialRequest
            )

            else -> true //ignore
        }
    }

    private suspend fun handleInvitation(
        protocolInstance: CredentialExchangeIssuerProtocol,
        message: Invitation
    ): Boolean {
        protocolInstance.sendOffer(
            CredentialOffer(
                UUID.randomUUID().toString(),
                outputDescriptor = Descriptor(
                    UUID.randomUUID().toString(), Credential(
                        atContext = Credential.DEFAULT_JSONLD_CONTEXTS + URI("https://gematik.de/vsd/v1"),
                        type = listOf("InsuranceCertificate")
                    )
                )
            )
        )
        return true
    }

    private suspend fun handleCredentialRequest(
        protocolInstance: CredentialExchangeIssuerProtocol,
        message: CredentialRequest
    ): Boolean {
        if(!message.outputDescriptor.frame.type.contains("InsuranceCertificate")) return false
        val invitationId = protocolInstance.protocolState.invitation?.id ?: return false
        val customer = customers.find { it.invitation.id == invitationId } ?: return false
        val verifiableCredential = customer.insurance?. apply {
            id = message.holderKey.toString()
        }?.let{
            JsonLdObject(json.encodeToJsonElement(it).jsonObject.toMap())
        }?.let {
            Credential(
                atContext = Credential.DEFAULT_JSONLD_CONTEXTS + listOf(URI.create("https://gematik.de/vsd/v1")),
                type = Credential.DEFAULT_JSONLD_TYPES + listOf("InsuranceCertificate"),
                credentialSubject = it,
                issuanceDate = ZonedDateTime.now(),
                issuer = credentialIssuer.didKey
            ).apply {
                sign(
                    LdProof(
                        type = listOf(ProofType.BbsBlsSignature2020.name),
                        created = ZonedDateTime.now(),
                        proofPurpose = ProofPurpose.ASSERTION_METHOD,
                        verificationMethod = credentialIssuer.verificationMethod
                    ),
                    credentialIssuer.keyPair.privateKey!!
                )
            }
        } ?: return false
        protocolInstance.submitCredential(
            CredentialSubmit(
                UUID.randomUUID().toString(),
                credential = verifiableCredential
            )
        )
        return false
    }

}

