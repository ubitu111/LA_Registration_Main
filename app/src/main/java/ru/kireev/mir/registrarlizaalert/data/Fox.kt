package ru.kireev.mir.registrarlizaalert.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "foxes")
data class Fox(
        @PrimaryKey(autoGenerate = true)
        var id: Int,
        var numberOfFox: String,
        var elderOfFox: Volunteer,
        var membersOfFox: List<Volunteer>,
        var navigators: String,
        var walkieTalkies: String,
        var compasses: String,
        var lamps: String,
        var others: String,
        var task: String,
        var leavingTime: String,
        var returnTime: String
)