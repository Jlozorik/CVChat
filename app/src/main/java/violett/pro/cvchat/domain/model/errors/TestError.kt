package violett.pro.cvchat.domain.model.errors
import violett.pro.cvchat.domain.util.CustomError
sealed interface TestError : CustomError {
    enum class Network : TestError {
        NO_INTERNET, SERVER_UNAVAILABLE
    }

    enum class Validation : TestError {
        PASSWORD_TOO_SHORT, INVALID_EMAIL
    }

    data class Unknown(val message: String) : TestError
}