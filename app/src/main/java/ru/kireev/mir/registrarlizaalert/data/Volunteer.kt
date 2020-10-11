package ru.kireev.mir.registrarlizaalert.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(tableName = "volunteers",
        foreignKeys = [
            ForeignKey(
                    entity = Group::class, parentColumns = arrayOf("id"),
                    childColumns = arrayOf("groupId"),
                    onDelete = ForeignKey.SET_NULL
            )
        ])
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
        var status: String,
        var notifyThatLeft: String = "false",
        var timeForSearch: String = "",
        var groupId: Int? = null
) {
    override fun toString(): String {
        return "$fullName ($callSign)"
    }
}