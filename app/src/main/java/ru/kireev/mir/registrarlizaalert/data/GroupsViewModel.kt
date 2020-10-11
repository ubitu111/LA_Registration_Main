package ru.kireev.mir.registrarlizaalert.data

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class GroupsViewModel(private val app: Application) : AndroidViewModel(app) {
    private val db = MainDatabase.getInstance(app)


    val allGroups = db.groupsDao().getAllGroups()
    fun getGroupByCallsignNotArchived(groupCallsign: GroupCallsigns) = db.groupsDao().getGroupsByCallsignNotArchived(groupCallsign)
    fun getGroupByCallsignArchived(groupCallsign: GroupCallsigns) = db.groupsDao().getGroupsByCallsignArchived(groupCallsign)

    suspend fun insertGroup(group: Group): Int {
        return withContext(viewModelScope.coroutineContext + Dispatchers.IO) {
            db.groupsDao().insertGroup(group).toInt()
        }
    }

    fun updateGroup(group: Group) {
        viewModelScope.launch(Dispatchers.IO) {
            db.groupsDao().updateGroup(group)
        }
    }

    fun deleteGroup(group: Group) {
        viewModelScope.launch(Dispatchers.IO) {
            db.groupsDao().deleteGroup(group)
        }
    }

    fun deleteAllGroups() {
        viewModelScope.launch(Dispatchers.IO) {
            db.groupsDao().deleteAllGroups()
        }
    }

    suspend fun getLastNumberOfGroup(groupCallsign: GroupCallsigns): Int {
        return withContext(viewModelScope.coroutineContext + Dispatchers.IO) {
            db.groupsDao().getLastNumberOfGroupByCallsign(groupCallsign)
        }
    }

    suspend fun getGroupById(id: Int): Group {
        return withContext(viewModelScope.coroutineContext + Dispatchers.IO) { db.groupsDao().getGroupById(id) }
    }

    suspend fun getArchivedGroupById(id: Int): Group {
        return withContext(viewModelScope.coroutineContext + Dispatchers.IO) { db.groupsDao().getArchivedGroupById(id) }
    }

    suspend fun getGroupIdByNumber(numberOfGroup: Int): Int {
        return withContext(viewModelScope.coroutineContext + Dispatchers.IO) { db.groupsDao().getGroupIdByNumber(numberOfGroup) }
    }

    fun insertInArchive(group: Group) {
        viewModelScope.launch(Dispatchers.IO) {
            group.archived = "true"
            db.groupsDao().updateGroup(group)
            val groupVolunteers = db.volunteersDao().getVolunteersByIdOfGroup(group.id)
            for (volunteer in groupVolunteers) {
                val archivedGroupsVolunteers = ArchivedGroupsVolunteers(group.id, volunteer.uniqueId)
                db.archiveGroupsVolunteersDao().insertInArchive(archivedGroupsVolunteers)
            }
            db.volunteersDao().clearVolunteersGroupId(group.id)
        }
    }
}