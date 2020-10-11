package ru.kireev.mir.registrarlizaalert.data

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface VolunteersDao {
    @Query("SELECT * FROM volunteers ORDER BY `_index` ASC")
    fun getAllVolunteers(): LiveData<List<Volunteer>>

    @Query("SELECT * FROM volunteers WHERE isSent == 'true'")
    fun getSentVolunteers(): LiveData<List<Volunteer>>

    @Query("SELECT * FROM volunteers WHERE isSent == 'false'")
    fun getNotSentVolunteers(): LiveData<List<Volunteer>>

    @Query("SELECT * FROM volunteers WHERE groupId IS NOT NULL")
    fun getAddedToGroupVolunteers(): LiveData<List<Volunteer>>

    @Query("SELECT * FROM volunteers WHERE status == :status AND groupId IS NULL")
    fun getVolunteersByStatusAndNotAddedToGroup(status: String): LiveData<List<Volunteer>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertVolunteer(volunteer: Volunteer)

    @Update
    fun updateVolunteer(volunteer: Volunteer)

    @Delete
    fun deleteVolunteer(volunteer: Volunteer)

    @Query("DELETE FROM volunteers")
    fun deleteAllVolunteers()

    @Query("SELECT * FROM volunteers WHERE uniqueId == :id")
    fun getVolunteerById(id: Int?): Volunteer

    @Query("SELECT * FROM volunteers WHERE status == :status AND notifyThatLeft == 'false'")
    fun getVolunteersWithStatus(status: String): LiveData<List<Volunteer>>

    @Query("SELECT COUNT('uniqueId') > 0 FROM volunteers WHERE fullName == :fullName AND phoneNumber == :phoneNumber")
    fun checkForVolunteerExist(fullName: String, phoneNumber: String): Boolean

    @Query("SELECT * FROM volunteers WHERE groupId == :idOfGroup")
    fun getVolunteersByIdOfGroup(idOfGroup: Int): List<Volunteer>

    @Query("UPDATE volunteers SET groupId = NULL WHERE groupId = :groupId")
    fun clearVolunteersGroupId(groupId: Int)

    @Query("SELECT * FROM volunteers WHERE uniqueId IN (SELECT archivedVolunteerId FROM archived_groups_volunteers WHERE archivedGroupId = :idOfGroup)")
    fun getVolunteersByIdOfArchiveGroup(idOfGroup: Int): List<Volunteer>
}