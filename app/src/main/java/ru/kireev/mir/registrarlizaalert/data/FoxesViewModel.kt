package ru.kireev.mir.registrarlizaalert.data

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FoxesViewModel(application: Application) : AndroidViewModel(application) {
    private val db = MainDatabase.getInstance(application)

    val allFoxes = db.foxesDao().getAllFoxes()

    fun insertFox(fox: Fox) {
        viewModelScope.launch(Dispatchers.IO) {
            db.foxesDao().insertFox(fox)
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

    suspend fun getLastNumberOfFox(): Int {
        return withContext(viewModelScope.coroutineContext + Dispatchers.IO) { db.foxesDao().getLastNumberOfFox() }
    }

    suspend fun getFoxById(id: Int): Fox {
        return withContext(viewModelScope.coroutineContext + Dispatchers.IO) { db.foxesDao().getFoxById(id)}
    }

}