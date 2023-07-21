package de.gematik.security.medicaloffice

import de.gematik.security.credentialExchangeLib.credentialSubjects.Gender
import de.gematik.security.credentialExchangeLib.credentialSubjects.InsuranceType
import de.gematik.security.credentialExchangeLib.credentialSubjects.ResidencyPrinciple
import de.gematik.security.credentialExchangeLib.extensions.Utils
import java.util.*
import java.util.concurrent.atomic.AtomicInteger

data class Patient(
    var name: String,
    var givenName: String,
    var birthDate: Date,
    var gender: Gender,
    var email: String? = null,
    var insurance: Insurance? = null,
    var vaccinations: MutableList<Vaccination> = mutableListOf(),
) {
    val id = idCounter.getAndIncrement()

    companion object {
        private val idCounter = AtomicInteger()
    }
}

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
                insurantId = "X110403566",
                streetAddress = "Dorfstrasse 1 10176 Berlin GER",
                costCenter = "Test_GKV-SV GER 109500969",
                insuranceType = InsuranceType.Member,
                residencyPrinciple = ResidencyPrinciple.Berlin,
                start = Utils.getDate(1993, 10, 7)
            ),
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
