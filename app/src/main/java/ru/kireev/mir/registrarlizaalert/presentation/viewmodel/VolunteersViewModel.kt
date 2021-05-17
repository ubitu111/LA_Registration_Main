package ru.kireev.mir.registrarlizaalert.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.kireev.mir.registrarlizaalert.data.database.MainDatabase
import ru.kireev.mir.registrarlizaalert.data.database.entity.Volunteer

class VolunteersViewModel(
    private val db: MainDatabase
) : ViewModel() {

    val allVolunteers = db.volunteersDao().getAllVolunteers()
    val sentVolunteers = db.volunteersDao().getSentVolunteers()
    val notSentVolunteers = db.volunteersDao().getNotSentVolunteers()
    val addedToGroupVolunteers = db.volunteersDao().getAddedToGroupVolunteers()

    fun getVolunteersByStatusAndNotAddedToGroup(status: String) = db.volunteersDao().getVolunteersByStatusAndNotAddedToGroup(status)

    fun insertVolunteer(volunteer: Volunteer) {
        viewModelScope.launch(Dispatchers.IO) {
            db.volunteersDao().insertVolunteer(volunteer)
        }
    }

    fun updateVolunteer(volunteer: Volunteer) {
        viewModelScope.launch(Dispatchers.IO) {
            db.volunteersDao().updateVolunteer(volunteer)
        }
    }

    fun deleteVolunteer(volunteer: Volunteer) {
        viewModelScope.launch(Dispatchers.IO) {
            db.volunteersDao().deleteVolunteer(volunteer)
        }
    }

    fun deleteAllVolunteers() {
        viewModelScope.launch(Dispatchers.IO) {
            db.archiveGroupsVolunteersDao().deleteAllArchivedGroupsVolunteers()
            db.volunteersDao().deleteAllVolunteers()
        }
    }

    suspend fun getVolunteerById(id: Int?): Volunteer {
        return withContext(viewModelScope.coroutineContext + Dispatchers.IO) {
            db.volunteersDao().getVolunteerById(id)
        }
    }

    fun getVolunteersWithStatus(status: String) = db.volunteersDao().getVolunteersWithStatus(status)

    suspend fun checkForVolunteerExist(fullName: String, phoneNumber: String): Boolean = withContext(viewModelScope.coroutineContext + Dispatchers.IO) {
        db.volunteersDao().checkForVolunteerExist(fullName, phoneNumber)
    }

    suspend fun getVolunteersByIdOfGroup(idOfGroup: Int): List<Volunteer> {
        return withContext(viewModelScope.coroutineContext + Dispatchers.IO) {
            db.volunteersDao().getVolunteersByIdOfGroup(idOfGroup)
        }
    }

    suspend fun getVolunteersByIdOfArchiveGroup(idOfGroup: Int) : List<Volunteer> {
        return withContext(viewModelScope.coroutineContext + Dispatchers.IO) {
            db.volunteersDao().getVolunteersByIdOfArchiveGroup(idOfGroup)
        }
    }
}