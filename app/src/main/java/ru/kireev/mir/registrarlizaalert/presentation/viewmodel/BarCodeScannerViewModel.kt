package ru.kireev.mir.registrarlizaalert.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.yanzhenjie.zbar.SymbolSet
import ru.kireev.mir.registrarlizaalert.R
import ru.kireev.mir.registrarlizaalert.data.database.entity.emptyVolunteer
import ru.kireev.mir.registrarlizaalert.domain.repository.BarCodeScannerRepository
import ru.kireev.mir.registrarlizaalert.presentation.BaseViewModel

class BarCodeScannerViewModel(
    private val repository: BarCodeScannerRepository
) : BaseViewModel() {

    private val delimiterScanResult = "\n"
    private val volunteer = emptyVolunteer()

    private val _result = MutableLiveData<Int>()
    val result: LiveData<Int>
        get() = _result

    fun handleScanResults(
        symbolSet: SymbolSet,
        templateFullName: String,
        templateCar: String,
        index: Int
    ) {
        for (symbols in symbolSet) {
            volunteer._index = index
            val scanResultArray = symbols.data.trim().split(delimiterScanResult)
            val possibleFullName =
                String.format(templateFullName, scanResultArray[0], scanResultArray[1])
            when (scanResultArray.size) {
                ScanResultTemplate.NAME_SURNAME_PHONE -> volunteer.apply {
                    fullName = possibleFullName
                    phoneNumber = scanResultArray[2]
                    insertVolunteer()
                }

                ScanResultTemplate.NAME_SURNAME_PHONE_CALLSIGN -> volunteer.apply {
                    fullName = possibleFullName
                    callSign = scanResultArray[2]
                    phoneNumber = scanResultArray[3]
                    insertVolunteer()
                }

                ScanResultTemplate.ALL_FIELDS -> volunteer.apply {
                    fullName = scanResultArray[0]
                    callSign = scanResultArray[1]
                    nickName = scanResultArray[2]
                    region = scanResultArray[3]
                    phoneNumber = scanResultArray[4]
                    car = scanResultArray[5]
                    insertVolunteer()
                }

                ScanResultTemplate.LEGACY_VERSION_NO_CALLSIGN -> volunteer.apply {
                    fullName = possibleFullName
                    phoneNumber = scanResultArray[2]
                    car = getFormattedCar(
                        template = templateCar,
                        carModel = "${scanResultArray[3]} ${scanResultArray[4]}",
                        carNumber = scanResultArray[5]
                    )
                    insertVolunteer()
                }

                ScanResultTemplate.LEGACY_VERSION_ALL_FIELDS -> volunteer.apply {
                    fullName = possibleFullName
                    callSign = scanResultArray[2]
                    phoneNumber = scanResultArray[3]
                    car = getFormattedCar(
                        template = templateCar,
                        carModel = "${scanResultArray[4]} ${scanResultArray[5]}",
                        carNumber = scanResultArray[6]
                    )
                    insertVolunteer()
                }

                else -> _result.value = R.string.error_qr_code_scan_message
            }
        }
    }

    private fun getFormattedCar(
        template: String,
        carModel: String,
        carNumber: String
    ) = String.format(template, carModel, carNumber)

    private fun insertVolunteer() {
        repository.checkVolunteerExist(volunteer.fullName, volunteer.phoneNumber)
            .subscribe(::handleVolunteerCheck)
    }

    private fun handleVolunteerCheck(isExist: Boolean) = if (isExist) {
        _result.value = R.string.warning_qr_code_scan_volunteer_exist_message
    } else {
        repository.insertVolunteer(volunteer)
        _result.value = R.string.success_qr_code_scan_message
    }
}

private object ScanResultTemplate {

    const val NAME_SURNAME_PHONE = 3
    const val NAME_SURNAME_PHONE_CALLSIGN = 4
    const val ALL_FIELDS = 6
    const val LEGACY_VERSION_NO_CALLSIGN = 7
    const val LEGACY_VERSION_ALL_FIELDS = 8
}

