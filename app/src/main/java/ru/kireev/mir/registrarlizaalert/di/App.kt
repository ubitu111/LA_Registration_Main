package ru.kireev.mir.registrarlizaalert.di

import android.app.Application
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.fragment.koin.fragmentFactory
import org.koin.core.context.startKoin
import ru.kireev.mir.registrarlizaalert.BuildConfig
import ru.kireev.mir.registrarlizaalert.di.modules.*
import timber.log.Timber

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        initKoin()
        initLog()
    }

    private fun initKoin() {
        startKoin {
            androidContext(this@App)
            fragmentFactory()
            val modules = listOf(
                databaseModule,
                viewModelModule,
                fragmentModule,
                navigationModule,
                repositoryModule
            )
            modules(modules)
        }
    }

    private fun initLog() {
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }
}