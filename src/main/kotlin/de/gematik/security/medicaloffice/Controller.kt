package de.gematik.security.medicaloffice

import de.gematik.security.credentialExchangeLib.connection.WsConnection
import de.gematik.security.credentialExchangeLib.credentialSubjects.*
import de.gematik.security.credentialExchangeLib.crypto.BbsPlusSigner
import de.gematik.security.credentialExchangeLib.crypto.ProofType
import de.gematik.security.credentialExchangeLib.json
import de.gematik.security.credentialExchangeLib.protocols.*
import de.gematik.security.credentialIssuer
import de.gematik.security.localIpAddress
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.encodeToJsonElement
import kotlinx.serialization.json.jsonObject
import mu.KotlinLogging
import java.net.URI
import java.util.*

object Controller {

    private val logger = KotlinLogging.logger {}

    private val sixMonth = 1000L * 60 * 60 * 24 * 180

    val port = 8090

    fun start(wait: Boolean = false) {
        CredentialExchangeIssuerProtocol.listen(WsConnection, host = localIpAddress, port= port) {
            while (true) {
                val message = it.receive()
                if (!handleIncomingMessage(it, message)) break
            }
        }
        embeddedServer(Netty, port = 8081){
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
                        atContext = Credential.DEFAULT_JSONLD_CONTEXTS + URI("https://w3id.org/vaccination/v1"),
                        type = Credential.DEFAULT_JSONLD_TYPES + "VaccinationCertificate"
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
        val invitationId = protocolInstance.protocolState.invitation?.id ?: return false
        val patient =
            patients.find { it.vaccinations.firstOrNull() { it.invitation.id == invitationId } != null } ?: return false
        val vaccination = patient.vaccinations.firstOrNull { it.invitation.id == invitationId } ?: return false
        val verifiableCredential = getVaccination(patient, vaccination, message).let {
            Credential(
                atContext = Credential.DEFAULT_JSONLD_CONTEXTS + listOf(URI.create("https://w3id.org/vaccination/v1")),
                type = Credential.DEFAULT_JSONLD_TYPES + listOf("VaccinationCertificate"),
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
                    BbsPlusSigner(credentialIssuer.keyPair)
                )
            }
        }
        protocolInstance.submitCredential(
            CredentialSubmit(
                UUID.randomUUID().toString(),
                credential = verifiableCredential
            )
        )
        return false
    }

    private fun getVaccination(patient: Patient, vaccination: Vaccination, message: CredentialRequest): JsonObject {
        return json.encodeToJsonElement(
            VaccinationEvent(
                order = "${vaccination.order}/3",
                batchNumber = vaccination.batchNumber,
                dateOfVaccination = vaccination.dateOfVaccination,
                administeringCentre = "Praxis Sommergarten",
                healthProfessional = "883110000015376",
                countryOfVaccination = "GE",
                nextVaccinationDate = Date(vaccination.dateOfVaccination.time + sixMonth),
                recipient = Recipient(
                    id = URI.create(message.holderKey),
                    birthDate = patient.birthDate,
                    familyName = patient.name,
                    givenName = patient.givenName,
                    gender = patient.gender.name
                ),
                vaccine = Vaccine(
                    atcCode = vaccination.atcCode,
                    medicalProductName = vaccination.vaccine.details.medicalProductName,
                    marketingAuthorizationHolder = vaccination.vaccine.details.marketingAuthorizationHolder
                )
            )
        ).jsonObject
    }
}

