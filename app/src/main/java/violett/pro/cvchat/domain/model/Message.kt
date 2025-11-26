package violett.pro.cvchat.domain.model

data class Message(
    val id: String = "",
    val contactId: String, // Связь с контактом tempId
    val content: String,
    val timestamp: Long,
    val isMine: Boolean,
    val status: MessageStatus = MessageStatus.SENDING
)

enum class MessageStatus {
    SENDING, SENT, FAILED, PENDING
}