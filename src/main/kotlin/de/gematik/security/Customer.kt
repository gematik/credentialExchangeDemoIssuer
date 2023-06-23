package de.gematik.security

import java.util.*
import java.util.concurrent.atomic.AtomicInteger

class Customer(
    var name: String,
    var givenName: String,
    var birthDate: Date,
    var email: String? = null
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
            birthDate = Calendar.getInstance(Locale.US).apply { set(1965, 5, 4) }.time
        ),
        Customer(
            "Mustermann",
            "Erika",
            birthDate = Calendar.getInstance(Locale.US).apply { set(1967, 11, 25) }.time,
            email = "emu@online.de"
        ),
        Customer(
            "Doe",
            "John",
            birthDate = Calendar.getInstance(Locale.US).apply { set(1935, 6, 22) }.time,
            email = "john.doe@aol.com"
        ),
        Customer(
            "Roe",
            "Jane",
            birthDate = Calendar.getInstance(Locale.US).apply { set(1934, 10, 13) }.time,
            email = "jr001@gmail.com"
        )
    )
)
