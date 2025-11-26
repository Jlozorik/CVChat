package violett.pro.cvchat.domain.usecases

import violett.pro.cvchat.domain.model.errors.NetworkError
import violett.pro.cvchat.domain.repo.SendKeyRepo
import violett.pro.cvchat.domain.util.CustomResult

class SendPublicKeyUseCase(
    private val repository: SendKeyRepo
) {
    suspend operator fun invoke(publicKey: String, tempId: String): CustomResult<Unit, NetworkError> {
        if (publicKey.isBlank()) {
            return CustomResult.Failure(NetworkError.Unknown("Public key is empty"))
        }
        return repository.sendPublicKey(publicKey, tempId)
    }
}