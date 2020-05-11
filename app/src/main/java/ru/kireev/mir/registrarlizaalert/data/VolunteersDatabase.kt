package ru.kireev.mir.registrarlizaalert.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Volunteer::class], version = 1, exportSchema = false)
abstract class VolunteersDatabase : RoomDatabase() {
    companion object {
        private var db: VolunteersDatabase? = null
        private const val DB_NAME = "main.db"
        private val LOCK = Any()

        fun getInstance(context: Context): VolunteersDatabase {
            synchronized(LOCK) {
                db?.let { return it }
                val instance = Room.databaseBuilder(
                        context,
                        VolunteersDatabase::class.java,
                        DB_NAME)
                        .build()
                db = instance
                return instance
            }
        }
    }

    abstract fun volunteersDao(): VolunteersDao

}