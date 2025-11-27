package violett.pro.cvchat.domain.usecases.bd.message

import violett.pro.cvchat.data.local.entity.toEntity
import violett.pro.cvchat.domain.model.Message
import violett.pro.cvchat.domain.model.errors.RoomError
import violett.pro.cvchat.domain.repo.MessageRepo
import violett.pro.cvchat.domain.util.CustomResult

class SaveMessageUseCase(
    private val repository: MessageRepo
) {
    suspend operator fun invoke(message: Message): CustomResult<Unit, RoomError> {
        return try {
            repository.saveMessage(message.toEntity())
            CustomResult.Success(Unit)
        } catch (e: Exception) {
            CustomResult.Failure(RoomError.WriteError(e.message ?: "Error saving message"))
        }
    }
}