package violett.pro.cvchat.domain.usecases.bd.message

import violett.pro.cvchat.domain.model.MessageStatus
import violett.pro.cvchat.domain.model.errors.RoomError
import violett.pro.cvchat.domain.repo.MessageRepo
import violett.pro.cvchat.domain.util.CustomResult

class UpdateMessageStatusUseCase(
    private val repository: MessageRepo
) {
    suspend operator fun invoke(messageId: String, newStatus: MessageStatus): CustomResult<Unit, RoomError> {
        return try {
            repository.updateMessageStatus(messageId, newStatus.name)
            CustomResult.Success(Unit)
        } catch (e: Exception) {
            CustomResult.Failure(RoomError.UpdateError(e.message ?: "Error updating message status"))
        }
    }
}