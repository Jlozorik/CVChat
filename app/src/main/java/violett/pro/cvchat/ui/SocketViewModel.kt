package violett.pro.cvchat.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import violett.pro.cvchat.data.remote.ChatSocketService
import violett.pro.cvchat.domain.model.Message
import violett.pro.cvchat.domain.model.MessageStatus
import violett.pro.cvchat.domain.model.websocket.ClientMessage
import violett.pro.cvchat.domain.model.websocket.MessageStatusUpdate
import violett.pro.cvchat.domain.model.websocket.ReceivedMessage
import violett.pro.cvchat.domain.usecases.bd.message.SaveMessageUseCase
import java.util.UUID

class SocketViewModel(
    private val socketService: ChatSocketService,
    private val saveMessageUseCase: SaveMessageUseCase
) : ViewModel() {

    private var connectionJob: Job? = null

    fun connect(tempId: String) {
        if (connectionJob?.isActive == true) return // Уже подключены

        connectionJob = viewModelScope.launch(Dispatchers.IO) {
            socketService.connect(tempId).collect { event ->
                when (event) {
                    is ReceivedMessage -> {
                        val message = Message(
                            id = UUID.randomUUID().toString(),
                            contactId = event.from,
                            content = event.payload,
                            timestamp = System.currentTimeMillis(),
                            isMine = false,
                            status = MessageStatus.SENT
                        )
                        saveMessageUseCase(message)
                    }
                    is MessageStatusUpdate -> {
                        // TODO: Обновить статус в БД
                        println("UI: Статус сообщения ${event.messageId}: ${event.status}")
                    }
                }
            }
        }
    }

    fun disconnect() {
        connectionJob?.cancel()
        viewModelScope.launch {
            socketService.disconnect()
        }
    }

    fun sendMessage(to: String, text: String) {
        viewModelScope.launch {
            val msg = ClientMessage(
                messageId = UUID.randomUUID().toString(),
                to = to,
                payload = text
            )
            socketService.sendMessage(msg)
        }
    }
}