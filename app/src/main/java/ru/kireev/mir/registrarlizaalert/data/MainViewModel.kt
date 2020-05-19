package ru.kireev.mir.registrarlizaalert.data

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.reactivex.disposables.CompositeDisposable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val db = VolunteersDatabase.getInstance(application)

    val allVolunteers = db.volunteersDao().getAllVolunteers()
    val sentVolunteers = db.volunteersDao().getSentVolunteers()
    val notSentVolunteers = db.volunteersDao().getNotSentVolunteers()
    val addedToFoxVolunteers = db.volunteersDao().getAddedToFoxVolunteers()
    val notAddedToFoxVolunteers = db.volunteersDao().getNotAddedToFoxVolunteers()

    fun insertVolunteer (volunteer: Volunteer) {
        viewModelScope.launch (Dispatchers.IO) {
            db.volunteersDao().insertVolunteer(volunteer)
        }
    }

    fun deleteVolunteer (volunteer: Volunteer) {
        viewModelScope.launch (Dispatchers.IO) {
            db.volunteersDao().deleteVolunteer(volunteer)
        }
    }

    fun deleteAllVolunteers() {
        viewModelScope.launch (Dispatchers.IO) {
            db.volunteersDao().deleteAllVolunteers()
        }
    }
}