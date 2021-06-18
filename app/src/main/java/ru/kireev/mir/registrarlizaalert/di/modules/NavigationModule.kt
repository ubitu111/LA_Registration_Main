package ru.kireev.mir.registrarlizaalert.di.modules

import com.github.terrakok.cicerone.Cicerone
import com.github.terrakok.cicerone.Router
import org.koin.dsl.module

val navigationModule = module {
    single { Cicerone.create() }
    single { get<Cicerone<Router>>().router }
}