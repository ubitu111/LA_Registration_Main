package ru.kireev.mir.registrarlizaalert.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.github.terrakok.cicerone.Router
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.rxkotlin.subscribeBy
import ru.kireev.mir.registrarlizaalert.R
import ru.kireev.mir.registrarlizaalert.data.database.entity.emptyVolunteer
import ru.kireev.mir.registrarlizaalert.domain.repository.AddManuallyRepository
import timber.log.Timber

class AddManuallyViewModel(
    private val repository: AddManuallyRepository,
    private val router: Router
) : ViewModel() {

    private val disposable = CompositeDisposable()
    private var volunteer = emptyVolunteer()
    private var isEdited = false

    private val _error = MutableLiveData<Int>()
    val error: LiveData<Int>
        get() = _error

    fun loadVolunteersData(id: Int?) {
        id?.let { volunteerId ->
            isEdited = true
            repository.getVolunteerById(volunteerId)
                .subscribeBy(
                    onSuccess = { volunteer = it },
                    onError = { Timber.e(it) }
                )
                .addTo(disposable)
        }
    }

    fun saveData(
        fullName: String,
        callSign: String,
        nickName: String,
        region: String,
        phoneNumber: String,
        car: String,
        index: Int
    ) {
        volunteer = volunteer.copy(
            _index = index,
            fullName = fullName,
            callSign = callSign,
            nickName = nickName,
            region = region,
            phoneNumber = phoneNumber,
            car = car
        )
        when {
            fullName.isEmpty() || phoneNumber.isEmpty() -> {
                _error.value =
                    R.string.fill_in_fields_volunteer
            }

            isEdited -> {
                repository.updateVolunteer(volunteer)
                router.exit()
            }

            else -> {
                repository.checkVolunteerExist(fullName, phoneNumber)
                    .subscribeBy(
                        onSuccess = { isExists ->
                            if (isExists) {
                                _error.value = R.string.warning_qr_code_scan_volunteer_exist_message
                            } else {
                                repository.insertVolunteer(volunteer)
                                router.exit()
                            }
                        }
                    )
                    .addTo(disposable)
            }
        }
    }

    override fun onCleared() {
        disposable.dispose()
        super.onCleared()
    }
}