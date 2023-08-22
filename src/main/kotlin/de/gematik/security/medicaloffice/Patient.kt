package de.gematik.security.medicaloffice

import de.gematik.security.credentialExchangeLib.credentialSubjects.*
import de.gematik.security.credentialExchangeLib.extensions.Utils
import de.gematik.security.credentialExchangeLib.serializer.DateSerializer
import kotlinx.serialization.EncodeDefault
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import java.util.*
import java.util.concurrent.atomic.AtomicInteger

data class Patient(
    var name: String,
    var givenName: String,
    var birthDate: Date,
    var gender: Gender,
    var email: String? = null,
    var insurance: Insurance? = null,
    var insuranceLastStatusCheck: @Serializable(with = DateSerializer::class) Date? = null,
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
            birthDate = Utils.getDate(1965, 5, 4),
            gender = Gender.Male,
            email = "gem.teclab1@gmail.de",
            vaccinations = mutableListOf(
                Vaccination(
                    dateOfVaccination = Date(System.currentTimeMillis() - 1000L * 60 * 60 * 24 * 360),
                    batchNumber = "65493",
                    vaccine = AuthorizedVaccine.Spikevax,
                    order = 1
                )
            )
        ),
        Patient(
            "Roe",
            "Jane",
            birthDate = Calendar.getInstance(Locale.US).apply { set(1934, 10, 13) }.time,
            email = "jr001@gmail.com",
            gender = Gender.Female,
            insurance = Insurance(
                insurant = Insurant(
                    insurantId = "X110403566",
                    familyName = "Roe",
                    givenName = "Jane",
                    birthDate = Utils.getDate(1934, 10, 13),
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
                    start = Utils.getDate(1993, 10, 7),
                    residencyPrinciple = ResidencyPrinciple.Berlin,

                )
            ),
            insuranceLastStatusCheck = Utils.getDate(2022, 10, 4),
            vaccinations = mutableListOf(
                Vaccination(
                    dateOfVaccination = Date(System.currentTimeMillis() - 1000L * 60 * 60 * 24 * 360),
                    batchNumber = "1554dfr",
                    vaccine = AuthorizedVaccine.Spikevax,
                    order = 1
                ),
                Vaccination(
                    dateOfVaccination = Date(System.currentTimeMillis() - 1000L * 60 * 60 * 24 * 180),
                    batchNumber = "44c37",
                    vaccine = AuthorizedVaccine.Comirnaty,
                    order = 2
                ),
                Vaccination(batchNumber = "134acd", vaccine = AuthorizedVaccine.Comirnaty, order = 3)
            )
        )
    )
)
