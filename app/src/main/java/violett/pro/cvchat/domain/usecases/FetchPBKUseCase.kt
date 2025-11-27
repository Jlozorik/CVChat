package violett.pro.cvchat.domain.usecases

import violett.pro.cvchat.data.remote.model.GetPublicKeyRequestDto
import violett.pro.cvchat.domain.model.errors.NetworkError
import violett.pro.cvchat.domain.repo.FetchPBKRepo
import violett.pro.cvchat.domain.util.CustomResult

class FetchPBKUseCase(
    private val fetchPBKRepo: FetchPBKRepo
) {
    suspend operator fun invoke(tempId: String): CustomResult<GetPublicKeyRequestDto, NetworkError> {
        if (tempId.isBlank()) {
            return CustomResult.Failure(NetworkError.Unknown("Temp id is empty"))
        }
        return fetchPBKRepo.fetchPBK(tempId)
    }
}