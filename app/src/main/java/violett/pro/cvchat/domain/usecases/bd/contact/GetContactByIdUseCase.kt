package violett.pro.cvchat.domain.usecases.bd.contact

import violett.pro.cvchat.domain.model.Contact
import violett.pro.cvchat.domain.model.errors.RoomError
import violett.pro.cvchat.domain.repo.ContactRepo
import violett.pro.cvchat.domain.util.CustomResult

class GetContactByIdUseCase(
    private val repository: ContactRepo
) {
    suspend operator fun invoke(tempId: String): CustomResult<Contact, RoomError> {
        return try {
            val entity = repository.getContactByTempId(tempId)
            if (entity != null) {
                CustomResult.Success(entity.toDomain())
            } else {
                CustomResult.Failure(RoomError.NotFound)
            }
        } catch (e: Exception) {
            CustomResult.Failure(RoomError.Unknown(e.message?: "Error fetching contact by id"))
        }
    }
}