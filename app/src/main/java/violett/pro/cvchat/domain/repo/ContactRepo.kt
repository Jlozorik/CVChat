package violett.pro.cvchat.domain.repo

import kotlinx.coroutines.flow.Flow
import violett.pro.cvchat.data.local.entity.ContactEntity

interface ContactRepo {
    fun getAllContacts(): Flow<List<ContactEntity>>
    suspend fun getContactByTempId(tempId: String): ContactEntity?
    suspend fun updateContactName(tempId: String, name: String)
    suspend fun saveContact(contact: ContactEntity)
    suspend fun deleteContact(contact: ContactEntity)
}