package violett.pro.cvchat.di

import androidx.room.Room
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.ANDROID
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.http.ContentType
import io.ktor.http.ContentType.Application.Json
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module
import violett.pro.cvchat.data.crypto.CryptoManager
import violett.pro.cvchat.data.crypto.CryptoManagerImpl
import violett.pro.cvchat.data.crypto.KeyManager
import violett.pro.cvchat.data.crypto.KeyManagerImpl
import violett.pro.cvchat.data.local.ChatDatabase
import violett.pro.cvchat.data.repo.SendKeyRepoImpl
import violett.pro.cvchat.domain.repo.SendKeyRepo
import violett.pro.cvchat.domain.usecases.GenerateKeysUseCase
import violett.pro.cvchat.domain.usecases.SendPublicKeyUseCase
import violett.pro.cvchat.ui.keygen.KeyGenViewModel



val mainModule = module {

    single<SendKeyRepo> {
        SendKeyRepoImpl(get())
    }
    single<KeyManager> { KeyManagerImpl() }
    single<CryptoManager> { CryptoManagerImpl(get()) }
    single {
        Json {
            ignoreUnknownKeys = true
            isLenient = true
            encodeDefaults = true
        }
    }
    single {
        HttpClient(CIO) {
            install(ContentNegotiation) {
                json(get())
            }

            install(Logging) {
                logger = Logger.ANDROID
                level = LogLevel.ALL
            }

        }
    }

    factory { GenerateKeysUseCase(get()) }
    factory { SendPublicKeyUseCase(get()) }
    viewModelOf(::KeyGenViewModel)

    single {
        Room.databaseBuilder(
            get(),
            ChatDatabase::class.java,
            "cvchat.db"
        )
            .fallbackToDestructiveMigration(true)
            .build()
    }

    single { get<ChatDatabase>().contactDao() }
    single { get<ChatDatabase>().messageDao() }
}

