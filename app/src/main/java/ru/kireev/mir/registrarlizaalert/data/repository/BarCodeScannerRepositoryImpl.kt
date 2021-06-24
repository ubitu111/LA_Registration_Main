package ru.kireev.mir.registrarlizaalert.data.repository

import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import ru.kireev.mir.registrarlizaalert.data.database.dao.VolunteersDao
import ru.kireev.mir.registrarlizaalert.data.database.entity.Volunteer
import ru.kireev.mir.registrarlizaalert.domain.repository.BarCodeScannerRepository

class BarCodeScannerRepositoryImpl(
    private val volunteersDao: VolunteersDao
) : BarCodeScannerRepository {

    override fun checkVolunteerExist(
        fullName: String,
        phoneNumber: String
    ): Single<Boolean> = volunteersDao
        .checkForVolunteerExist(
            fullName = fullName,
            phoneNumber = phoneNumber
        )
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())

    override fun insertVolunteer(volunteer: Volunteer) {
        volunteersDao.insertVolunteer(volunteer)
    }
}