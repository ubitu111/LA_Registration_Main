package ru.kireev.mir.registrarlizaalert.di.modules

import org.koin.dsl.module
import ru.kireev.mir.registrarlizaalert.data.repository.AddManuallyRepositoryImpl
import ru.kireev.mir.registrarlizaalert.data.repository.BarCodeScannerRepositoryImpl
import ru.kireev.mir.registrarlizaalert.domain.repository.AddManuallyRepository
import ru.kireev.mir.registrarlizaalert.domain.repository.BarCodeScannerRepository

val repositoryModule = module {
    factory<AddManuallyRepository> { AddManuallyRepositoryImpl(get()) }
    factory<BarCodeScannerRepository> { BarCodeScannerRepositoryImpl(get()) }
}