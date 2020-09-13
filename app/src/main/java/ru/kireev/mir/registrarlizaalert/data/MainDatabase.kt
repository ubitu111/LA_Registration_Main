package ru.kireev.mir.registrarlizaalert.data

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Volunteer::class, Fox::class], version = 9, exportSchema = false)
abstract class MainDatabase : RoomDatabase() {
    companion object {
        private var db: MainDatabase? = null
        private const val DB_NAME = "main.db"
        private val LOCK = Any()
        private const val FILENAME_FOR_BACKUP_AND_RESTORE = "database_backup.csv"

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
    abstract fun mainDao(): MainDao


    //очищает счетчик auto_increment, чтобы восстановление таблицы из файла прошла успешно
    private fun clearTablesAutoIncrementCounter(context: Context) {
        db?.let {
            val helper = it.openHelper
            val dataBase = SQLiteDatabase.openOrCreateDatabase(context.getDatabasePath(helper.databaseName), null)
            dataBase.execSQL("UPDATE sqlite_sequence SET seq = 0 WHERE name = ?;", arrayOf("volunteers"))
            dataBase.execSQL("UPDATE sqlite_sequence SET seq = 0 WHERE name = ?;", arrayOf("foxes"))
        }
    }

    //запрос, который возвращает количество ранее добавленных записей в таблицу foxes (по сути, счетчик auto_increment)
    fun getLastNumberOfFox(context: Context):Int {
        var count = 0
        db?.let {
            val helper = it.openHelper
            val dataBase = SQLiteDatabase.openOrCreateDatabase(context.getDatabasePath(helper.databaseName), null)
            val cursor = dataBase.rawQuery("SELECT seq FROM sqlite_sequence WHERE name = ?;", arrayOf("foxes"))
            if (cursor.moveToNext()) {
                count = cursor.getInt(cursor.getColumnIndex("seq"))
            }
            cursor.close()
        }
        return count
    }

    fun exportToCSV(context: Context) {
        
    }
}