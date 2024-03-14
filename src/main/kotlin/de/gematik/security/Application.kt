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

import de.gematik.security.credentialExchangeLib.crypto.KeyPair
import de.gematik.security.credentialExchangeLib.crypto.bbs.BbsCryptoCredentials
import de.gematik.security.credentialExchangeLib.extensions.hexToByteArray
import java.net.InetAddress

fun main() {
    de.gematik.security.insurance.Controller.start()
    de.gematik.security.medicaloffice.Controller.start(wait = true)
}

val hostName = InetAddress.getLocalHost().hostName.also {
    println("host name: $it - home page: http://$it:8080")
}

val credentialIssuer = BbsCryptoCredentials(
    KeyPair(
        "4b72cad121e0459dce3c5ead7683e82185459a77ac33a9bcd84423c36683acf5".hexToByteArray(),
        "9642f47f8f970fe5a36f67d74841cf0885141ccc8eae92685b4dbda5891b42ab132ab0b8c8df8ec11316bdddddbed330179ca7dc7c6dbbd7bf74584831087bb9884d504a76afd4d8f03c14c1e6acccb7bf76b4e2068725456f65fca1bdc184b5".hexToByteArray()
    )
)

enum class PreferredContact {
    unknown,
    phone,
    sms,
    email
}
