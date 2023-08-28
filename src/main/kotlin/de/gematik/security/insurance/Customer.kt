package de.gematik.security.insurance

import de.gematik.security.PreferredContact
import de.gematik.security.credentialExchangeLib.credentialSubjects.*
import de.gematik.security.credentialExchangeLib.extensions.getZonedDate
import de.gematik.security.credentialExchangeLib.extensions.toIsoInstantString
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
    var birthDate: String,
    var gender: Gender,
    var phone: String? = null,
    var email: String? = null,
    var preferredContact: PreferredContact = PreferredContact.unknown,
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
            birthDate = getZonedDate(1965, 5, 4).toIsoInstantString(),
            gender = Gender.Male,
            phone = "+49 30 1234234",
            email = "gem.teclab1@gmail.de",
            preferredContact = PreferredContact.email,
            insurance = Insurance(
                insurant = Insurant(
                    insurantId = "X110403567",
                    familyName = "Mustermann",
                    givenName = "Max",
                    birthDate = getZonedDate(1965, 5, 4).toIsoInstantString(),
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
                    start = getZonedDate(2001, 5, 3).toIsoInstantString(),
                    residencyPrinciple = ResidencyPrinciple.Berlin,
                    insuranceType = InsuranceType.Member,
                    costCenter = costCenter,
                    dmpMark = DmpMark.CHD_CoronaryHeartDisease,
                    coPayment = CoPayment(
                        status = true,
                        validUntil = getZonedDate(2024, 3, 2).toIsoInstantString()
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
                        start = getZonedDate(2023, 1, 1).toIsoInstantString(),
                        end = getZonedDate(2025, 12, 31).toIsoInstantString(),
                        dormancyType = DormancyType.complete
                    )
                )
            )
        ),
        Customer(
            "Mustermann",
            "Erika",
            birthDate = getZonedDate(1967, 11, 25).toIsoInstantString(),
            email = "emu@online.de",
            gender = Gender.Female,
            insurance = Insurance(
                insurant = Insurant(
                    insurantId = "X110403567",
                    familyName = "Mustermann",
                    givenName = "Erika",
                    birthDate = getZonedDate(1968, 4, 3).toIsoInstantString(),
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
                    start = getZonedDate(2007, 1, 1).toIsoInstantString(),
                    residencyPrinciple = ResidencyPrinciple.Berlin,
                    insuranceType = InsuranceType.Member,
                    costCenter = costCenter
                )
            )

        ),
        Customer(
            "Doe",
            "John",
            birthDate = getZonedDate(1935, 6, 22).toIsoInstantString(),
            email = "john.doe@aol.com",
            gender = Gender.Male
        ),
        Customer(
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
                        10176,
                        "Berlin",
                        "Dorfstrasse",
                        "1",
                        "GER"
                    )
                ),
                coverage = Coverage(
                    start = getZonedDate(1993, 10, 7).toIsoInstantString(),
                    residencyPrinciple = ResidencyPrinciple.Berlin,
                    insuranceType = InsuranceType.Member,
                    costCenter = costCenter,
                    dmpMark = DmpMark.ChronicCardiacInsufficiency,
                )
            )
        )
    )
)
