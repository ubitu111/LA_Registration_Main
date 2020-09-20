package ru.kireev.mir.registrarlizaalert

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
import ru.kireev.mir.registrarlizaalert.data.VolunteersViewModel
import ru.kireev.mir.registrarlizaalert.listeners.OnVolunteerPhoneNumberClickListener

class FoxDetailActivity : AppCompatActivity() {

    private lateinit var volunteerAdapter: VolunteerAdapter
    private lateinit var elderAdapter: VolunteerAdapter
    private lateinit var foxesViewModel: FoxesViewModel
    private lateinit var volunteersViewModel: VolunteersViewModel
    private lateinit var fox: Fox

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fox_detail)
        val volunteersModel by viewModels<VolunteersViewModel>()
        volunteersViewModel = volunteersModel
        volunteerAdapter = VolunteerAdapter(this, volunteersViewModel)
        elderAdapter = VolunteerAdapter(this, volunteersViewModel)
        val foxesModel by viewModels<FoxesViewModel>()
        foxesViewModel = foxesModel
        val foxId = intent.getIntExtra("fox_id", 0)

        volunteerAdapter.onVolunteerPhoneNumberClickListener = object : OnVolunteerPhoneNumberClickListener {
            override fun onVolunteerPhoneNumberClick(phone: String) {
                makeCall(phone)
            }
        }

        elderAdapter.onVolunteerPhoneNumberClickListener = object : OnVolunteerPhoneNumberClickListener {
            override fun onVolunteerPhoneNumberClick(phone: String) {
                makeCall(phone)
            }
        }
        rvFoxDetailInfoVolunteers.adapter = volunteerAdapter
        rvFoxDetailInfoVolunteers.layoutManager = LinearLayoutManager(this)
        rvFoxDetailInfoElder.adapter = elderAdapter
        rvFoxDetailInfoElder.layoutManager = LinearLayoutManager(this)

        CoroutineScope(Dispatchers.Main).launch {
            fox = foxesViewModel.getFoxById(foxId)
            tvNumberOfFox.text = String.format(getString(R.string.foxes_item_number_of_fox), fox.numberOfFox)
            tvDateOfCreation.text = fox.dateOfCreation
            val elder = volunteersViewModel.getVolunteerById(fox.elderOfFoxId)
            elderAdapter.volunteers = listOf(elder)
            volunteerAdapter.volunteers = volunteersViewModel.getVolunteersByNumberOfGroup(fox.numberOfFox).filter { it != elder }
            etTask.setText(fox.task)
            etNavigators.setText(fox.navigators)
            etWalkieTalkies.setText(fox.walkieTalkies)
            etCompasses.setText(fox.compasses)
            etLamps.setText(fox.lamps)
            etOthers.setText(fox.others)

            //слушатель изменения статуса у старшего
            elderAdapter.onChangeVolunteerStatusListener = object : VolunteerAdapter.OnChangeVolunteerStatusListener {
                override fun onStatusChanged(position: Int) {
                    elderAdapter.notifyItemChanged(position)
                }
            }
            //слушатель изменения времени на поиск у старшего
            elderAdapter.onVolunteerChangeTimeToSearchListener = object : VolunteerAdapter.OnVolunteerChangeTimeToSearchListener {
                override fun onTimeChanged(position: Int) {
                    elderAdapter.notifyItemChanged(position)
                }
            }

            //слушатель изменения статуса у поисковиков
            volunteerAdapter.onChangeVolunteerStatusListener = object : VolunteerAdapter.OnChangeVolunteerStatusListener {
                override fun onStatusChanged(position: Int) {
                    volunteerAdapter.notifyItemChanged(position)
                }
            }
            //слушатель изменения времени на поиск у поисковиков
            volunteerAdapter.onVolunteerChangeTimeToSearchListener = object : VolunteerAdapter.OnVolunteerChangeTimeToSearchListener {
                override fun onTimeChanged(position: Int) {
                    volunteerAdapter.notifyItemChanged(position)
                }
            }
        }
    }

    fun onClickSaveFoxData(view: View) {
        with(fox) {
            task = etTask.text.toString().trim()
            navigators = etNavigators.text.toString().trim()
            walkieTalkies = etWalkieTalkies.text.toString().trim()
            compasses = etCompasses.text.toString().trim()
            lamps = etLamps.text.toString().trim()
            others = etOthers.text.toString().trim()
            foxesViewModel.updateFox(this)
        }
        Toast.makeText(this, getString(R.string.data_saved), Toast.LENGTH_SHORT).show()
        onBackPressed()
    }

    private fun makeCall(phone: String) {
        val toDial = "tel:$phone"
        startActivity(Intent(Intent.ACTION_DIAL, Uri.parse(toDial)))
    }

}