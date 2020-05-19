package ru.kireev.mir.registrarlizaalert

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_add_manually.*
import ru.kireev.mir.registrarlizaalert.data.MainViewModel
import ru.kireev.mir.registrarlizaalert.data.Volunteer

class AddManuallyActivity : AppCompatActivity() {
    private lateinit var viewModel: MainViewModel
    private var index = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_manually)
        val model by viewModels<MainViewModel>()
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
        val name = editTextName.text.toString()
        val surname = editTextSurname.text.toString()
        val callSign = editTextCallSign.text.toString()
        val phoneNumber = editTextPhoneNumber.text.toString()
        val haveACar = checkBoxHaveACar.isChecked
        val carMark = editTextCarMark.text.toString()
        val carModel = editTextCarModel.text.toString()
        val carRegistrationNumber = editTextCarRegistrationNumber.text.toString()
        val carColor = editTextCarColor.text.toString()

        if (name.isEmpty() || surname.isEmpty() || phoneNumber.isEmpty()) {
            Toast.makeText(this, "Заполните поля - Имя, Фамилия и Номер телефона!", Toast.LENGTH_SHORT).show()
        } else if (haveACar && (carMark.isEmpty() || carModel.isEmpty() || carRegistrationNumber.isEmpty() || carColor.isEmpty())) {
            Toast.makeText(this, "Заполните все данные об автомобиле!", Toast.LENGTH_SHORT).show()
        } else {
            val volunteer = Volunteer(0, index, name, surname, callSign, phoneNumber,
                    "false", carMark, carModel, carRegistrationNumber, carColor, "false")
            viewModel.insertVolunteer(volunteer)
            onBackPressed()
        }
    }

//    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
//        menuInflater.inflate(R.menu.main_menu, menu)
//        return super.onCreateOptionsMenu(menu)
//    }
//
//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        when (item.itemId) {
//            R.id.menu_item_foxes -> {
//                val intentFoxes = Intent(this, FoxesMainActivity::class.java)
//                startActivity(intentFoxes)
//            }
//            R.id.menu_item_volunteers -> {
//                val intentVolunteers = Intent(this, TabbedMainActivity::class.java)
//                startActivity(intentVolunteers)
//            }
//        }
//
//        return super.onOptionsItemSelected(item)
//    }
}