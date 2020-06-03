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

    //метод сразу обновляет данные из БД волонтеров, если статус или время поменялись
    suspend fun getFoxById(id: Int): Fox {
        return withContext(viewModelScope.coroutineContext + Dispatchers.IO) {
            val fox = db.foxesDao().getFoxById(id)
            val elderId = fox.elderOfFox.uniqueId
            val elderVolunteer = db.volunteersDao().getVolunteerById(elderId)
            fox.elderOfFox = elderVolunteer

            val mutableMembers = fox.membersOfFox.toMutableList()
            for ((index, member) in mutableMembers.withIndex()) {
                val volunteer = db.volunteersDao().getVolunteerById(member.uniqueId)
                mutableMembers[index] = volunteer
            }
            fox.membersOfFox = mutableMembers
            db.foxesDao().insertFox(fox)
            return@withContext fox
        }
    }

}