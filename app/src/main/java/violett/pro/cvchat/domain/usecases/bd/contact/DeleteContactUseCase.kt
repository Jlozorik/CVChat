package violett.pro.cvchat.domain.usecases.bd.contact

import violett.pro.cvchat.data.local.entity.toEntity
import violett.pro.cvchat.domain.model.Contact
import violett.pro.cvchat.domain.model.errors.RoomError
import violett.pro.cvchat.domain.repo.ContactRepo
import violett.pro.cvchat.domain.util.CustomResult

class DeleteContactUseCase(
    private val repository: ContactRepo
) {
    suspend operator fun invoke(contact: Contact): CustomResult<Unit, RoomError> {
        return try {
            repository.deleteContact(contact.toEntity())
            CustomResult.Success(Unit)
        } catch (e: Exception) {
            CustomResult.Failure(RoomError.DeleteError(e.message ?: "Error deleting contact"))
        }
    }
}