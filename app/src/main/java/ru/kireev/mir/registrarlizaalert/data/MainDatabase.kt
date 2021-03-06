package ru.kireev.mir.registrarlizaalert.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import ru.kireev.mir.registrarlizaalert.converters.Converter

@Database(entities = [Volunteer::class, Group::class, ArchivedGroupsVolunteers::class], version = 22, exportSchema = false)
@TypeConverters(value = [Converter::class])
abstract class MainDatabase : RoomDatabase() {
    companion object {
        private var db: MainDatabase? = null
        private const val DB_NAME = "main.db"
        private val LOCK = Any()

        fun getInstance(context: Context): MainDatabase {
            synchronized(LOCK) {
                db?.let { return it }
                val instance = Room.databaseBuilder(
                        context,
                        MainDatabase::class.java,
                        DB_NAME)
                        .fallbackToDestructiveMigration()
                        .build()
                db = instance
                return instance
            }
        }
    }

    abstract fun volunteersDao(): VolunteersDao
    abstract fun groupsDao(): GroupsDao
    abstract fun mainDao(): MainDao
    abstract fun archiveGroupsVolunteersDao(): ArchiveGroupsVolunteersDao
}