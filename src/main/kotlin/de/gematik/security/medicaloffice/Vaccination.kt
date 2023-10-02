package de.gematik.security.medicaloffice

import de.gematik.security.credentialExchangeLib.connection.Invitation
import de.gematik.security.credentialExchangeLib.extensions.toIsoInstantString
import de.gematik.security.credentialExchangeLib.protocols.GoalCode
import de.gematik.security.hostName
import kotlinx.serialization.Serializable
import java.net.URI
import java.time.ZonedDateTime
import java.util.*

@Serializable
data class VaccineDetails(
    val medicalProductName: String,
    val marketingAuthorizationHolder: String,
    val note: String? = null
)

@Serializable
enum class AuthorizedVaccine(val details: VaccineDetails) {
    Bimervax(
        VaccineDetails(
            "Bimervax",
            "HIPRA Human Health S.L.U.",
            "previously COVID-19 Vaccine HIPRA"
        )
    ),
    Comirnaty(
        VaccineDetails(
            "Comirnaty",
            "BioNTech Manufacturing GmbH",
            "developed by BioNTech and Pfizer"
        )
    ),
    Valneva(
        VaccineDetails(
            "Valneva",
            "Valneva Austria GmbH",
            "COVID-19 Vaccine (inactivated, adjuvanted)"
        )
    ),
    Jcovden(
        VaccineDetails(
            "Jcovden",
            "Janssen-Cilag International NV",
            "previously COVID-19 Vaccine Janssen"
        )
    ),
    Nuvaxovid(
        VaccineDetails(
            "Nuvaxovid",
            "Novavax CZ, a.s."
        )
    ),
    Spikevax(
        VaccineDetails(
            "Spikevax",
            "Moderna Biotech Spain S.L.",
            "previously COVID-19 Vaccine Moderna"
        )
    ),
    Vaxzevria(
        VaccineDetails(
            "Vaxzevria",
            "AstraZeneca AB",
            "previously COVID-19 Vaccine AstraZeneca"
        )
    ),
    VidPrevtyn(
        VaccineDetails(
            "VidPrevtyn",
            "Sanofi Pasteur",
            "Beta"
        )
    ),
}

@Serializable
data class Vaccination(
    val dateOfVaccination: String = ZonedDateTime.now().toIsoInstantString(),
    val atcCode: String = "J07BX03",
    val vaccine: AuthorizedVaccine,
    val batchNumber: String,
    val order: Int,
    val invitation: Invitation = Invitation(
        id = UUID.randomUUID().toString(),
        label = "Praxis Sommergarten",
        goal = "Issue Vaccination Certificate",
        goalCode = GoalCode.OFFER_CREDENDIAL,
        from = URI("ws://$hostName:${de.gematik.security.medicaloffice.Controller.port}")
    )
)
