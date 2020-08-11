package ru.kireev.mir.registrarlizaalert

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_add_manually.*
import ru.kireev.mir.registrarlizaalert.data.Volunteer
import ru.kireev.mir.registrarlizaalert.data.VolunteersViewModel

class AddManuallyActivity : AppCompatActivity() {
    private lateinit var viewModel: VolunteersViewModel
    private var index = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_manually)
        val model by viewModels<VolunteersViewModel>()
        viewModel = model
        index = intent.getIntExtra("size", 0)
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
        } else {
            val status = getString(R.string.volunteer_status_active)
            val volunteer = Volunteer(0, index, fullName, callSign, nickName, region, phoneNumber, car, status = status)
            viewModel.insertVolunteer(volunteer)
            onBackPressed()
        }
    }
}