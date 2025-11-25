package violett.pro.cvchat.ui.test

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import violett.pro.cvchat.domain.usecases.TestUseCase
import violett.pro.cvchat.domain.util.onFailure
import violett.pro.cvchat.domain.util.onSuccess

class TestViewModel(
    private val testUseCase: TestUseCase
) : ViewModel(){

    private val _testState = MutableStateFlow<TestState>(TestState())
    val testState = _testState.asStateFlow()

    // Channel.BUFFERED означает "подержи событие в памяти, если UI прямо сейчас не слушает"
    private val _action = Channel<TestAction>(Channel.BUFFERED)
    // Превращаем в Flow, чтобы UI мог просто подписаться
    val action = _action.receiveAsFlow()

    fun testFunc(isSuccess : Boolean) {
        viewModelScope.launch{
            _testState.update {
                it.copy(
                    isLoading = true
                )
            }
            delay(1000)
            testUseCase(isSuccess)
                .onSuccess {
                    _testState.update {
                        it.copy(
                            isSuccess = isSuccess,
                            isLoading = false
                        )
                    }
                    _action.send(TestAction.NavigateToDetails)

                }
                .onFailure {
                    _testState.update {
                        it.copy(
                            isSuccess = isSuccess,
                            isLoading = false
                        )
                    }
                    _action.send(TestAction.ShowToast("Ошибка!"))
                }
        }
    }

}

data class TestState(
    val isSuccess: Boolean = false,
    val isLoading : Boolean = false,
    val textData: String = ""
)

sealed interface TestAction {
    data object NavigateToDetails : TestAction
    data class ShowToast(val message: String) : TestAction
}