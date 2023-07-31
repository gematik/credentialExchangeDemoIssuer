package de.gematik.security.insurance

import de.gematik.security.credentialExchangeLib.credentialSubjects.Gender
import de.gematik.security.credentialExchangeLib.credentialSubjects.InsuranceType
import de.gematik.security.credentialExchangeLib.credentialSubjects.ResidencyPrinciple
import de.gematik.security.credentialExchangeLib.extensions.Utils
import java.util.*
import java.util.concurrent.atomic.AtomicInteger

data class Insurant(
    var name: String,
    var givenName: String,
    var birthDate: Date,
    var gender: Gender,
    var email: String? = null,
    var insurance: Insurance?=null
) {
    val id = idCounter.getAndIncrement()
    companion object {
        private val idCounter = AtomicInteger()
    }
}

val insurants = Collections.synchronizedList(
    mutableListOf(
        Insurant(
            "Mustermann",
            "Max",
            birthDate = Utils.getDate(1965, 5, 4),
            gender = Gender.Male,
            email = "gem.teclab1@gmail.de",
            insurance = Insurance(
                insurantId = "X110403567",
                streetAddress = "Kastanienalle 231 10113 Berlin GER",
                costCenter = "Health-Insurance-North GER 109500969",
                insuranceType = InsuranceType.Member,
                residencyPrinciple = ResidencyPrinciple.Berlin,
                start = Utils.getDate(2001, 5, 3)
            )
        ),
        Insurant(
            "Mustermann",
            "Erika",
            birthDate = Utils.getDate(1967, 11, 25),
            email = "emu@online.de",
            gender = Gender.Female
        ),
        Insurant(
            "Doe",
            "John",
            birthDate = Utils.getDate(1935, 6, 22),
            email = "john.doe@aol.com",
            gender = Gender.Male
        ),
        Insurant(
            "Roe",
            "Jane",
            birthDate = Utils.getDate(1934, 10, 13),
            email = "jr001@gmail.com",
            gender = Gender.Female,
            insurance = Insurance(
                insurantId = "X110403566",
                streetAddress = "Dorfstrasse 1 10176 Berlin GER",
                costCenter = "Health-Insurance-North GER 109500969",
                insuranceType = InsuranceType.Member,
                residencyPrinciple = ResidencyPrinciple.Berlin,
                start = Utils.getDate(1993, 10, 7)
            )
        )
    )
)
