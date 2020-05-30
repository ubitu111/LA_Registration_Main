package ru.kireev.mir.registrarlizaalert

import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_fox_detail.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.kireev.mir.registrarlizaalert.adapters.VolunteerAdapter
import ru.kireev.mir.registrarlizaalert.data.Fox
import ru.kireev.mir.registrarlizaalert.data.FoxesViewModel
import ru.kireev.mir.registrarlizaalert.data.Volunteer
import ru.kireev.mir.registrarlizaalert.data.VolunteersViewModel
import ru.kireev.mir.registrarlizaalert.listeners.OnVolunteerLongClickListener
import ru.kireev.mir.registrarlizaalert.listeners.OnVolunteerPhoneNumberClickListener

class FoxDetailActivity : AppCompatActivity() {

    private val volunteerAdapter = VolunteerAdapter()
    private lateinit var foxesViewModel: FoxesViewModel
    private lateinit var volunteersViewModel: VolunteersViewModel
    private lateinit var fox: Fox

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fox_detail)
        val volunteersModel by viewModels<VolunteersViewModel>()
        volunteersViewModel = volunteersModel
        val foxesModel by viewModels<FoxesViewModel>()
        foxesViewModel = foxesModel
        val foxId = intent.getIntExtra("fox_id", 0)

        volunteerAdapter.onVolunteerLongClickListener = object : OnVolunteerLongClickListener {
            override fun onLongVolunteerClick(volunteer: Volunteer) {
                onClickDeleteVolunteer(volunteer)
            }
        }
        volunteerAdapter.onVolunteerPhoneNumberClickListener = object : OnVolunteerPhoneNumberClickListener {
            override fun onVolunteerPhoneNumberClick(phone: String) {
                val toDial = "tel:$phone"
                startActivity(Intent(Intent.ACTION_DIAL, Uri.parse(toDial)))
            }
        }
        rvFoxDetailInfoVolunteers.adapter = volunteerAdapter
        rvFoxDetailInfoVolunteers.layoutManager = LinearLayoutManager(this)

        CoroutineScope(Dispatchers.Main).launch{
            fox = foxesViewModel.getFoxById(foxId)
            tvNumberOfFox.text = String.format(getString(R.string.foxes_item_number_of_fox), fox.numberOfFox)
            tvDateOfCreation.text = fox.dateOfCreation
            tvElderOfFox.text = fox.elderOfFox.toString()
            tvElderOfFoxPhoneNumber.text = fox.elderOfFox.phoneNumber
            volunteerAdapter.volunteers = fox.membersOfFox
            etTask.setText(fox.task)
            etNavigators.setText(fox.navigators)
            etWalkieTalkies.setText(fox.walkieTalkies)
            etCompasses.setText(fox.compasses)
            etLamps.setText(fox.lamps)
            etOthers.setText(fox.others)
        }
    }


    private fun onClickDeleteVolunteer(volunteer: Volunteer) {
        val alertDialog = AlertDialog.Builder(this)
        alertDialog.setTitle(getString(R.string.warning))
        alertDialog.setMessage(getString(R.string.message_confirm_delete_one))
        alertDialog.setPositiveButton(getString(R.string.delete_all)) { _, _ ->
            val newMembers = fox.membersOfFox.toMutableList()
            newMembers.remove(volunteer)
            fox.membersOfFox = newMembers
            foxesViewModel.insertFox(fox)
            volunteerAdapter.volunteers = fox.membersOfFox
            volunteer.isAddedToFox = "false"
            volunteersViewModel.insertVolunteer(volunteer)
        }
        alertDialog.setNegativeButton(getString(R.string.cancel), null)
        alertDialog.show()
    }

    fun onClickSaveFoxData(view: View) {
        with(fox) {
            task = etTask.text.toString().trim()
            navigators = etNavigators.text.toString().trim()
            walkieTalkies = etWalkieTalkies.text.toString().trim()
            compasses = etCompasses.text.toString().trim()
            lamps = etLamps.text.toString().trim()
            others = etOthers.text.toString().trim()
            foxesViewModel.insertFox(this)
        }
        Toast.makeText(this, "Данные сохранены", Toast.LENGTH_SHORT).show()
        onBackPressed()
    }
}