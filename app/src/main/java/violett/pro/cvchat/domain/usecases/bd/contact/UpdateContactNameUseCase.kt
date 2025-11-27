package violett.pro.cvchat.domain.usecases.bd.contact

import violett.pro.cvchat.domain.model.errors.RoomError
import violett.pro.cvchat.domain.repo.ContactRepo
import violett.pro.cvchat.domain.util.CustomResult

class UpdateContactNameUseCase(
    private val repository: ContactRepo
) {
    suspend operator fun invoke(tempId: String, newName: String): CustomResult<Unit, RoomError> {
        return try {
            repository.updateContactName(tempId, newName)
            CustomResult.Success(Unit)
        } catch (e: Exception) {
            CustomResult.Failure(RoomError.UpdateError(e.message ?: "Error updating contact name"))
        }
    }
}