package violett.pro.cvchat.domain.model


data class Contact(
    val publicKey: String,
    val name: String = "",
    val tempId: String,
)