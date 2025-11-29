package violett.pro.cvchat.ui.contacts

import android.util.Base64
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import violett.pro.cvchat.data.crypto.CryptoManager
import violett.pro.cvchat.domain.model.Contact
import violett.pro.cvchat.domain.model.errors.RoomError
import violett.pro.cvchat.domain.usecases.FetchPBKUseCase
import violett.pro.cvchat.domain.usecases.bd.contact.DeleteContactUseCase
import violett.pro.cvchat.domain.usecases.bd.contact.GetAllContactsUseCase
import violett.pro.cvchat.domain.usecases.bd.contact.GetContactByIdUseCase
import violett.pro.cvchat.domain.usecases.bd.contact.SaveContactUseCase
import violett.pro.cvchat.domain.usecases.bd.contact.UpdateContactNameUseCase
import violett.pro.cvchat.domain.usecases.bd.message.SaveMessageUseCase
import violett.pro.cvchat.domain.util.onFailure
import violett.pro.cvchat.domain.util.onSuccess
import javax.crypto.SecretKey

class ContactViewModel(
    private val fetchPBKUseCase: FetchPBKUseCase,
    private val saveContactUseCase: SaveContactUseCase,
    private val saveMessageUseCase: SaveMessageUseCase,
    private val getAllContactsUseCase: GetAllContactsUseCase,
    private val updateContactNameUseCase: UpdateContactNameUseCase,
    private val getContactByIdUseCase: GetContactByIdUseCase,
    private val deleteContactUseCase: DeleteContactUseCase,
    private val cryptoManager: CryptoManager
) : ViewModel() {

    private val _contactState = MutableStateFlow(ContactState())
    val contactState = _contactState.asStateFlow()

    private val _action = Channel<ContactActions>(Channel.BUFFERED)
    val action = _action.receiveAsFlow()

    init {
        loadContacts()
    }

    fun deleteContact(contact: Contact) {
        viewModelScope.launch {
            deleteContactUseCase(contact)
                .onSuccess {
                    _contactState.update {
                        it.copy(
                            contacts = it.contacts.filter {localContact-> localContact.tempId != contact.tempId }
                        )
                    }
                }
                .onFailure {
                    _action.send(ContactActions.ShowToast(
                        when(it)
                        {
                            is RoomError.Unknown -> it.message
                            is RoomError.DeleteError -> it.message
                            else -> "Хз чо за ошибка"
                        }
                    )
                    )
                }

        }
    }

    fun changeName(tempId: String, name : String){
        viewModelScope.launch {
            updateContactNameUseCase(tempId = tempId, newName = name)
                .onSuccess {
                    _contactState.update {
                        it.copy(
                            contacts = it.contacts.map { contact ->
                                if (contact.tempId == tempId) {
                                    contact.copy(name = name)
                                } else {
                                    contact
                                }
                            }
                        )
                    }
                }
                .onFailure { error ->
                    _action.send(ContactActions.ShowToast(
                        when(error)
                        {
                            is RoomError.Unknown -> error.message
                            else -> "Имя не было изменено"
                        })
                    )
                }
        }
    }

    fun addUser(tempId : String) {
        viewModelScope.launch {
            fetchPBKUseCase(tempId)
                .onSuccess { data ->
                    val contact = Contact(
                        tempId = tempId,
                        publicKey = data.publicKey,
                        name = "",
                    )
                    saveContactUseCase(
                        contact
                    )
                        .onSuccess {
//                            _action.send(ContactActions.NavigateToChat(
//                                contact
//                            )
                            _contactState.update {
                                it.copy(
                                    contacts = it.contacts + contact
                                )
                            }
                        }
                        .onFailure {  error ->
                            Log.d("ContactViewModel", "saveContactUseCase: $error")
                        }
                }
                .onFailure { error ->
                    Log.d("ContactViewModel", "fetchPBKUseCase: $error")
                }
        }
    }

    fun loadContacts() {
        viewModelScope.launch {
            getAllContactsUseCase()
                .onSuccess { result ->
                    val contacts = result.map {
                        val publicKeyBytes = Base64.decode(it.publicKey, Base64.DEFAULT)
                        val secretKey : SecretKey = cryptoManager.calculateSharedSecret(publicKeyBytes)
                        Contact(
                            tempId = it.tempId,
                            publicKey = it.publicKey,
                            name = it.name,
                            sharedSecret = secretKey
                        )
                    }

                    _contactState.update {
                        it.copy(
                            isLoading = false,
                            contacts = contacts
                        )
                    }
                    Log.d("ContactViewModel", "loadContacts: $result")
                }
                .onFailure { error ->
                    _action.send(ContactActions.ShowToast(
                            when(error)
                            {
                                is RoomError.Unknown -> error.message
                                else -> "Хз чо за ошибка"
                           }
                         )
                    )


                }
        }
    }

}


data class ContactState(
    val isLoading : Boolean = true,
    val contacts : List<Contact> = emptyList(),
)

sealed interface ContactActions{
    data class NavigateToChat(val contact : Contact) : ContactActions
    data object RefreshContact : ContactActions
    data class ShowToast(val message : String) : ContactActions
}