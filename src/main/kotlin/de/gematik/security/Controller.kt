package de.gematik.security

import de.gematik.security.credentialExchangeLib.crypto.BbsPlusSigner
import de.gematik.security.credentialExchangeLib.crypto.ProofType
import de.gematik.security.credentialExchangeLib.json
import de.gematik.security.credentialExchangeLib.protocols.*
import de.gematik.security.credentialsubjects.Recipient
import de.gematik.security.credentialsubjects.VaccinationEvent
import de.gematik.security.credentialsubjects.Vaccine
import kotlinx.serialization.json.encodeToJsonElement
import kotlinx.serialization.json.jsonObject
import mu.KotlinLogging
import java.net.URI
import java.util.*

object Controller {

    private val logger = KotlinLogging.logger {}

    private val sixMonth = 1000L * 60 * 60 * 24 * 180

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
        val invitationId = protocolInstance.protocolState.invitation?.id
        invitationId ?: return false
        val customer = customers.find { it.vaccinations.firstOrNull() { it.invitation.id == invitationId } != null }
        customer ?: return false
        val vaccination = customer.vaccinations.firstOrNull { it.invitation.id == invitationId }
        vaccination ?: return false
        val verifiableCredential = Credential(
            atContext = Credential.DEFAULT_JSONLD_CONTEXTS + listOf(URI.create("https://w3id.org/vaccination/v1")),
            type = Credential.DEFAULT_JSONLD_TYPES + listOf("VaccinationCertificate"),
            credentialSubject = json.encodeToJsonElement(
                VaccinationEvent(
                    order = "${vaccination.order}/3",
                    batchNumber = vaccination.batchNumber,
                    dateOfVaccination = vaccination.dateOfVaccination,
                    administeringCentre = "Praxis Sommergarten",
                    healthProfessional = "883110000015376",
                    countryOfVaccination = "GE",
                    nextVaccinationDate = Date(vaccination.dateOfVaccination.time + sixMonth),
                    recipient =  Recipient(
                        id = URI.create(message.holderKey),
                        birthDate = customer.birthDate,
                        familyName = customer.name,
                        givenName = customer.givenName,
                        gender = customer.gender.name
                    ),
                    vaccine =  Vaccine(
                        atcCode = vaccination.atcCode,
                        medicalProductName = vaccination.vaccine.details.medicalProductName,
                        marketingAuthorizationHolder = vaccination.vaccine.details.marketingAuthorizationHolder
                    )
                )
            ).jsonObject,
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