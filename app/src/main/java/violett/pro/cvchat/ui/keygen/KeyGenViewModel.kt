package violett.pro.cvchat.ui.keygen

import android.util.Log
import androidx.compose.foundation.text.KeyboardActions
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import violett.pro.cvchat.domain.model.errors.NetworkError
import violett.pro.cvchat.domain.usecases.GenerateKeysUseCase
import violett.pro.cvchat.domain.usecases.SendPublicKeyUseCase
import violett.pro.cvchat.domain.util.onFailure
import violett.pro.cvchat.domain.util.onSuccess
import java.security.SecureRandom
import java.util.Base64

class KeyGenViewModel(
    private val generateKeysUseCase: GenerateKeysUseCase,
    private val sendPublicKeyUseCase: SendPublicKeyUseCase
) : ViewModel()  {

    private val _keyGenState = MutableStateFlow(KeyGenState())
    val keyGenState = _keyGenState.asStateFlow()

    // Channel.BUFFERED означает "подержи событие в памяти, если UI прямо сейчас не слушает"
    private val _action = Channel<KeyGenActions>(Channel.BUFFERED)
    // Превращаем в Flow, чтобы UI мог просто подписаться
    val action = _action.receiveAsFlow()


    fun generateKeys() {


        viewModelScope.launch {
            val charPool: List<Char> = ('a'..'z') + ('A'..'Z') + ('0'..'9') + listOf('$', '%', '^', '&', '*', '(', ')', '-', '_', '+', '=', '[', ']', ';', ':', ',', '<', '>', '?')
            val random = SecureRandom()
            val tempId = (1..20)
                .map { random.nextInt(charPool.size) }
                .map(charPool::get)
                .joinToString("")

            generateKeysUseCase()
                .onSuccess { pbk ->
                    val publicKeyBase64 = Base64.getEncoder().encodeToString(pbk.encoded)
                    Log.d("KeyGenViewModel", "Public Key: $publicKeyBase64")

                    // add tempId availability check later
                    sendPublicKeyUseCase(publicKeyBase64, tempId)
                        .onSuccess {
                            _action.send(KeyGenActions.NavigateToContacts(tempId = tempId))
                            _action.send(KeyGenActions.ShowToast("Ключ успешно отправлен"))
                            _keyGenState.update {
                                it.copy(
                                    isLoading = false,
                                    pbk = publicKeyBase64,
                                    tempId = tempId
                                )
                            }
                        }
                        .onFailure { networkError ->
                            _keyGenState.update { it.copy(isLoading = false) }

                            val errorMessage = when(networkError) {
                                is NetworkError.NoInternet -> "Нет интернета"
                                is NetworkError.ServerError -> "Ошибка сервера"
                                else -> "Не удалось отправить ключ"
                            }
                            println(networkError)
                            _action.send(KeyGenActions.ShowToast(errorMessage))
                        }
                }
                .onFailure {
                    // add error handling later
                    _keyGenState.update {
                        it.copy(
                            isLoading = false
                        )
                    }
                    _action.send(KeyGenActions.ShowToast("Перезагрузи приложение и попробуй еще раз!"))
                }
        }
    }


}



data class KeyGenState(
    val isLoading : Boolean = false,
    val tempId : String = "",
    val pbk : String = ""
)

sealed interface KeyGenActions {
    data class NavigateToContacts(val tempId: String) : KeyGenActions
    data class ShowToast(val message: String) : KeyGenActions
}
