package violett.pro.cvchat.domain.model.websocket

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ClientMessage(
    val messageId: String,
    val to: String,
    val payload: String
)

@Serializable
sealed interface ServerResponse

@Serializable
@SerialName("message")
data class ReceivedMessage(
    val from: String,
    val payload: String
) : ServerResponse

@Serializable
@SerialName("status")
data class MessageStatusUpdate(
    val messageId: String,
    val status: String
) : ServerResponse