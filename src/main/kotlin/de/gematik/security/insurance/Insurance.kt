package de.gematik.security.insurance

import de.gematik.security.credentialExchangeLib.credentialSubjects.InsuranceType
import de.gematik.security.credentialExchangeLib.credentialSubjects.ResidencyPrinciple
import de.gematik.security.credentialExchangeLib.protocols.GoalCode
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
    val invitation: Invitation = Invitation(
        UUID.randomUUID().toString(),
        label = "Health Insurance North",
        goal = "Issue Insurance Certificates",
        goalCode = GoalCode.OFFER_CREDENDIAL,
        service = listOf(
            Service(
                serviceEndpoint = URI("ws://$localIpAddress:${de.gematik.security.insurance.Controller.port}")
            )
        )
    )
)
