package de.gematik.security.medicaloffice

import de.gematik.security.credentialExchangeLib.connection.Invitation
import de.gematik.security.credentialExchangeLib.connection.websocket.WsConnection
import de.gematik.security.credentialExchangeLib.credentialSubjects.Insurance
import de.gematik.security.credentialExchangeLib.credentialSubjects.Recipient
import de.gematik.security.credentialExchangeLib.credentialSubjects.VaccinationEvent
import de.gematik.security.credentialExchangeLib.credentialSubjects.Vaccine
import de.gematik.security.credentialExchangeLib.crypto.ProofType
import de.gematik.security.credentialExchangeLib.extensions.createUri
import de.gematik.security.credentialExchangeLib.extensions.toIsoInstantString
import de.gematik.security.credentialExchangeLib.extensions.toObject
import de.gematik.security.credentialExchangeLib.extensions.toZonedDateTime
import de.gematik.security.credentialExchangeLib.json
import de.gematik.security.credentialExchangeLib.protocols.*
import de.gematik.security.credentialIssuer
import de.gematik.security.hostName
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.websocket.*
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.encodeToJsonElement
import kotlinx.serialization.json.jsonObject
import mu.KotlinLogging
import java.net.URI
import java.time.ZonedDateTime
import java.util.*

object Controller {

    private val logger = KotlinLogging.logger {}

    private val sixMonth = 1000L * 60 * 60 * 24 * 180

    val port = 8090

    var lastCallingRemoteAddress: String? = null

    val invitation = Invitation(
        id = "29fe2783-2a48-4cd3-8c8d-821764d6c463",
        label = "Praxis Sommergarten",
        goal = "Check In",
        goalCode = GoalCode.REQUEST_PRESENTATION,
        from = URI("ws://$hostName:${port}")
    )

    fun start(wait: Boolean = false) {
        WsConnection.listen(createUri("0.0.0.0", port = port)) {
            while (true) {
                lastCallingRemoteAddress =
                    (it.session as DefaultWebSocketServerSession).call.request.local.remoteAddress
                if (it.invitationId.toString() == invitation.id) { // Check in
                    PresentationExchangeVerifierProtocol.bind(it) {
                        // request insurance credential
                        if (handleInvitation(it)) {
                            while (true) {
                                if (!handleIncomingMessage(it)) break
                            }
                        }
                    }
                } else { // issue vaccination credential
                    CredentialExchangeIssuerProtocol.bind(it) {
                        // offer vacination credential
                        if (handleInvitation(it)) {
                            while (true) {
                                if (!handleIncomingMessage(it)) break
                            }
                        }
                    }
                }
                break
            }
        }
        embeddedServer(Netty, host = "0.0.0.0", port = 8081) {
            configureTemplating()
            configureRouting()
        }.start(wait)
    }

// issue credential

    suspend fun handleIncomingMessage(protocolInstance: CredentialExchangeIssuerProtocol): Boolean {
        val message = protocolInstance.receive()
        return when {
            message.type.contains("Close") -> false // close connection
            message.type.contains("CredentialRequest") -> handleCredentialRequest(
                protocolInstance,
                message as CredentialRequest
            )

            else -> true //ignore
        }
    }

