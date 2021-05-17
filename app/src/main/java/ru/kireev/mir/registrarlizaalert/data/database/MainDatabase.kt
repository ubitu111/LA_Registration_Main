package ru.kireev.mir.registrarlizaalert.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import ru.kireev.mir.registrarlizaalert.data.database.converter.Converter
import ru.kireev.mir.registrarlizaalert.data.database.dao.ArchiveGroupsVolunteersDao
import ru.kireev.mir.registrarlizaalert.data.database.dao.GroupsDao
import ru.kireev.mir.registrarlizaalert.data.database.dao.MainDao
import ru.kireev.mir.registrarlizaalert.data.database.dao.VolunteersDao
import ru.kireev.mir.registrarlizaalert.data.database.entity.ArchivedGroupsVolunteers
import ru.kireev.mir.registrarlizaalert.data.database.entity.Group
import ru.kireev.mir.registrarlizaalert.data.database.entity.Volunteer

@Database(
    entities = [
        Volunteer::class,
        Group::class,
        ArchivedGroupsVolunteers::class
    ],
    version = 22,
    exportSchema = false
)
@TypeConverters(value = [Converter::class])
abstract class MainDatabase : RoomDatabase() {
    abstract fun volunteersDao(): VolunteersDao
    abstract fun groupsDao(): GroupsDao
    abstract fun mainDao(): MainDao
    abstract fun archiveGroupsVolunteersDao(): ArchiveGroupsVolunteersDao
}