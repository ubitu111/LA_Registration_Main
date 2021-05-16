package ru.kireev.mir.registrarlizaalert.data.database.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import ru.kireev.mir.registrarlizaalert.data.database.GroupCallsigns
import ru.kireev.mir.registrarlizaalert.data.database.entity.Group

@Dao
interface GroupsDao {
    @Query("SELECT * FROM groups ORDER BY groupCallsign")
    fun getAllGroups(): LiveData<List<Group>>

    @Query("SELECT * FROM groups WHERE groupCallsign = :groupCallsign AND archived = 'false'")
    fun getGroupsByCallsignNotArchived(groupCallsign: GroupCallsigns): LiveData<List<Group>>

    @Query("SELECT * FROM groups WHERE groupCallsign = :groupCallsign AND archived = 'true'")
    fun getGroupsByCallsignArchived(groupCallsign: GroupCallsigns): LiveData<List<Group>>

    @Query("SELECT numberOfGroup FROM groups WHERE groupCallsign = :groupCallsign ORDER BY numberOfGroup DESC LIMIT 1")
    fun getLastNumberOfGroupByCallsign(groupCallsign: GroupCallsigns): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertGroup(group: Group): Long

    @Delete
    fun deleteGroup(group: Group)

    @Query("DELETE FROM groups")
    fun deleteAllGroups()

    @Update
    fun updateGroup(group: Group)

    @Query("SELECT * FROM groups WHERE id = :id")
    fun getGroupById(id: Int): Group

    @Query("SELECT id FROM groups WHERE numberOfGroup = :numberOfGroup")
    fun getGroupIdByNumber(numberOfGroup: Int): Int

    @Query("SELECT * FROM groups LEFT JOIN archived_groups_volunteers ON archived_groups_volunteers.archivedGroupId = groups.id LEFT JOIN volunteers ON volunteers.uniqueId = archived_groups_volunteers.archivedVolunteerId WHERE id = :id")
    fun getArchivedGroupById(id: Int): Group
}