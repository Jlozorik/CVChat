package violett.pro.cvchat.domain.model.websocket

sealed interface ChatSocketEvent {
    data class MessageReceived(val from: String, val payload: String) : ChatSocketEvent
    data class StatusUpdate(val messageId: String, val status: String) : ChatSocketEvent
    data object Connected : ChatSocketEvent
    data class Error(val reason: String) : ChatSocketEvent
}