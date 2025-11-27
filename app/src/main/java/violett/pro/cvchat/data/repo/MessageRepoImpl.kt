package violett.pro.cvchat.data.repo

import kotlinx.coroutines.flow.Flow
import violett.pro.cvchat.data.local.dao.MessageDao
import violett.pro.cvchat.data.local.entity.MessageEntity
import violett.pro.cvchat.domain.repo.MessageRepo

class MessageRepoImpl(
    private val messageDao: MessageDao
) : MessageRepo {

    override fun getMessages(
        contactId: String,
        limit: Int,
        offset: Int
    ): Flow<List<MessageEntity>> {
        return messageDao.getMessagesForContact(contactId, limit, offset)
    }

    override suspend fun saveMessage(message: MessageEntity) {
        messageDao.insertMessage(message)
    }

    override suspend fun updateMessageStatus(msgId: String, status: String) {
        messageDao.updateStatus(msgId, status)
    }
}