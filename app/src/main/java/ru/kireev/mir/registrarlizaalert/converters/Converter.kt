package ru.kireev.mir.registrarlizaalert.converters

import androidx.room.TypeConverter
import com.google.gson.Gson
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
}
