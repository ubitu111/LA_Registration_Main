package ru.kireev.mir.registrarlizaalert.converters

import androidx.room.TypeConverter
import com.google.gson.Gson
import ru.kireev.mir.registrarlizaalert.data.GroupCallsigns
import ru.kireev.mir.registrarlizaalert.data.Volunteer

class Converter {
    @TypeConverter
    fun listVolunteersToString(volunteers: List<Volunteer>): String = Gson().toJson(volunteers)

    @TypeConverter
    fun stringToListVolunteers(listVolunteersAsString: String): List<Volunteer> = Gson().fromJson(listVolunteersAsString, Array<Volunteer>::class.java).toList()

    @TypeConverter
    fun volunteerToString(volunteer: Volunteer): String = Gson().toJson(volunteer)

    @TypeConverter
    fun stringToVolunteer(volunteerAsString: String): Volunteer = Gson().fromJson(volunteerAsString, Volunteer::class.java)

    @TypeConverter
    fun listIntToString(ids: List<Int>): String = Gson().toJson(ids)

    @TypeConverter
    fun stringToListInt(listIdsAsString: String): List<Int> = Gson().fromJson(listIdsAsString, Array<Int>::class.java).toList()

    @TypeConverter
    fun groupToString(group: GroupCallsigns): String = Gson().toJson(group)

    @TypeConverter
    fun stringToGroup(groupAsString: String): GroupCallsigns = Gson().fromJson(groupAsString, GroupCallsigns::class.java)
}
