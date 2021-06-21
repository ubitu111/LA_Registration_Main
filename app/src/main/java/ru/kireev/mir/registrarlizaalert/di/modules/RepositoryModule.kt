package ru.kireev.mir.registrarlizaalert.di.modules

import org.koin.dsl.module
import ru.kireev.mir.registrarlizaalert.data.repository.AddManuallyRepositoryImpl
import ru.kireev.mir.registrarlizaalert.domain.repository.AddManuallyRepository

val repositoryModule = module {
    factory<AddManuallyRepository> { AddManuallyRepositoryImpl(get()) }
}