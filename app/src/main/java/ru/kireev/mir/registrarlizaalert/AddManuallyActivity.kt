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
        checkBoxHaveACar.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                editTextCarMark.visibility = View.VISIBLE
                editTextCarModel.visibility = View.VISIBLE
                editTextCarRegistrationNumber.visibility = View.VISIBLE
                editTextCarColor.visibility = View.VISIBLE
            } else {
                editTextCarMark.visibility = View.INVISIBLE
                editTextCarModel.visibility = View.INVISIBLE
                editTextCarRegistrationNumber.visibility = View.INVISIBLE
                editTextCarColor.visibility = View.INVISIBLE
            }
        }

        index = intent.getIntExtra("size", 0)
    }

    fun onClickSaveData(view: View) {
        val name = editTextName.text.toString().trim()
        val surname = editTextSurname.text.toString().trim()
        val callSign = editTextCallSign.text.toString().trim()
        val phoneNumber = editTextPhoneNumber.text.toString().trim()
        val haveACar = checkBoxHaveACar.isChecked
        val carMark = editTextCarMark.text.toString().trim()
        val carModel = editTextCarModel.text.toString().trim()
        val carRegistrationNumber = editTextCarRegistrationNumber.text.toString().trim()
        val carColor = editTextCarColor.text.toString().trim()

        if (name.isEmpty() || surname.isEmpty() || phoneNumber.isEmpty()) {
            Toast.makeText(this, getString(R.string.fill_in_fields_volunteer), Toast.LENGTH_SHORT).show()
        } else if (haveACar && (carMark.isEmpty() || carModel.isEmpty() || carRegistrationNumber.isEmpty() || carColor.isEmpty())) {
            Toast.makeText(this, getString(R.string.fill_in_fields_volunteer_car), Toast.LENGTH_SHORT).show()
        } else {
            val status = getString(R.string.volunteer_status_active)
            val volunteer = Volunteer(0, index, name, surname, callSign, phoneNumber,
                    carMark =  carMark, carModel =  carModel, carRegistrationNumber =  carRegistrationNumber, carColor =  carColor, status = status)
            viewModel.insertVolunteer(volunteer)
            onBackPressed()
        }
    }
}