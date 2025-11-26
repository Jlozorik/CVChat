package violett.pro.cvchat.domain.usecases

import violett.pro.cvchat.data.crypto.KeyManager
import violett.pro.cvchat.domain.model.errors.KeyGenError
import violett.pro.cvchat.domain.util.CustomResult
import java.security.PublicKey

class GenerateKeysUseCase(
    private val keyManager: KeyManager
) {
    operator fun invoke(): CustomResult<PublicKey, KeyGenError> {
        return try {
            CustomResult.Success(keyManager.getOrCreateKeyPair().public)
        } catch (e: Exception) {
            CustomResult.Failure(KeyGenError.UnknownError(e.message ?: "Unknown error"))
        }

    }
}