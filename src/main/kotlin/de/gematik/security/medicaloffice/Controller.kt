package de.gematik.security.medicaloffice

import de.gematik.security.credentialExchangeLib.connection.MessageType
import de.gematik.security.credentialExchangeLib.connection.WsConnection
import de.gematik.security.credentialExchangeLib.credentialSubjects.Insurance
import de.gematik.security.credentialExchangeLib.credentialSubjects.Recipient
import de.gematik.security.credentialExchangeLib.credentialSubjects.VaccinationEvent
import de.gematik.security.credentialExchangeLib.credentialSubjects.Vaccine
import de.gematik.security.credentialExchangeLib.crypto.BbsPlusSigner
import de.gematik.security.credentialExchangeLib.crypto.ProofType
import de.gematik.security.credentialExchangeLib.json
import de.gematik.security.credentialExchangeLib.protocols.*
import de.gematik.security.credentialIssuer
import de.gematik.security.insurance.Controller
import de.gematik.security.localIpAddress
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.websocket.*
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.encodeToJsonElement
import kotlinx.serialization.json.jsonObject
import mu.KotlinLogging
import java.net.URI
import java.util.*

object Controller {

    private val logger = KotlinLogging.logger {}

    private val sixMonth = 1000L * 60 * 60 * 24 * 180

    val port = 8090

    var lastCallingRemoteAddress : String? = null

    fun start(wait: Boolean = false) {
        WsConnection.listen(host = localIpAddress, port = port) {
            while (true) {
                lastCallingRemoteAddress = (it.session as DefaultWebSocketServerSession).call.request.local.remoteAddress
                val message = it.receive()
                if (message.type == MessageType.CLOSE) break
                if (!(message.type == MessageType.INVITATION_ACCEPT)) continue
                val invitation = json.decodeFromJsonElement<Invitation>(message.content)
                if (invitation.goalCode == GoalCode.REQUEST_PRESENTATION) {
                    PresentationExchangeVerifierProtocol.bind(it, invitation) {
                        // request insurance credential
                        if (handleInvitation(it, invitation)) {
                            while (true) {
                                if (!handleIncomingMessage(it)) break
                            }
                        }
                    }
                } else {
                    CredentialExchangeIssuerProtocol.bind(it, invitation) {
                        if (handleInvitation(it, invitation)) {
                            while (true) {
                                if (!handleIncomingMessage(it)) break
                            }
                        }
                    }
                }
                break
            }
        }
        embeddedServer(Netty, host = localIpAddress, port = 8081) {
            configureTemplating()
            configureRouting()
        }.start(wait)
    }

    // issue credential

    suspend fun handleIncomingMessage(protocolInstance: CredentialExchangeIssuerProtocol): Boolean {
        val message = protocolInstance.receive()
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
                    birthDate = patient.birthDate,
                    familyName = patient.name,
                    givenName = patient.givenName,
                    gender = patient.gender.name
                ).apply {
                        id = URI.create(message.holderKey)
                } ,
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
        val type = message.type ?: return true //ignore
        return when {
            type.contains("Close") -> false // close connection
            type.contains("PresentationSubmit") -> handlePresentationSubmit(
                protocolInstance,
                message as PresentationSubmit
            )

            else -> true //ignore
        }
    }

    suspend fun handleInvitation(
        protocolInstance: PresentationExchangeVerifierProtocol,
        invitation: Invitation
    ): Boolean {
        protocolInstance.requestPresentation(
            PresentationRequest(
                inputDescriptor = Descriptor(
                    UUID.randomUUID().toString(),
                    Credential(
                        atContext = Credential.DEFAULT_JSONLD_CONTEXTS + URI("https://gematik.de/vsd/v1"),
                        type = Credential.DEFAULT_JSONLD_TYPES + "InsuranceCertificate"
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
        if (!presentationSubmit.presentation.verifiableCredential.get(0).type.contains("InsuranceCertificate")) return false
        val insurance =
            json.decodeFromJsonElement<Insurance>(presentationSubmit.presentation.verifiableCredential.get(0).credentialSubject!!.jsonObject)
        val patient = patients.find {
            (it.name == insurance.insurant.familyName) &&
                    (it.givenName == insurance.insurant.givenName) &&
                    (it.birthDate == insurance.insurant.birthdate)
        } ?: return false

        patient.insurance = Insurance(
            insurance.insurant.insurantId,
            streetAddress = insurance.insurant.streetAddress?.let {
                "${it.street} ${it.streetNumber} ${it.location} ${it.postalCode} ${it.country}"
            } ?: "",
            costCenter = insurance.coverage?.costCenter?.let {
                "${it.name} ${it.identification} ${it.countryCode}"
            } ?: "",
            insuranceType = insurance.coverage?.insuranceType,
            residencyPrinciple = insurance.coverage?.residencyPrinciple,
            start = insurance.coverage?.start,
            lastStatusCheck = Date()
        )

        return false
    }


}

