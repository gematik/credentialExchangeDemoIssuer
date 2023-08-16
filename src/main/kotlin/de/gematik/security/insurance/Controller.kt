package de.gematik.security.insurance

import de.gematik.security.credentialExchangeLib.connection.WsConnection
import de.gematik.security.credentialExchangeLib.credentialSubjects.*
import de.gematik.security.credentialExchangeLib.crypto.ProofType
import de.gematik.security.credentialExchangeLib.extensions.Utils
import de.gematik.security.credentialExchangeLib.json
import de.gematik.security.credentialExchangeLib.protocols.*
import de.gematik.security.credentialIssuer
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.websocket.*
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.encodeToJsonElement
import kotlinx.serialization.json.jsonObject
import mu.KotlinLogging
import java.net.URI
import java.util.*

object Controller {

    private val logger = KotlinLogging.logger {}

    private val sixMonth = 1000L * 60 * 60 * 24 * 180

    val port = 8091

    var lastCallingRemoteAddress : String? = null

    fun start(wait : Boolean = false){
        CredentialExchangeIssuerProtocol.listen(WsConnection, host = "0.0.0.0", port = port) {
            lastCallingRemoteAddress = ((it.connection as WsConnection).session as DefaultWebSocketServerSession).call.request.local.remoteAddress
            while (true) {
                val message = it.receive()
                if (!handleIncomingMessage(it, message)) break
            }
        }
        embeddedServer(Netty, host = "0.0.0.0", port = 8080){
            configureTemplating()
            configureRouting()
        }.start(wait)
    }

    suspend fun handleIncomingMessage(protocolInstance: CredentialExchangeIssuerProtocol, message: LdObject): Boolean {

        val type = message.type ?: return true //ignore
        return when {
            type.contains("Close") -> false // close connection
            type.contains("Invitation") -> handleInvitation(protocolInstance, message as Invitation)
            type.contains("CredentialRequest") -> handleCredentialRequest(
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
                        type = Credential.DEFAULT_JSONLD_TYPES + "InsuranceCertificate"
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
        val invitationId = protocolInstance.protocolState.invitation?.id?: return false
        val insurant = insurants.find {it.insurance?.invitation?.id == invitationId} ?: return false
        val insurance = insurant.insurance ?: return false
        val verifiableCredential = getInsurance(insurant, message.holderKey).let {
            Credential(
                atContext = Credential.DEFAULT_JSONLD_CONTEXTS + listOf(URI.create("https://gematik.de/vsd/v1")),
                type = Credential.DEFAULT_JSONLD_TYPES + listOf("InsuranceCertificate"),
                credentialSubject = it,
                issuanceDate = Date(),
                issuer = credentialIssuer.didKey
            ).apply {
                sign(
                    LdProof(
                        type = listOf(ProofType.BbsBlsSignature2020.name),
                        created = Date(),
                        proofPurpose = ProofPurpose.ASSERTION_METHOD,
                        verificationMethod = credentialIssuer.verificationMethod
                    ),
                    credentialIssuer.keyPair.privateKey!!
                )
            }
        }?: return false
            protocolInstance.submitCredential(
                CredentialSubmit(
                    UUID.randomUUID().toString(),
                    credential = verifiableCredential
                )
            )
        return false
    }

    private fun getInsurance(insurant: Insurant, holderId: String): JsonObject? {
        val insurance = insurant.insurance ?: return null
        val streetAddress = insurance.streetAddress.split(' ')
        val costCenter = insurance.costCenter.split(' ')
        return json.encodeToJsonElement(
            Insurance(
                insurant = Insurant(
                    insurantId = insurance.insurantId,
                    familyName = insurant.name,
                    givenName = insurant.givenName,
                    birthdate = insurant.birthDate,
                    gender = insurant.gender,
                    streetAddress = StreetAddress(
                        postalCode = streetAddress[2].toInt(),
                        location = streetAddress[3],
                        street = streetAddress[0],
                        streetNumber = streetAddress[1],
                        country = streetAddress[4]
                    )
                ),
                coverage = Coverage(
                    start = Utils.getDate(1993, 10, 7),
                    costCenter = CostCenter(
                        name = costCenter[0],
                        countryCode = costCenter[1],
                        identification = costCenter[2].toInt()
                    ),
                    insuranceType = insurance.insuranceType,
                    residencyPrinciple = insurance.residencyPrinciple
                )
            ).apply { id = URI.create(holderId) }
        ).jsonObject
    }

}

