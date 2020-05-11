package ru.kireev.mir.registrarlizaalert.data

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface VolunteersDao {
    @Query("SELECT * FROM volunteers ORDER BY `index` ASC")
    fun getAllVolunteers(): LiveData<List<Volunteer>>

    @Query("SELECT * FROM volunteers WHERE isSent == 'true'")
    fun getSentVolunteers(): LiveData<List<Volunteer>>

    @Query("SELECT * FROM volunteers WHERE isSent == 'false'")
    fun getNotSentVolunteers(): LiveData<List<Volunteer>>

    @Query("SELECT * FROM volunteers WHERE isAddedToFox == 'true'")
    fun getAddedToFoxVolunteers(): LiveData<List<Volunteer>>

    @Query("SELECT * FROM volunteers WHERE isAddedToFox == 'false'")
    fun getNotAddedToFoxVolunteers(): LiveData<List<Volunteer>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertVolunteer(volunteer: Volunteer)

    @Delete
    fun deleteVolunteer(volunteer: Volunteer)

    @Query("DELETE FROM volunteers")
    fun deleteAllVolunteers()
}