package violett.pro.cvchat.data.crypto

import java.security.KeyFactory
import java.security.MessageDigest
import java.security.PublicKey
import java.security.spec.X509EncodedKeySpec
import javax.crypto.Cipher
import javax.crypto.KeyAgreement
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.SecretKeySpec

interface CryptoManager {
    fun calculateSharedSecret(theirPublicKeyBytes: ByteArray): SecretKey
    fun encrypt(data: String, secretKey: SecretKey): ByteArray?
    fun decrypt(encryptedData: ByteArray, secretKey: SecretKey): String?
}

class CryptoManagerImpl(
    private val keyManager: KeyManager // Зависимость объявляется здесь
) : CryptoManager {

    private val cipher: Cipher = Cipher.getInstance(AES_TRANSFORMATION)
    private val keyAgreement: KeyAgreement = KeyAgreement.getInstance(KEY_AGREEMENT_ALGORITHM)

    override fun calculateSharedSecret(theirPublicKeyBytes: ByteArray): SecretKey {
        val theirPublicKey = decodePublicKey(theirPublicKeyBytes)
        val ourPrivateKey = keyManager.getPrivateKey()
            ?: throw IllegalStateException("Приватный ключ не найден в Keystore.")

        keyAgreement.init(ourPrivateKey)
        keyAgreement.doPhase(theirPublicKey, true)

        val sharedSecretBytes = keyAgreement.generateSecret()

        // KDF для получения надежного AES ключа из общего секрета
        val messageDigest = MessageDigest.getInstance(KDF_ALGORITHM)
        val derivedKeyBytes = messageDigest.digest(sharedSecretBytes)

        return SecretKeySpec(derivedKeyBytes, 0, 32, AES_ALGORITHM)
    }

    override fun encrypt(data: String, secretKey: SecretKey): ByteArray? {
        return try {
            cipher.init(Cipher.ENCRYPT_MODE, secretKey)
            val iv = cipher.iv
            val encryptedBytes = cipher.doFinal(data.toByteArray(Charsets.UTF_8))
            iv + encryptedBytes // [IV] + [Шифротекст]
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    override fun decrypt(encryptedData: ByteArray, secretKey: SecretKey): String? {
        return try {
            val iv = encryptedData.copyOfRange(0, IV_SIZE_BYTES)
            val encryptedBytes = encryptedData.copyOfRange(IV_SIZE_BYTES, encryptedData.size)

            val gcmParameterSpec = GCMParameterSpec(GCM_TAG_LENGTH_BITS, iv)
            cipher.init(Cipher.DECRYPT_MODE, secretKey, gcmParameterSpec)

            val decryptedBytes = cipher.doFinal(encryptedBytes)
            String(decryptedBytes, Charsets.UTF_8)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun decodePublicKey(encodedKey: ByteArray): PublicKey {
        val keySpec = X509EncodedKeySpec(encodedKey)
        val keyFactory = KeyFactory.getInstance("EC")
        return keyFactory.generatePublic(keySpec)
    }

    companion object {
        private const val AES_ALGORITHM = "AES"
        private const val AES_TRANSFORMATION = "AES/GCM/NoPadding"
        private const val KEY_AGREEMENT_ALGORITHM = "ECDH"
        private const val KDF_ALGORITHM = "SHA-256"
        private const val IV_SIZE_BYTES = 12
        private const val GCM_TAG_LENGTH_BITS = 128
    }
}