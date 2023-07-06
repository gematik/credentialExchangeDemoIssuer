package de.gematik.security

import de.gematik.security.credentialExchangeLib.crypto.BbsPlusSigner
import de.gematik.security.credentialExchangeLib.crypto.ProofType
import de.gematik.security.credentialExchangeLib.protocols.*
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import mu.KotlinLogging
import java.net.URI
import java.util.*

object Controller {

    private val logger = KotlinLogging.logger {}

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
        val customer = customers.find { it.invitation.id == protocolInstance.protocolState.invitation?.id } ?: run {
            logger.info { "invalid or expired invitation" }
            return false
        }
        val verifiableCredential = Credential(
            atContext = Credential.DEFAULT_JSONLD_CONTEXTS + listOf(URI.create("https://w3id.org/vaccination/v1")),
            type = Credential.DEFAULT_JSONLD_TYPES + listOf("VaccinationCertificate"),
            credentialSubject = JsonObject(
                mapOf(
                    "type" to JsonPrimitive("VaccinationEvent"),
                    "batchNumber" to JsonPrimitive("1626382736"),
                    "dateOfVaccination" to JsonPrimitive("2021-06-23T13:40:12Z"),
                    "administeringCentre" to JsonPrimitive("Praxis Sommergarten"),
                    "healthProfessional" to JsonPrimitive("883110000015376"),
                    "countryOfVaccination" to JsonPrimitive("GE"),
                    "nextVaccinationDate" to JsonPrimitive("2021-08-16T13:40:12Z"),
                    "order" to JsonPrimitive("3/3"),
                    "recipient" to JsonObject(
                        mapOf(
                            "type" to JsonPrimitive("VaccineRecipient"),
                            "givenName" to JsonPrimitive(customer.givenName),
                            "familyName" to JsonPrimitive(customer.name),
                            "gender" to JsonPrimitive(customer.gender.name),
                            "birthDate" to JsonPrimitive(customer.birthDate.toSimpleString())
                        )
                    ),
                    "vaccine" to JsonObject(
                        mapOf(
                            "type" to JsonPrimitive("Vaccine"),
                            "atcCode" to JsonPrimitive("J07BX03"),
                            "medicinalProductName" to JsonPrimitive("COVID-19 Vaccine Moderna"),
                            "marketingAuthorizationHolder" to JsonPrimitive("Moderna Biotech")
                        )
                    )
                )
            ),
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
        protocolInstance.submitCredential(
            CredentialSubmit(
                UUID.randomUUID().toString(),
                credential = verifiableCredential
            )
        )
        return false
    }

}