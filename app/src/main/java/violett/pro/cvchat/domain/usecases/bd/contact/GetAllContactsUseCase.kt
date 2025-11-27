package violett.pro.cvchat.domain.usecases.bd.contact

import kotlinx.coroutines.flow.first
import violett.pro.cvchat.domain.model.Contact
import violett.pro.cvchat.domain.model.errors.RoomError
import violett.pro.cvchat.domain.repo.ContactRepo
import violett.pro.cvchat.domain.util.CustomResult

class GetAllContactsUseCase(
    private val repository: ContactRepo
) {
    suspend operator fun invoke(): CustomResult<List<Contact>, RoomError> {
        return try {
            val entities = repository.getAllContacts().first()
            val contacts = entities.map { it.toDomain() }
            CustomResult.Success(contacts)
        } catch (e: Exception) {
            CustomResult.Failure(RoomError.Unknown(e.message ?: "Error fetching contacts"))
        }
    }
}