package ru.kireev.mir.registrarlizaalert

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import kotlinx.android.synthetic.main.activity_add_new_fox.*
import ru.kireev.mir.registrarlizaalert.adapters.VolunteerAutoCompleteAdapter
import ru.kireev.mir.registrarlizaalert.data.MainViewModel
import ru.kireev.mir.registrarlizaalert.data.Volunteer

class AddNewFoxActivity : AppCompatActivity() {
    private lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_new_fox)
        val autoCompleteAdapter = VolunteerAutoCompleteAdapter(this, arrayListOf())
        val model by viewModels<MainViewModel>()
        viewModel = model
        viewModel.notAddedToFoxVolunteers.observe(this, Observer {
            autoCompleteAdapter.fullList = it
        })
        actvAddFoxElder.setAdapter(autoCompleteAdapter)
        actvAddFoxSearcher.setAdapter(autoCompleteAdapter)

        actvAddFoxElder.setOnItemClickListener { parent, _, position, _ ->
            markSelectedItem(parent, position)
        }

        actvAddFoxSearcher.setOnItemClickListener { parent, _, position, _ ->
            markSelectedItem(parent, position)
        }
    }

    private fun markSelectedItem(parent: AdapterView<*>, position: Int) {
        val volunteer = parent.getItemAtPosition(position) as Volunteer
        volunteer.isAddedToFox = "true"
        viewModel.insertVolunteer(volunteer)
    }

    fun onClickSaveFox(view: View) {

    }

    fun onClickAddNewMember(view: View) {

    }


}