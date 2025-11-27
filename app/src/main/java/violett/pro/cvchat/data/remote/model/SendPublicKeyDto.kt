package violett.pro.cvchat.data.remote.model

import kotlinx.serialization.Serializable

@Serializable
data class SendPublicKeyRequestDto(
    val publicKey: String,
    val tempId: String
)