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

import de.gematik.security.PreferredContact
import de.gematik.security.credentialExchangeLib.credentialSubjects.*
import de.gematik.security.credentialExchangeLib.extensions.getZonedDate
import de.gematik.security.credentialExchangeLib.extensions.toIsoInstantString
import kotlinx.serialization.EncodeDefault
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import java.time.ZonedDateTime
import java.util.*
import java.util.concurrent.atomic.AtomicInteger

data class Patient(
    var name: String,
    var givenName: String,
    var birthDate: String,
    var gender: Gender,
    var phone: String? = null,
    var email: String? = null,
    var preferredContact : PreferredContact = PreferredContact.unknown,
    var insurance: Insurance? = null,
    var insuranceLastStatusCheck: String? = null,
    var vaccinations: MutableList<Vaccination> = mutableListOf()
) {
    val id = idCounter.getAndIncrement()

    companion object {
        private val idCounter = AtomicInteger()
    }
}

@Serializable
data class Status(
    @OptIn(ExperimentalSerializationApi::class)
    @EncodeDefault
    var update: Boolean = false
)

val patientsDataStatus = Status()

val patients = Collections.synchronizedList(
    mutableListOf(
        Patient(
            "Mustermann",
            "Max",
            birthDate = getZonedDate(1965, 5, 4).toIsoInstantString(),
            gender = Gender.Male,
            email = "gem.teclab1@gmail.de",
            vaccinations = mutableListOf(
                Vaccination(
                    dateOfVaccination = ZonedDateTime.now().minusYears(1).toIsoInstantString(),
                    batchNumber = "65493",
                    vaccine = AuthorizedVaccine.Spikevax,
                    order = 1
                )
            )
        ),
        Patient(
            "Roe",
            "Jane",
            birthDate = getZonedDate(1934, 10, 13).toIsoInstantString(),
            email = "jr001@gmail.com",
            gender = Gender.Female,
            insurance = Insurance(
                insurant = Insurant(
                    insurantId = "X110403566",
                    familyName = "Roe",
                    givenName = "Jane",
                    birthDate = getZonedDate(1934, 10, 13).toIsoInstantString(),
                    gender = Gender.Female,
                    streetAddress = StreetAddress(
                        street = "Dorfstrasse",
                        streetNumber = "1",
                        location = "Berlin",
                        postalCode = 10176,
                        country = "GER"
                    )
                ),
                coverage = Coverage(
                    insuranceType = InsuranceType.Member,
                    costCenter = CostCenter(
                        identification = 109500969,
                        name = "Test_GKV-SV",
                        countryCode = "GER"
                    ),
                    start = getZonedDate(1993, 10, 7).toIsoInstantString(),
                    residencyPrinciple = ResidencyPrinciple.Berlin,

                )
            ),
            insuranceLastStatusCheck = getZonedDate(2022, 10, 4).toIsoInstantString(),
            vaccinations = mutableListOf(
                Vaccination(
                    dateOfVaccination = ZonedDateTime.now().minusYears(1).toIsoInstantString(),
                    batchNumber = "1554dfr",
                    vaccine = AuthorizedVaccine.Spikevax,
                    order = 1
                ),
                Vaccination(
                    dateOfVaccination = ZonedDateTime.now().minusMonths(6).toIsoInstantString(),
                    batchNumber = "44c37",
                    vaccine = AuthorizedVaccine.Comirnaty,
                    order = 2
                ),
                Vaccination(batchNumber = "134acd", vaccine = AuthorizedVaccine.Comirnaty, order = 3)
            )
        )
    )
)
