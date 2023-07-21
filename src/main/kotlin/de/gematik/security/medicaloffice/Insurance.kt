package de.gematik.security.medicaloffice

import de.gematik.security.credentialExchangeLib.credentialSubjects.InsuranceType
import de.gematik.security.credentialExchangeLib.credentialSubjects.ResidencyPrinciple
import de.gematik.security.credentialExchangeLib.protocols.Invitation
import de.gematik.security.credentialExchangeLib.protocols.Service
import de.gematik.security.credentialExchangeLib.serializer.DateSerializer
import de.gematik.security.localIpAddress
import kotlinx.serialization.Serializable
import java.net.URI
import java.util.*

@Serializable
data class Insurance(
    val insurantId: String,
    val streetAddress: String,
    val costCenter: String,
    val insuranceType: InsuranceType,
    val residencyPrinciple: ResidencyPrinciple,
    val start: @Serializable(with = DateSerializer::class) Date,
)
