package ru.kireev.mir.registrarlizaalert.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import ru.kireev.mir.registrarlizaalert.converters.Converter

@Entity(tableName = "foxes",
        foreignKeys = [
                ForeignKey(
                        entity = Volunteer::class, parentColumns = arrayOf("uniqueId"),
                        childColumns = arrayOf("elderOfFox"),
                        onDelete = ForeignKey.CASCADE,
                        onUpdate = ForeignKey.CASCADE
                )
        ])
@TypeConverters(value = [Converter::class])
data class Fox(
        @PrimaryKey(autoGenerate = true)
        var id: Int,
        var numberOfFox: Int,
        var elderOfFox: Int,
        var membersOfFox: List<Int>,
        var navigators: String = "",
        var walkieTalkies: String = "",
        var compasses: String = "",
        var lamps: String = "",
        var others: String = "",
        var task: String = "",
        var leavingTime: String = "",
        var returnTime: String = "",
        var dateOfCreation: String
)