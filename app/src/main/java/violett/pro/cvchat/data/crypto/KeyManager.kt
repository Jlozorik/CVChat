package violett.pro.cvchat.data.crypto

import android.os.Build
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import androidx.annotation.RequiresApi
import java.security.KeyPair
import java.security.KeyPairGenerator
import java.security.KeyStore
import java.security.PrivateKey
import java.security.PublicKey

interface KeyManager {
    fun getOrCreateKeyPair(): KeyPair
    fun getPublicKey(): PublicKey?
    fun getPrivateKey(): PrivateKey?
}


class KeyManagerImpl : KeyManager {

    private val keyStore: KeyStore by lazy {
        KeyStore.getInstance(ANDROID_KEYSTORE_PROVIDER).apply {
            load(null)
        }
    }

    override fun getOrCreateKeyPair(): KeyPair {
        return if (keyStore.containsAlias(KEY_ALIAS)) {
            val privateKey = keyStore.getKey(KEY_ALIAS, null) as PrivateKey
            val publicKey = keyStore.getCertificate(KEY_ALIAS).publicKey
            KeyPair(publicKey, privateKey)
        } else {
            generateAndStoreKeyPair()
        }
    }

    override fun getPublicKey(): PublicKey? {
        return if (keyStore.containsAlias(KEY_ALIAS)) {
            keyStore.getCertificate(KEY_ALIAS).publicKey
        } else {
            null
        }
    }

    override fun getPrivateKey(): PrivateKey? {
        return if (keyStore.containsAlias(KEY_ALIAS)) {
            keyStore.getKey(KEY_ALIAS, null) as? PrivateKey
        } else {
            null
        }
    }

    private fun generateAndStoreKeyPair(): KeyPair {
        val keyPairGenerator = KeyPairGenerator.getInstance(
            KeyProperties.KEY_ALGORITHM_EC,
            ANDROID_KEYSTORE_PROVIDER
        )

        val purposes = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            KeyProperties.PURPOSE_AGREE_KEY
        } else {
            KeyProperties.PURPOSE_SIGN or KeyProperties.PURPOSE_VERIFY
        }

        val parameterSpec = KeyGenParameterSpec.Builder(
            KEY_ALIAS,
            purposes
        )
            .setKeySize(256)
            .build()

        keyPairGenerator.initialize(parameterSpec)
        return keyPairGenerator.generateKeyPair()
    }

    companion object {
        private const val ANDROID_KEYSTORE_PROVIDER = "AndroidKeyStore"
        private const val KEY_ALIAS = "violett.pro.cvchat.E2EE_KEY_PAIR_ALIAS"
    }
}