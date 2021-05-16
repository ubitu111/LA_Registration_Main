package ru.kireev.mir.registrarlizaalert.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import ru.kireev.mir.registrarlizaalert.data.database.entity.ArchivedGroupsVolunteers

@Dao
interface ArchiveGroupsVolunteersDao {

    @Insert
    fun insertInArchive(archivedGroupsVolunteers: ArchivedGroupsVolunteers)

    @Query("DELETE FROM archived_groups_volunteers")
    fun deleteAllArchivedGroupsVolunteers()

}