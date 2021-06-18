package ru.kireev.mir.registrarlizaalert.di

import android.app.Application
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.fragment.koin.fragmentFactory
import org.koin.core.context.startKoin
import ru.kireev.mir.registrarlizaalert.di.modules.databaseModule
import ru.kireev.mir.registrarlizaalert.di.modules.fragmentModule
import ru.kireev.mir.registrarlizaalert.di.modules.navigationModule
import ru.kireev.mir.registrarlizaalert.di.modules.viewModelModule

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        initKoin()
    }

    private fun initKoin() {
        startKoin {
            androidContext(this@App)
            fragmentFactory()
            val modules = listOf(
                databaseModule,
                viewModelModule,
                fragmentModule,
                navigationModule
            )
            modules(modules)
        }
    }
}