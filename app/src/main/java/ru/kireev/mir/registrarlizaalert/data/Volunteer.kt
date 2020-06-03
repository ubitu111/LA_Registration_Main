package ru.kireev.mir.registrarlizaalert.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "volunteers")
data class Volunteer(
        @PrimaryKey(autoGenerate = true)
        var uniqueId: Int,
        var index: Int,
        var name: String,
        var surname: String,
        var callSign: String = "",
        var phoneNumber: String,
        var isSent: String = "false",
        var carMark: String = "",
        var carModel: String = "",
        var carRegistrationNumber: String = "",
        var carColor: String = "",
        var isAddedToFox: String = "false",
        var status: String,
        var notifyThatLeft: String = "false",
        var timeForSearch: String = ""
) {
    override fun toString(): String {
        return "$name $surname ($callSign)"
    }
}