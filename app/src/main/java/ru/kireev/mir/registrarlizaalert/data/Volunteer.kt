package ru.kireev.mir.registrarlizaalert.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "volunteers")
data class Volunteer(
        @PrimaryKey(autoGenerate = true)
        var uniqueId: Int,
        var _index: Int,
        var fullName: String,
        var callSign: String = "",
        var nickName: String = "",
        var region: String = "",
        var phoneNumber: String,
        var car: String = "",
        var isSent: String = "false",
        var isAddedToFox: String = "false",
        var status: String,
        var notifyThatLeft: String = "false",
        var timeForSearch: String = ""
) {
    override fun toString(): String {
        return "$fullName ($callSign)"
    }
}