package violett.pro.cvchat.di

import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module
import violett.pro.cvchat.domain.usecases.TestUseCase
import violett.pro.cvchat.ui.test.TestViewModel

//val repositoryModule = module {
//    // В зависимости от того, как вы хотите управлять жизненным циклом:
//    // single - для одного экземпляра на все приложение (наиболее часто для репозиториев)
//    single<ChatRepository> {
//        ChatRepositoryImpl(
//            // Koin здесь автоматически предоставит зависимости,
//            // от которых зависит ChatRepositoryImpl (например, DAO и API-сервис),
//            // при условии, что они объявлены в других модулях (DatabaseModule, NetworkModule).
//            localDataSource = get(), // Получает DAO из DatabaseModule
//            remoteDataSource = get()  // Получает API-сервис из NetworkModule
//        )
//    }
//}

val repoModule = module {
    single<String> {
        "Мухехехе"
    }
}


val mainModule = module {
    factory { TestUseCase(get()) }
    viewModelOf(::TestViewModel)
}

