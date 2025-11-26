package violett.pro.cvchat

import android.app.Application
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level
import violett.pro.cvchat.di.mainModule


class KoinApp : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            // Log Koin messages
            androidLogger(Level.ERROR) // Установите Level.DEBUG для более подробного логирования
            // Android context
            androidContext(this@KoinApp)
            // Load modules
            modules(
                mainModule,
            )
        }
    }
}