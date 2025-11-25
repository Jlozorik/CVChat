package violett.pro.cvchat.domain.util

sealed interface CustomResult<out D, out E : CustomError> {
    data class Success<out D, out E : CustomError>(val data: D) : CustomResult<D, E>

    data class Failure<out D, out E : CustomError>(val error: E) : CustomResult<D, E>
}