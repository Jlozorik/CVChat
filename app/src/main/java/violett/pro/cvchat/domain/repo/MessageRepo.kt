package violett.pro.cvchat.domain.repo

import kotlinx.coroutines.flow.Flow
import violett.pro.cvchat.data.local.entity.MessageEntity

interface MessageRepo {
    fun getMessages(contactId: String, limit: Int = 150, offset: Int = 0): Flow<List<MessageEntity>>
    suspend fun saveMessage(message: MessageEntity)
    suspend fun updateMessageStatus(msgId: String, status: String)
}