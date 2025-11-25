package violett.pro.cvchat.domain.util

inline fun <D, E : CustomError> CustomResult<D, E>.onSuccess(action: (D) -> Unit): CustomResult<D, E> {
    if (this is CustomResult.Success) {
        action(data)
    }
    return this
}

inline fun <D, E : CustomError> CustomResult<D, E>.onFailure(action: (E) -> Unit): CustomResult<D, E> {
    if (this is CustomResult.Failure) {
        action(error)
    }
    return this
}