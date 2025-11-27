package violett.pro.cvchat.ui.contacts

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import violett.pro.cvchat.data.crypto.CryptoManager
import violett.pro.cvchat.domain.model.Contact
import violett.pro.cvchat.domain.model.errors.RoomError
import violett.pro.cvchat.domain.usecases.FetchPBKUseCase
import violett.pro.cvchat.domain.usecases.bd.contact.GetAllContactsUseCase
import violett.pro.cvchat.domain.usecases.bd.contact.GetContactByIdUseCase
import violett.pro.cvchat.domain.usecases.bd.contact.SaveContactUseCase
import violett.pro.cvchat.domain.usecases.bd.message.SaveMessageUseCase
import violett.pro.cvchat.domain.util.onFailure
import violett.pro.cvchat.domain.util.onSuccess

class ContactViewModel(
    private val fetchPBKUseCase: FetchPBKUseCase,
    private val saveContactUseCase: SaveContactUseCase,
    private val saveMessageUseCase: SaveMessageUseCase,
    private val getAllContactsUseCase: GetAllContactsUseCase,
    private val getContactByIdUseCase: GetContactByIdUseCase,
    private val cryptoManager: CryptoManager
) : ViewModel() {

    private val _contactState = MutableStateFlow(ContactState())
    val contactState = _contactState.asStateFlow()

    private val _action = Channel<ContactActions>(Channel.BUFFERED)
    val action = _action.receiveAsFlow()

    init {
        loadContacts()
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
                    _contactState.update {
                        it.copy(
                            isLoading = false,
                            contacts = result
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
    val isLoading : Boolean = false,
    val contacts : List<Contact> = emptyList(),
)

sealed interface ContactActions{
    data class NavigateToChat(val contact : Contact) : ContactActions
    data object RefreshContact : ContactActions
    data class ShowToast(val message : String) : ContactActions
}