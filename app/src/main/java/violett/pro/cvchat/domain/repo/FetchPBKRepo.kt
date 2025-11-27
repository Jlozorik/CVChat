package violett.pro.cvchat.domain.repo

import violett.pro.cvchat.data.remote.model.GetPublicKeyRequestDto
import violett.pro.cvchat.domain.model.errors.NetworkError
import violett.pro.cvchat.domain.util.CustomResult

interface FetchPBKRepo {
    suspend fun fetchPBK(tempId : String) : CustomResult<GetPublicKeyRequestDto, NetworkError>

}