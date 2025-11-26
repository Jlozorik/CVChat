package violett.pro.cvchat.data.remote.model

import kotlinx.serialization.Serializable

@Serializable
data class PublicKeyRequestDto(
    val publicKey: String,
    val tempId: String
)