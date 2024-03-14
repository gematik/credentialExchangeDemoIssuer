/*
 * Copyright 2021-2024, gematik GmbH
 *
 * Licensed under the EUPL, Version 1.2 or - as soon they will be approved by the
 * European Commission – subsequent versions of the EUPL (the "Licence").
 * You may not use this work except in compliance with the Licence.
 *
 * You find a copy of the Licence in the "Licence" file or at
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either expressed or implied.
 * In case of changes by gematik find details in the "Readme" file.
 *
 * See the Licence for the specific language governing permissions and limitations under the Licence.
 */

package de.gematik.security

import com.google.zxing.BarcodeFormat
import com.google.zxing.client.j2se.MatrixToImageWriter
import com.google.zxing.qrcode.QRCodeWriter
import de.gematik.security.credentialExchangeLib.connection.Invitation
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

fun String.toDate() = runCatching { sdfDate.parse(this) }.getOrDefault( Date(0))
fun Date.toDateString() = sdfDate.format(this)

fun String.toDateTimeUtc() = sdfDateTimeUtc.parse(this)
fun Date.toDateTimeUtcString() = sdfDateTimeUtc.format(this)

val Invitation.url : String
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