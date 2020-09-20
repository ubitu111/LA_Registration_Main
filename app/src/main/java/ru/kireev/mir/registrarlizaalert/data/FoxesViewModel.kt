package ru.kireev.mir.registrarlizaalert.data

import android.app.Application
import android.database.sqlite.SQLiteDatabase
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FoxesViewModel(private val app: Application) : AndroidViewModel(app) {
    private val db = MainDatabase.getInstance(app)


    val allFoxes = db.foxesDao().getAllFoxes()

    suspend fun insertFox(fox: Fox): Int {
        return withContext(viewModelScope.coroutineContext + Dispatchers.IO) {
            db.foxesDao().insertFox(fox).toInt()
        }
    }

    fun updateFox(fox: Fox) {
        viewModelScope.launch(Dispatchers.IO) {
            db.foxesDao().updateFox(fox)
        }
    }

    fun deleteFox(fox: Fox) {
        viewModelScope.launch(Dispatchers.IO) {
            db.foxesDao().deleteFox(fox)
        }
    }

    fun deleteAllFoxes() {
        viewModelScope.launch(Dispatchers.IO) {
            db.foxesDao().deleteAllFoxes()
        }
    }

    //запрос, который возвращает количество ранее добавленных записей в таблицу foxes (по сути, счетчик auto_increment)
    suspend fun getLastNumberOfFox(): Int {
        return withContext(viewModelScope.coroutineContext + Dispatchers.IO) {
            var count = 0
            val helper = db.openHelper
            val dataBase = SQLiteDatabase.openOrCreateDatabase(app.getDatabasePath(helper.databaseName), null)
            val cursor = dataBase.rawQuery("SELECT seq FROM sqlite_sequence WHERE name = ?;", arrayOf("foxes"))
            if (cursor.moveToNext()) {
                count = cursor.getInt(cursor.getColumnIndex("seq"))
            }
            cursor.close()

            count
        }
    }

    suspend fun getFoxById(id: Int): Fox {
        return withContext(viewModelScope.coroutineContext + Dispatchers.IO) { db.foxesDao().getFoxById(id) }
    }

    suspend fun getFoxIdByNumber(numberOfFox: Int) : Int {
        return withContext(viewModelScope.coroutineContext + Dispatchers.IO) { db.foxesDao().getFoxIdByNumber(numberOfFox)}
    }
}