package ru.kireev.mir.registrarlizaalert.data

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface GroupsDao {
    @Query("SELECT * FROM groups ORDER BY groupCallsign")
    fun getAllGroups(): LiveData<List<Group>>

    @Query("SELECT * FROM groups WHERE groupCallsign = :groupCallsign")
    fun getGroupByCallsign(groupCallsign: GroupCallsigns): LiveData<List<Group>>

    @Query("SELECT numberOfGroup FROM groups WHERE groupCallsign = :groupCallsign ORDER BY numberOfGroup DESC LIMIT 1")
    fun getLastNumberOfGroupByCallsign(groupCallsign: GroupCallsigns) : Int

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

    @Query("SELECT id FROM groups WHERE numberOfGroup == :numberOfGroup")
    fun getGroupIdByNumber(numberOfGroup: Int): Int
}