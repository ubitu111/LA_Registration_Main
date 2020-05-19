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
        var callSign: String,
        var phoneNumber: String,
        var isSent: String,
        var carMark: String,
        var carModel: String,
        var carRegistrationNumber: String,
        var carColor: String,
        var isAddedToFox: String
) {
        override fun toString(): String {
                return "$name $surname ($callSign)"
        }
}