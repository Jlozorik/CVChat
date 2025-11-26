package violett.pro.cvchat.domain.repo

import violett.pro.cvchat.domain.model.errors.NetworkError
import violett.pro.cvchat.domain.util.CustomResult

interface SendKeyRepo {
    suspend fun sendPublicKey(publicKey: String, tempId: String): CustomResult<Unit, NetworkError>
}