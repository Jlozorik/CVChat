package violett.pro.cvchat.domain.model

import javax.crypto.SecretKey


data class Contact(
    val publicKey: String,
    val name: String = "",
    val tempId: String,
    val sharedSecret: SecretKey? = null
)