package violett.pro.cvchat.ui.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import violett.pro.cvchat.domain.model.Message
import violett.pro.cvchat.domain.model.MessageStatus
import violett.pro.cvchat.domain.model.errors.RoomError
import violett.pro.cvchat.domain.usecases.bd.message.GetChatMessagesUseCase
import violett.pro.cvchat.domain.usecases.bd.message.SaveMessageUseCase
import violett.pro.cvchat.domain.util.onFailure
import violett.pro.cvchat.domain.util.onSuccess
import violett.pro.cvchat.ui.SocketViewModel
import java.util.UUID

class ChatViewModel(
    private val contactId: String,
    private val getMessagesUseCase: GetChatMessagesUseCase,
    private val saveMessageUseCase: SaveMessageUseCase,
    private val socketViewModel: SocketViewModel
) : ViewModel() {

    val messages: StateFlow<List<Message>> = getMessagesUseCase(contactId)
        .map { list -> list.sortedByDescending { it.timestamp } }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _action = Channel<ChatAction>(Channel.BUFFERED)
    val action = _action.receiveAsFlow()


    fun sendMessage(text: String) {
        val newMessage = Message(
            id = UUID.randomUUID().toString(),
            contactId = contactId,
            content = text,
            timestamp = System.currentTimeMillis(),
            isMine = true,
            status = MessageStatus.SENDING
        )

        viewModelScope.launch {
            saveMessageUseCase(newMessage)
                .onFailure { error->
                    _action.send(ChatAction.ShowToast(
                        when(error)
                        {
                            is RoomError.Unknown -> error.message
                            else -> "Хз чо за ошибка"
                        }
                    )
                    )
                }
            socketViewModel.sendMessage(to = contactId, text = text)
        }
    }
}

sealed interface ChatAction{
    data object RefreshChat : ChatAction
    data class ShowToast(val message : String) : ChatAction
}
