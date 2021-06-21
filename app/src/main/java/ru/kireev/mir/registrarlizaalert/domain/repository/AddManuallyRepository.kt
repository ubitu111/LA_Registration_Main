package ru.kireev.mir.registrarlizaalert.domain.repository

import io.reactivex.Single
import ru.kireev.mir.registrarlizaalert.data.database.entity.Volunteer

interface AddManuallyRepository {
    fun getVolunteerById(id: Int): Single<Volunteer>
    fun updateVolunteer(volunteer: Volunteer)
    fun checkVolunteerExist(fullName: String, phoneNumber: String): Single<Boolean>
    fun insertVolunteer(volunteer: Volunteer)
}