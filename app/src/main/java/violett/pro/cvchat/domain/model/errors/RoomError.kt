package violett.pro.cvchat.domain.model.errors

import violett.pro.cvchat.domain.util.CustomError

interface RoomError : CustomError {
    data class Unknown(val message: String) : RoomError
    data object NotFound : RoomError

    data class WriteError(val message: String) : RoomError
    data class UpdateError(val message: String) : RoomError
    data class DeleteError(val message: String) : RoomError
}