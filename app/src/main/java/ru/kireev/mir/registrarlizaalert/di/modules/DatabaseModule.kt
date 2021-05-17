package ru.kireev.mir.registrarlizaalert.di.modules

import androidx.room.Room
import org.koin.dsl.module
import ru.kireev.mir.registrarlizaalert.data.database.MainDatabase

val databaseModule = module {
    single {
        Room.databaseBuilder(get(), MainDatabase::class.java, DB_NAME)
            .fallbackToDestructiveMigration()
            .build()
    }
}

private const val DB_NAME = "main.db"