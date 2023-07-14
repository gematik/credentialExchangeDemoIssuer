package de.gematik.security

import com.google.zxing.BarcodeFormat
import com.google.zxing.client.j2se.MatrixToImageWriter
import com.google.zxing.qrcode.QRCodeWriter
import de.gematik.security.credentialExchangeLib.protocols.Invitation
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.*
import javax.imageio.ImageIO

private val sdfDate = SimpleDateFormat("yyyy-MM-dd", Locale.US).apply {
    timeZone = TimeZone.getTimeZone("UTC")
}
private val sdfDateTimeUtc = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssX", Locale.US).apply {
    timeZone = TimeZone.getTimeZone("UTC")
}

fun String.toDate() = sdfDate.parse(this)
fun Date.toDateString() = sdfDate.format(this)

fun String.toDateTimeUtc() = sdfDateTimeUtc.parse(this)
fun Date.toDateTimeUtcString() = sdfDateTimeUtc.format(this)

val  Invitation.url : String
    get() = "https://my-wallet.me/ssi?oob=${this.toBase64()}"

val Invitation.qrCode : String
    get() = Base64.getEncoder().encodeToString(
    QRCodeWriter().encode(
        this.url,
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
    }
)