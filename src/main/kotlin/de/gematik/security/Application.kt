package de.gematik.security

import de.gematik.security.credentialExchangeLib.crypto.KeyPair
import de.gematik.security.credentialExchangeLib.crypto.bbs.BbsCryptoCredentials
import de.gematik.security.credentialExchangeLib.extensions.hexToByteArray
import java.net.InetAddress

fun main() {
    de.gematik.security.insurance.Controller.start()
    de.gematik.security.medicaloffice.Controller.start(wait = true)
}

val localIpAddress = InetAddress.getLocalHost().hostAddress

val credentialIssuer = BbsCryptoCredentials(
    KeyPair(
        "4b72cad121e0459dce3c5ead7683e82185459a77ac33a9bcd84423c36683acf5".hexToByteArray(),
        "9642f47f8f970fe5a36f67d74841cf0885141ccc8eae92685b4dbda5891b42ab132ab0b8c8df8ec11316bdddddbed330179ca7dc7c6dbbd7bf74584831087bb9884d504a76afd4d8f03c14c1e6acccb7bf76b4e2068725456f65fca1bdc184b5".hexToByteArray()
    )
)
