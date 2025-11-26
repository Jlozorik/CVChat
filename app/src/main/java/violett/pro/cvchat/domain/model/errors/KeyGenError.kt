package violett.pro.cvchat.domain.model.errors

import violett.pro.cvchat.domain.util.CustomError

interface KeyGenError : CustomError{
    data class UnknownError(val msg: String) : KeyGenError
}