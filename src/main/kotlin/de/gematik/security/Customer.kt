package de.gematik.security

import de.gematik.security.credentialExchangeLib.protocols.Invitation
import de.gematik.security.credentialExchangeLib.protocols.Service
import java.net.URI
import java.util.*
import java.util.concurrent.atomic.AtomicInteger

enum class Gender {
    Male,
    Female,
    Uni
}

data class Customer(
    var name: String,
    var givenName: String,
    var birthDate: Date,
    var gender: Gender,
    var email: String? = null,
    var vaccinations: MutableList<Vaccination> = mutableListOf()
) {
    val id = idCounter.getAndIncrement()
    companion object {
        private val idCounter = AtomicInteger()
    }
}

val customers = Collections.synchronizedList(
    mutableListOf(
        Customer(
            "Mustermann",
            "Max",
            birthDate = Calendar.getInstance(Locale.US).apply { set(1965, 5, 4) }.time,
            gender = Gender.Male
        ),
        Customer(
            "Mustermann",
            "Erika",
            birthDate = Calendar.getInstance(Locale.US).apply { set(1967, 11, 25) }.time,
            email = "emu@online.de",
            gender = Gender.Female
        ),
        Customer(
            "Doe",
            "John",
            birthDate = Calendar.getInstance(Locale.US).apply { set(1935, 6, 22) }.time,
            email = "john.doe@aol.com",
            gender = Gender.Male
        ),
        Customer(
            "Roe",
            "Jane",
            birthDate = Calendar.getInstance(Locale.US).apply { set(1934, 10, 13) }.time,
            email = "jr001@gmail.com",
            gender = Gender.Female,
            vaccinations = mutableListOf(
                Vaccination(dateOfVaccination = Date(System.currentTimeMillis() - 1000L*60*60*24*360), batchNumber = "1554dfr", vaccine = AuthorizedVaccine.Spikevax, order = 1),
                Vaccination(dateOfVaccination = Date(System.currentTimeMillis() - 1000L*60*60*24*180), batchNumber = "44c37", vaccine = AuthorizedVaccine.Comirnaty, order = 2),
                Vaccination(batchNumber = "134acd", vaccine = AuthorizedVaccine.Comirnaty, order = 3)
            )
        )
    )
)
