package ru.kireev.mir.registrarlizaalert

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_add_manually.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import ru.kireev.mir.registrarlizaalert.data.Volunteer
import ru.kireev.mir.registrarlizaalert.data.VolunteersViewModel

class AddManuallyActivity : AppCompatActivity() {

    companion object {
        private const val EXTRA_SIZE = "size"
        private const val EXTRA_VOLUNTEER_ID = "volunteer_id"
    }

    private lateinit var viewModel: VolunteersViewModel
    private var index = 0
    private var volunteerId = 0
    private var isEdited = false
    private lateinit var volunteer: Volunteer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_manually)
        val model by viewModels<VolunteersViewModel>()
        viewModel = model
        index = intent.getIntExtra(EXTRA_SIZE, 0)
        volunteerId = intent.getIntExtra(EXTRA_VOLUNTEER_ID, 0)
        if (volunteerId != 0) {
            runBlocking {
                isEdited = true
                val job = launch {
                    volunteer = viewModel.getVolunteerById(volunteerId)
                    editTextFullName.setText(volunteer.fullName)
                    editTextCallSign.setText(volunteer.callSign)
                    editTextForumNickname.setText(volunteer.nickName)
                    editTextRegion.setText(volunteer.region)
                    editTextPhoneNumber.setText(volunteer.phoneNumber)
                    editTextCar.setText(volunteer.car)
                }
                job.join()
            }
        }
    }

    fun onClickSaveData(view: View) {
        val fullName = editTextFullName.text.toString().trim()
        val callSign = editTextCallSign.text.toString().trim()
        val nickName = editTextForumNickname.text.toString().trim()
        val region = editTextRegion.text.toString().trim()
        val phoneNumber = editTextPhoneNumber.text.toString().trim()
        val car = editTextCar.text.toString().trim()

        if (fullName.isEmpty() || phoneNumber.isEmpty()) {
            Toast.makeText(this, getString(R.string.fill_in_fields_volunteer), Toast.LENGTH_SHORT).show()
        } else if (isEdited) {
            volunteer.fullName = fullName
            volunteer.callSign = callSign
            volunteer.nickName = nickName
            volunteer.region = region
            volunteer.phoneNumber = phoneNumber
            volunteer.car = car
            viewModel.insertVolunteer(volunteer)
            onBackPressed()
        } else {
            val status = getString(R.string.volunteer_status_active)
            val volunteer = Volunteer(0, index, fullName, callSign, nickName, region, phoneNumber, car, status = status)
            runBlocking {
                val job = launch {
                    if (viewModel.checkForVolunteerExist(fullName, phoneNumber)) {
                        Toast.makeText(this@AddManuallyActivity, getString(R.string.warning_qr_code_scan_volunteer_exist_message), Toast.LENGTH_SHORT).show()
                    } else {
                        viewModel.insertVolunteer(volunteer)
                        onBackPressed()
                    }
                }
                job.join()
            }
        }
    }
}