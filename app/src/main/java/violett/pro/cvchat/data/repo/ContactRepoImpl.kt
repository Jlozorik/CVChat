package violett.pro.cvchat.data.repo

import kotlinx.coroutines.flow.Flow
import violett.pro.cvchat.data.local.dao.ContactDao
import violett.pro.cvchat.data.local.entity.ContactEntity
import violett.pro.cvchat.domain.repo.ContactRepo

class ContactRepoImpl(
    private val contactDao: ContactDao
) : ContactRepo {

    override fun getAllContacts(): Flow<List<ContactEntity>> {
        return contactDao.getAllContacts()
    }

    override suspend fun getContactByTempId(tempId: String): ContactEntity? {
        return contactDao.getContactByTempId(tempId)
    }

    override suspend fun updateContactName(tempId: String, name: String) {
        contactDao.updateContact(tempId, name)
    }

    override suspend fun saveContact(contact: ContactEntity) {
        contactDao.insertContact(contact)
    }

    override suspend fun deleteContact(contact: ContactEntity) {
        contactDao.deleteContact(contact)
    }
}