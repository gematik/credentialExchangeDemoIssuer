package de.gematik.security.insurance

import de.gematik.security.credentialExchangeLib.credentialSubjects.*
import de.gematik.security.credentialExchangeLib.extensions.Utils
import de.gematik.security.credentialExchangeLib.protocols.GoalCode
import de.gematik.security.credentialExchangeLib.protocols.Invitation
import de.gematik.security.credentialExchangeLib.protocols.Service
import de.gematik.security.hostName
import java.net.URI
import java.util.*
import java.util.concurrent.atomic.AtomicInteger

data class Customer(
    var name: String,
    var givenName: String,
    var birthDate: Date,
    var gender: Gender,
    var email: String? = null,
    var insurance: Insurance? = null,
    val invitation: Invitation = Invitation(
        UUID.randomUUID().toString(),
        label = "Health Insurance North",
        goal = "Issue Insurance Certificates",
        goalCode = GoalCode.OFFER_CREDENDIAL,
        service = listOf(
            Service(
                serviceEndpoint = URI("ws://$hostName:${Controller.port}")
            )
        )
    )
) {
    val id = idCounter.getAndIncrement()

    companion object {
        private val idCounter = AtomicInteger()
    }
}

val costCenter = CostCenter(
    109500969,
    "GER",
    "Health-Insurance-North"
)


val customers = Collections.synchronizedList(
    mutableListOf(
        Customer(
            "Mustermann",
            "Max",
            birthDate = Utils.getDate(1965, 5, 4),
            gender = Gender.Male,
            email = "gem.teclab1@gmail.de",
            insurance = Insurance(
                insurant = Insurant(
                    insurantId = "X110403567",
                    familyName = "Mustermann",
                    givenName = "Max",
                    birthDate = Utils.getDate(1965, 5, 4),
                    gender = Gender.Male,
                    streetAddress = StreetAddress(
                        10113,
                        "Berlin",
                        "Kastanienalle",
                        "231",
                        "GER"
                    ),
                    postBoxAddress = PostBoxAddress(
                        10113,
                        "Berlin",
                        "12234",
                        "GER"
                    )

                ),
                coverage = Coverage(
                    start = Utils.getDate(2001, 5, 3),
                    residencyPrinciple = ResidencyPrinciple.Berlin,
                    insuranceType = InsuranceType.Member,
                    costCenter = costCenter,
                    dmpMark = DmpMark.CHD_CoronaryHeartDisease,
                    coPayment = CoPayment(
                        status = true,
                        validUntil = Utils.getDate(2024, 3, 2)
                    ),
                    reimbursement = Reimbursement(
                        medicalCare = true,
                        dentalCare = true,
                        inpatientSector = true,
                        initiatedServices = false
                    ),
                    selectiveContracts = SelectiveContracts(
                        medical = SelectiveContractStatus.available,
                        dental = SelectiveContractStatus.notUsed,
                        contractType = ContractType(
                            generalPractionerCare = true,
                            structuredTreatmentProgram = false,
                            integratedCare = false
                        )
                    ),
                    dormantBenefitsEntitlement = DormantBenefitsEntitlement(
                        start = Utils.getDate(2023, 1, 1),
                        end = Utils.getDate(2025, 12, 31),
                        dormancyType = DormancyType.complete
                    )
                )
            )
        ),
        Customer(
            "Mustermann",
            "Erika",
            birthDate = Utils.getDate(1967, 11, 25),
            email = "emu@online.de",
            gender = Gender.Female,
            insurance = Insurance(
                insurant = Insurant(
                    insurantId = "X110403567",
                    familyName = "Mustermann",
                    givenName = "Erika",
                    birthDate = Utils.getDate(1968, 4, 3),
                    gender = Gender.Female,
                    streetAddress = StreetAddress(
                        10112,
                        "Berlin",
                        "Max und Moritz Strasse",
                        "13a",
                        "GER"
                    )
                ),
                coverage = Coverage(
                    start = Utils.getDate(2007, 1, 1),
                    residencyPrinciple = ResidencyPrinciple.Berlin,
                    insuranceType = InsuranceType.Member,
                    costCenter = costCenter
                )
            )

        ),
        Customer(
            "Doe",
            "John",
            birthDate = Utils.getDate(1935, 6, 22),
            email = "john.doe@aol.com",
            gender = Gender.Male
        ),
        Customer(
            "Roe",
            "Jane",
            birthDate = Utils.getDate(1934, 10, 13),
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
                        10176,
                        "Berlin",
                        "Dorfstrasse",
                        "1",
                        "GER"
                    )
                ),
                coverage = Coverage(
                    start = Utils.getDate(1993, 10, 7),
                    residencyPrinciple = ResidencyPrinciple.Berlin,
                    insuranceType = InsuranceType.Member,
                    costCenter = costCenter,
                    dmpMark = DmpMark.ChronicCardiacInsufficiency,
                )
            )
        )
    )
)
