package violett.pro.cvchat.domain.usecases.bd.message

import kotlinx.coroutines.flow.first
import violett.pro.cvchat.domain.model.Message
import violett.pro.cvchat.domain.model.errors.RoomError
import violett.pro.cvchat.domain.repo.MessageRepo
import violett.pro.cvchat.domain.util.CustomResult

class GetChatMessagesUseCase(
    private val repository: MessageRepo
) {
    suspend operator fun invoke(
        contactId: String, 
        limit: Int = 150, 
        offset: Int = 0
    ): CustomResult<List<Message>, RoomError> {
        return try {
            val entities = repository.getMessages(contactId, limit, offset).first()
            val messages = entities.map { it.toDomain() }
            CustomResult.Success(messages)
        } catch (e: Exception) {
            CustomResult.Failure(RoomError.Unknown(e.message ?: "Error fetching messages"))
        }
    }
}