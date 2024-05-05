package kurd.reco.recoz

import android.app.Application
import kurd.reco.recoz.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class KtorApp : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@KtorApp)
            modules(appModule)
        }
    }
}
