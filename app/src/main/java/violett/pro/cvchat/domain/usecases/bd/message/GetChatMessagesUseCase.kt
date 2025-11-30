package violett.pro.cvchat.domain.usecases.bd.message

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import violett.pro.cvchat.domain.model.Message
import violett.pro.cvchat.domain.model.errors.RoomError
import violett.pro.cvchat.domain.repo.MessageRepo
import violett.pro.cvchat.domain.util.CustomResult

class GetChatMessagesUseCase(
    private val repository: MessageRepo
) {
    operator fun invoke(
        contactId: String,
        limit: Int = 150,
        offset: Int = 0
    ): Flow<List<Message>> {
        return repository.getMessages(contactId, limit, offset)
            .map { list ->
                list.map { it.toDomain() }
            }
    }
}