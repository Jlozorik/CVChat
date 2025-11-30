package violett.pro.cvchat.di

import androidx.room.Room
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.ANDROID
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.websocket.WebSockets
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.core.module.dsl.viewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module
import violett.pro.cvchat.data.crypto.CryptoManager
import violett.pro.cvchat.data.crypto.CryptoManagerImpl
import violett.pro.cvchat.data.crypto.KeyManager
import violett.pro.cvchat.data.crypto.KeyManagerImpl
import violett.pro.cvchat.data.local.ChatDatabase
import violett.pro.cvchat.data.remote.ChatSocketService
import violett.pro.cvchat.data.repo.ContactRepoImpl
import violett.pro.cvchat.data.repo.FetchPBKRepoImpl
import violett.pro.cvchat.data.repo.MessageRepoImpl
import violett.pro.cvchat.data.repo.SendKeyRepoImpl
import violett.pro.cvchat.domain.repo.ContactRepo
import violett.pro.cvchat.domain.repo.FetchPBKRepo
import violett.pro.cvchat.domain.repo.MessageRepo
import violett.pro.cvchat.domain.repo.SendKeyRepo
import violett.pro.cvchat.domain.usecases.FetchPBKUseCase
import violett.pro.cvchat.domain.usecases.GenerateKeysUseCase
import violett.pro.cvchat.domain.usecases.SendPublicKeyUseCase
import violett.pro.cvchat.domain.usecases.bd.contact.DeleteContactUseCase
import violett.pro.cvchat.domain.usecases.bd.contact.GetAllContactsUseCase
import violett.pro.cvchat.domain.usecases.bd.contact.GetContactByIdUseCase
import violett.pro.cvchat.domain.usecases.bd.contact.SaveContactUseCase
import violett.pro.cvchat.domain.usecases.bd.contact.UpdateContactNameUseCase
import violett.pro.cvchat.domain.usecases.bd.message.GetChatMessagesUseCase
import violett.pro.cvchat.domain.usecases.bd.message.SaveMessageUseCase
import violett.pro.cvchat.domain.usecases.bd.message.UpdateMessageStatusUseCase
import violett.pro.cvchat.ui.SocketViewModel
import violett.pro.cvchat.ui.chat.ChatViewModel
import violett.pro.cvchat.ui.contacts.ContactViewModel
import violett.pro.cvchat.ui.keygen.KeyGenViewModel


val mainModule = module {

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

    factory { GetAllContactsUseCase(get()) }
    factory { GetContactByIdUseCase(get()) }
    factory { SaveContactUseCase(get()) }
    factory { UpdateContactNameUseCase(get()) }
    factory { DeleteContactUseCase(get()) }

    // Messages
    factory { GetChatMessagesUseCase(get()) }
    factory { SaveMessageUseCase(get()) }
    factory { UpdateMessageStatusUseCase(get()) }

    single<SendKeyRepo> {
        SendKeyRepoImpl(get())
    }
    single<FetchPBKRepo> {
        FetchPBKRepoImpl(get())
    }
    single<ContactRepo> {
        ContactRepoImpl(contactDao = get())
    }

    single<MessageRepo> {
        MessageRepoImpl(messageDao = get())
    }

    single<KeyManager> { KeyManagerImpl() }
    single<CryptoManager> { CryptoManagerImpl(get()) }
    single {
        Json {
            ignoreUnknownKeys = true
            isLenient = true
            encodeDefaults = true
            classDiscriminator = "type"
            prettyPrint = true
        }
    }
    single {
        HttpClient(CIO) {
            install(ContentNegotiation) {
                json(get())
            }
            install(WebSockets)
            install(Logging) {
                logger = Logger.ANDROID
                level = LogLevel.ALL
            }

        }
    }

    factory { GenerateKeysUseCase(get()) }
    factory { SendPublicKeyUseCase(get()) }
    factory { FetchPBKUseCase(get()) }

    viewModelOf(::KeyGenViewModel)
    viewModelOf(::ContactViewModel)
    viewModelOf(::SocketViewModel)
    viewModel { (contactId: String) ->
        ChatViewModel(
            contactId = contactId,
            getMessagesUseCase = get(),
            saveMessageUseCase = get(),
            socketViewModel = get()
        )
    }

    single { ChatSocketService(get(), get()) }


}

