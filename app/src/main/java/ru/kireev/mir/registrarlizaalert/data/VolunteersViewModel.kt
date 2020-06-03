package ru.kireev.mir.registrarlizaalert.data

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class VolunteersViewModel(application: Application) : AndroidViewModel(application) {
    private val db = MainDatabase.getInstance(application)

    val allVolunteers = db.volunteersDao().getAllVolunteers()
    val sentVolunteers = db.volunteersDao().getSentVolunteers()
    val notSentVolunteers = db.volunteersDao().getNotSentVolunteers()
    val addedToFoxVolunteers = db.volunteersDao().getAddedToFoxVolunteers()

    fun getNotAddedToFoxAndActiveVolunteers(status: String) = db.volunteersDao().getNotAddedToFoxAndActiveVolunteers(status)

    fun insertVolunteer(volunteer: Volunteer) {
        viewModelScope.launch(Dispatchers.IO) {
            db.volunteersDao().insertVolunteer(volunteer)
        }
    }

    fun deleteVolunteer(volunteer: Volunteer) {
        viewModelScope.launch(Dispatchers.IO) {
            db.volunteersDao().deleteVolunteer(volunteer)
        }
    }

    fun deleteAllVolunteers() {
        viewModelScope.launch(Dispatchers.IO) {
            db.volunteersDao().deleteAllVolunteers()
        }
    }

    suspend fun getVolunteerById(id: Int?): Volunteer {
        return withContext(viewModelScope.coroutineContext + Dispatchers.IO) {
            db.volunteersDao().getVolunteerById(id)
        }
    }

    fun getVolunteersWithStatus(status: String) = db.volunteersDao().getVolunteersWithStatus(status)
}