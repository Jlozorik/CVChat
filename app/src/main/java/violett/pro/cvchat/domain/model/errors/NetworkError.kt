package violett.pro.cvchat.domain.model.errors

import violett.pro.cvchat.domain.util.CustomError

sealed interface NetworkError : CustomError {
    data object NoInternet : NetworkError
    data object Forbidden : NetworkError
    data object ServerError : NetworkError
    data object NotFound : NetworkError
    data object Serialization : NetworkError
    data class Unknown(val message: String) : NetworkError
}