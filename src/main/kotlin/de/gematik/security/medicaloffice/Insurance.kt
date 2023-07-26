package de.gematik.security.medicaloffice

import de.gematik.security.credentialExchangeLib.credentialSubjects.InsuranceType
import de.gematik.security.credentialExchangeLib.credentialSubjects.ResidencyPrinciple
import de.gematik.security.credentialExchangeLib.serializer.DateSerializer
import kotlinx.serialization.Serializable
import java.util.*

@Serializable
data class Insurance(
    val insurantId: String,
    val streetAddress: String,
    val costCenter: String,
    val insuranceType: InsuranceType?,
    val residencyPrinciple: ResidencyPrinciple?,
    val start: @Serializable(with = DateSerializer::class) Date?,
    val lastStatusCheck: @Serializable(with = DateSerializer::class) Date
)
