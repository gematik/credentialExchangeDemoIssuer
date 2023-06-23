package de.gematik.security

import com.google.zxing.BarcodeFormat
import com.google.zxing.client.j2se.MatrixToImageWriter
import com.google.zxing.qrcode.QRCodeWriter
import de.gematik.security.credentialExchangeLib.connection.WsConnection
import de.gematik.security.credentialExchangeLib.protocols.*
import de.gematik.security.plugins.configureRouting
import de.gematik.security.plugins.configureTemplating
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import kotlinx.coroutines.runBlocking
import java.io.ByteArrayOutputStream
import java.net.URI
import java.util.*
import javax.imageio.ImageIO

fun main() {
    CredentialExchangeIssuer.listen(WsConnection) {
        while (true) {
            val request = it.receive()
            val type = request.type ?: continue
            when {
                type.contains("Close") -> break
                type.contains("Invitation") -> {
                    it.sendOffer(
                        CredentialOffer(
                            UUID.randomUUID().toString(),
                            outputDescriptor = Credential(
                                atContext = Credential.DEFAULT_JSONLD_CONTEXTS + URI("https://w3id.org/vaccination/v1"),
                                type = Credential.DEFAULT_JSONLD_TYPES + "VaccinationCertificate"
                            )
                        )
                    )
                }
            }
        }
    }
    embeddedServer(Netty, port = 8080, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

fun Application.module() {
    configureTemplating()
    configureRouting()
}

val invitation = Invitation(
    UUID.randomUUID().toString(),
    label = "credential issuer",
    service = listOf(
        Service(
            serviceEndpoint = URI("ws://127.0.0.1:8090/ws")
        )
    )
)

object QrCodeInvitation {
    val url = "ws://127.0.0.1:8090/ws?oob=${invitation.toBase64()}"
    val qrCode = Base64.getEncoder().encodeToString(QRCodeWriter().encode(
        "ws://127.0.0.1:8090/ws?oob=${invitation.toBase64()}",
        BarcodeFormat.QR_CODE,
        256,
        256
    ).let
    {
        MatrixToImageWriter.toBufferedImage(it).let {
            ByteArrayOutputStream().apply {
                ImageIO.write(it, "png", this)
            }.toByteArray()
        }
    })
}
