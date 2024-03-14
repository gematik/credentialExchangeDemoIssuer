/*
 * Copyright 2021-2024, gematik GmbH
 *
 * Licensed under the EUPL, Version 1.2 or - as soon they will be approved by the
 * European Commission – subsequent versions of the EUPL (the "Licence").
 * You may not use this work except in compliance with the Licence.
 *
 * You find a copy of the Licence in the "Licence" file or at
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either expressed or implied.
 * In case of changes by gematik find details in the "Readme" file.
 *
 * See the Licence for the specific language governing permissions and limitations under the Licence.
 */

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
