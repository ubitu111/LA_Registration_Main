package ru.kireev.mir.registrarlizaalert.data.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey

@Entity(tableName = "archived_groups_volunteers",
        primaryKeys = ["archivedGroupId", "archivedVolunteerId"],
        foreignKeys = [
            ForeignKey(entity = Group::class, parentColumns = arrayOf("id"),
                    childColumns = arrayOf("archivedGroupId")),

            ForeignKey(entity = Volunteer::class, parentColumns = arrayOf("uniqueId"),
                    childColumns = arrayOf("archivedVolunteerId"))
        ])
data class ArchivedGroupsVolunteers(
        var archivedGroupId: Int,
        var archivedVolunteerId: Int
)