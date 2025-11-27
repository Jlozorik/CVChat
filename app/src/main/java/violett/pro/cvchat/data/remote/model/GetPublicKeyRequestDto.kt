package violett.pro.cvchat.data.remote.model

import kotlinx.serialization.Serializable

@Serializable
data class GetPublicKeyRequestDto(
    val publicKey: String,
)