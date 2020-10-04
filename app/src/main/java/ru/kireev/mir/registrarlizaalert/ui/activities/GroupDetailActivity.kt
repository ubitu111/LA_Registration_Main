package ru.kireev.mir.registrarlizaalert.ui.activities

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_group_detail.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.kireev.mir.registrarlizaalert.R
import ru.kireev.mir.registrarlizaalert.adapters.VolunteerAdapter
import ru.kireev.mir.registrarlizaalert.data.Group
import ru.kireev.mir.registrarlizaalert.data.GroupsViewModel
import ru.kireev.mir.registrarlizaalert.data.VolunteersViewModel
import ru.kireev.mir.registrarlizaalert.listeners.OnVolunteerPhoneNumberClickListener
import ru.kireev.mir.registrarlizaalert.util.getGroupCallsignAsString

class GroupDetailActivity : AppCompatActivity() {

    private lateinit var volunteerAdapter: VolunteerAdapter
    private lateinit var elderAdapter: VolunteerAdapter
    private lateinit var groupsViewModel: GroupsViewModel
    private lateinit var volunteersViewModel: VolunteersViewModel
    private lateinit var group: Group

    companion object {
        private const val ARG_GROUP_ID = "group_id"
        fun getIntent(groupId: Int, context: Context) : Intent {
            val intent = Intent(context, GroupDetailActivity::class.java)
            intent.putExtra(ARG_GROUP_ID, groupId)
            return intent
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_group_detail)
        val volunteersModel by viewModels<VolunteersViewModel>()
        volunteersViewModel = volunteersModel
        val groupsModel by viewModels<GroupsViewModel>()
        groupsViewModel = groupsModel
        volunteerAdapter = VolunteerAdapter(this, volunteersViewModel, groupsViewModel)
        elderAdapter = VolunteerAdapter(this, volunteersViewModel, groupsViewModel)

        val groupId = intent.getIntExtra(ARG_GROUP_ID, 0)

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
        rvGroupDetailInfoVolunteers.adapter = volunteerAdapter
        rvGroupDetailInfoVolunteers.layoutManager = LinearLayoutManager(this)
        rvGroupDetailInfoElder.adapter = elderAdapter
        rvGroupDetailInfoElder.layoutManager = LinearLayoutManager(this)

        CoroutineScope(Dispatchers.Main).launch {
            group = groupsViewModel.getGroupById(groupId)
            val groupCallsign = group.groupCallsign.getGroupCallsignAsString(this@GroupDetailActivity)
            val groupName = "$groupCallsign ${group.numberOfGroup}"
            supportActionBar?.title = groupName
            tvNumberOfGroup.text = groupName
            tvDateOfCreation.text = group.dateOfCreation
            val elder = volunteersViewModel.getVolunteerById(group.elderOfGroupId)
            elderAdapter.volunteers = listOf(elder)
            volunteerAdapter.volunteers = volunteersViewModel.getVolunteersByIdOfGroup(group.id).filter { it != elder }
            etTask.setText(group.task)
            etNavigators.setText(group.navigators)
            etWalkieTalkies.setText(group.walkieTalkies)
            etCompasses.setText(group.compasses)
            etLamps.setText(group.lamps)
            etOthers.setText(group.others)

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

    fun onClickSaveGroupData(view: View) {
        with(group) {
            task = etTask.text.toString().trim()
            navigators = etNavigators.text.toString().trim()
            walkieTalkies = etWalkieTalkies.text.toString().trim()
            compasses = etCompasses.text.toString().trim()
            lamps = etLamps.text.toString().trim()
            others = etOthers.text.toString().trim()
            groupsViewModel.updateGroup(this)
        }
        Toast.makeText(this, getString(R.string.data_saved), Toast.LENGTH_SHORT).show()
        onBackPressed()
    }

    private fun makeCall(phone: String) {
        val toDial = "tel:$phone"
        startActivity(Intent(Intent.ACTION_DIAL, Uri.parse(toDial)))
    }

}