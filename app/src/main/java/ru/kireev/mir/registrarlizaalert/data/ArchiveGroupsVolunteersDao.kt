package ru.kireev.mir.registrarlizaalert.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface ArchiveGroupsVolunteersDao {

    @Insert
    fun insertInArchive(archivedGroupsVolunteers: ArchivedGroupsVolunteers)

    @Query("DELETE FROM archived_groups_volunteers")
    fun deleteAllArchivedGroupsVolunteers()

}