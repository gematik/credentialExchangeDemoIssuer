package de.gematik.security.insurance

import de.gematik.security.PreferredContact
import de.gematik.security.credentialExchangeLib.connection.Invitation
import de.gematik.security.credentialExchangeLib.credentialSubjects.*
import de.gematik.security.credentialExchangeLib.extensions.getZonedDate
import de.gematik.security.credentialExchangeLib.extensions.toIsoInstantString
import de.gematik.security.credentialExchangeLib.protocols.GoalCode
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
    val invitationId: UUID
) {
    val id = idCounter.getAndIncrement()
    val invitation = Invitation(
        id = invitationId.toString(),
        label = "Health Insurance North",
        goal = "Issue Insurance Certificates",
        goalCode = GoalCode.OFFER_CREDENDIAL,
        from = URI("ws://$hostName:${Controller.port}")
    )

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
            ),
            invitationId = UUID.fromString("ede563a1-215c-45c2-9afb-1c3d36245cf4")
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
                    ),
                    portrait = object {}.javaClass.getResourceAsStream("/files/erika-mustermann.jpg")?.readBytes()?.let {
                        Base64.getEncoder().encodeToString(it)
                    }
                ),
                coverage = Coverage(
                    start = getZonedDate(2007, 1, 1).toIsoInstantString(),
                    residencyPrinciple = ResidencyPrinciple.Berlin,
                    insuranceType = InsuranceType.Member,
                    costCenter = costCenter
                )
            ),
            invitationId = UUID.fromString("083d2a47-1d96-40bf-83ad-bec0aa04710d")
        ),
        Customer(
            "Doe",
            "John",
            birthDate = getZonedDate(1935, 6, 22).toIsoInstantString(),
            email = "john.doe@aol.com",
            gender = Gender.Male,
            invitationId = UUID.fromString("39127d73-2bca-44e3-af84-1deb1d6ed234")
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
            ),
            invitationId = UUID.fromString("e123cdb3-5a0d-4164-9f7b-072442ee67db")
        )
    )
)
