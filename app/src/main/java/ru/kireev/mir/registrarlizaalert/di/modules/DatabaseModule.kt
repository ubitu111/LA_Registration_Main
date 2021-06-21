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
    single { get<MainDatabase>().volunteersDao() }
    single { get<MainDatabase>().groupsDao() }
    single { get<MainDatabase>().mainDao() }
    single { get<MainDatabase>().archiveGroupsVolunteersDao() }
}

private const val DB_NAME = "main.db"