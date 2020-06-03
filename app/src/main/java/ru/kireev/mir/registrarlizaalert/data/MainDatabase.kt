package ru.kireev.mir.registrarlizaalert.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Volunteer::class, Fox::class], version = 7, exportSchema = false)
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
    abstract fun foxesDao(): FoxesDao
}