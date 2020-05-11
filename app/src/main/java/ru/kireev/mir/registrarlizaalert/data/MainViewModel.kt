package ru.kireev.mir.registrarlizaalert.data

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import io.reactivex.disposables.CompositeDisposable
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val db = VolunteersDatabase.getInstance(application)
    private val compositeDisposable = CompositeDisposable()

    val allVolunteers = db.volunteersDao().getAllVolunteers()
    val sentVolunteers = db.volunteersDao().getSentVolunteers()
    val notSentVolunteers = db.volunteersDao().getNotSentVolunteers()
    val addedToFoxVolunteers = db.volunteersDao().getAddedToFoxVolunteers()
    val notAddedToFoxVolunteers = db.volunteersDao().getNotAddedToFoxVolunteers()

    fun insertVolunteer (volunteer: Volunteer) {
        GlobalScope.launch {
            db.volunteersDao().insertVolunteer(volunteer)
        }
    }

    fun deleteVolunteer (volunteer: Volunteer) {
        GlobalScope.launch {
            db.volunteersDao().deleteVolunteer(volunteer)
        }
    }

    fun deleteAllVolunteers() {
        GlobalScope.launch {
            db.volunteersDao().deleteAllVolunteers()
        }
    }
}