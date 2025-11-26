package violett.pro.cvchat.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import violett.pro.cvchat.domain.model.Message
import violett.pro.cvchat.domain.model.MessageStatus
import java.util.UUID

@Entity(
    tableName = "messages",
    foreignKeys = [
        ForeignKey(
            entity = ContactEntity::class,
            parentColumns = ["tempId"],
            childColumns = ["contactId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["contactId"])]
)
data class MessageEntity(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val contactId: String,
    val content: String,
    val timestamp: Long,
    val isMine: Boolean,
    val status: String
) {
    fun toDomain(): Message {
        return Message(
            id = id,
            contactId = contactId,
            content = content,
            timestamp = timestamp,
            isMine = isMine,
            status = try {
                MessageStatus.valueOf(status)
            } catch (e: Exception) {
                MessageStatus.FAILED
            }
        )
    }
}

fun Message.toEntity(): MessageEntity {
    return MessageEntity(
        id = this.id.ifBlank { UUID.randomUUID().toString() },
        contactId = this.contactId,
        content = this.content,
        timestamp = this.timestamp,
        isMine = this.isMine,
        status = this.status.name
    )
}