    private suspend fun handleInvitation(
        protocolInstance: CredentialExchangeIssuerProtocol
    ): Boolean {
        protocolInstance.sendOffer(
            CredentialOffer(
                UUID.randomUUID().toString(),
                outputDescriptor = Descriptor(
                    UUID.randomUUID().toString(), Credential(
                        atContext = Credential.DEFAULT_JSONLD_CONTEXTS + URI("https://w3id.org/vaccination/v1"),
                        type = listOf("VaccinationCertificate")
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
        if (!message.outputDescriptor.frame.type.contains("VaccinationCertificate")) return false
        val invitationId = protocolInstance.protocolState.invitationId ?: return false
        val patient =
            patients.find { it.vaccinations.firstOrNull() { it.invitation.id == invitationId.toString() } != null }
                ?: return false
        val vaccination =
            patient.vaccinations.firstOrNull { it.invitation.id == invitationId.toString() } ?: return false
        val verifiableCredential = getVaccination(patient, vaccination, message).let {
            Credential(
                atContext = Credential.DEFAULT_JSONLD_CONTEXTS + listOf(URI.create("https://w3id.org/vaccination/v1")),
                type = Credential.DEFAULT_JSONLD_TYPES + listOf("VaccinationCertificate"),
                credentialSubject = JsonLdObject(it.toMap()),
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
                nextVaccinationDate = vaccination.dateOfVaccination.toZonedDateTime().plusMonths(6)
                    .toIsoInstantString(),
                recipient = Recipient(
                    birthDate = patient.birthDate,
                    familyName = patient.name,
                    givenName = patient.givenName,
                    gender = patient.gender.name
                ).apply {
                    id = message.holderKey.toString()
                },
                vaccine = Vaccine(
                    atcCode = vaccination.atcCode,
                    medicalProductName = vaccination.vaccine.details.medicalProductName,
                    marketingAuthorizationHolder = vaccination.vaccine.details.marketingAuthorizationHolder
                )
            )
        ).jsonObject
    }

    // check in by verifing insurance credential
    suspend fun handleIncomingMessage(protocolInstance: PresentationExchangeVerifierProtocol): Boolean {
        val message = protocolInstance.receive()
        return when {
            message.type.contains("Close") -> false // close connection
            message.type.contains("PresentationSubmit") -> handlePresentationSubmit(
                protocolInstance,
                message as PresentationSubmit
            )

            else -> true //ignore
        }
    }

    suspend fun handleInvitation(
        protocolInstance: PresentationExchangeVerifierProtocol
    ): Boolean {
        protocolInstance.requestPresentation(
            PresentationRequest(
                inputDescriptor = listOf(
                    Descriptor(
                        UUID.randomUUID().toString(),
                        Credential(
                            atContext = Credential.DEFAULT_JSONLD_CONTEXTS + URI("https://gematik.de/vsd/v1"),
                            type = listOf("InsuranceCertificate"),
                        )
                    )
                )
            )
        )
        return true
    }

    suspend fun handlePresentationSubmit(
        protocolInstance: PresentationExchangeVerifierProtocol,
        presentationSubmit: PresentationSubmit
    ): Boolean {
        if (!presentationSubmit.presentation.verifiableCredential.get(0).type.contains("InsuranceCertificate")) {
            logger.info { "invalid credential subject type: ${presentationSubmit.presentation.verifiableCredential.get(0).type} but \"InsuranceCertificate\" was expected" }
            return false
        }
        val insurance =
            presentationSubmit.presentation.verifiableCredential.get(0).credentialSubject?.toObject<Insurance>()
                ?: run {
                    logger.info { "invalid credential subject" }
                    return false
                }
        val patient = patients.find {
            it?.insurance?.insurant?.insurantId == insurance.insurant.insurantId
        } ?: patients.find {
            (it.name == insurance.insurant.familyName) &&
                    (it.givenName == insurance.insurant.givenName) &&
                    (it.birthDate == insurance.insurant.birthDate)
        }

        if (patient == null) { // new patient
            patients.add(
                Patient(
                    name = insurance.insurant.familyName,
                    givenName = insurance.insurant.givenName,
                    birthDate = insurance.insurant.birthDate ?: "---",
                    gender = insurance.insurant.gender,
                    insurance = insurance,
                    insuranceLastStatusCheck = ZonedDateTime.now().toIsoInstantString()
                )
            )
        } else { // update existing patient{
            patient.insurance = insurance
            patient.insuranceLastStatusCheck = ZonedDateTime.now().toIsoInstantString()
        }

        // inform the browser to update the patient page
        patientsDataStatus.update = true

        return false
    }


}

