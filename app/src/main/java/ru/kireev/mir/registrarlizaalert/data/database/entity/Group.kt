package ru.kireev.mir.registrarlizaalert.data.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import ru.kireev.mir.registrarlizaalert.data.database.GroupCallsigns
import ru.kireev.mir.registrarlizaalert.data.database.converter.Converter

@Entity(tableName = "groups",
        foreignKeys = [
                ForeignKey(
                        entity = Volunteer::class, parentColumns = arrayOf("uniqueId"),
                        childColumns = arrayOf("elderOfGroupId"),
                        onDelete = ForeignKey.CASCADE
                )
        ])
@TypeConverters(value = [Converter::class])
data class Group(
        @PrimaryKey(autoGenerate = true)
        var id: Int,
        var numberOfGroup: Int,
        var elderOfGroupId: Int? = null,
        var navigators: String = "",
        var walkieTalkies: String = "",
        var compasses: String = "",
        var lamps: String = "",
        var others: String = "",
        var task: String = "",
        var leavingTime: String = "",
        var returnTime: String = "",
        var dateOfCreation: String,
        var groupCallsign: GroupCallsigns,
        var archived: String = "false"
)