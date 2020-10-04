package ru.kireev.mir.registrarlizaalert.ui.fragments.tabs.groups

import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_active_group.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.kireev.mir.registrarlizaalert.R
import ru.kireev.mir.registrarlizaalert.adapters.GroupsAdapter
import ru.kireev.mir.registrarlizaalert.data.Group
import ru.kireev.mir.registrarlizaalert.data.GroupCallsigns
import ru.kireev.mir.registrarlizaalert.data.GroupsViewModel
import ru.kireev.mir.registrarlizaalert.data.VolunteersViewModel
import ru.kireev.mir.registrarlizaalert.listeners.OnDeleteGroupClickListener
import ru.kireev.mir.registrarlizaalert.listeners.OnGroupClickListener
import ru.kireev.mir.registrarlizaalert.listeners.OnVolunteerPhoneNumberClickListener
import ru.kireev.mir.registrarlizaalert.ui.activities.AddNewGroupActivity
import ru.kireev.mir.registrarlizaalert.ui.activities.GroupDetailActivity
import ru.kireev.mir.registrarlizaalert.util.getGroupCallsignAsString

class ActiveGroupFragment : Fragment(), View.OnClickListener {

    private var groupCallsign: GroupCallsigns? = null
    private lateinit var groupsViewModel: GroupsViewModel
    private lateinit var volunteersViewModel: VolunteersViewModel
    private lateinit var groupsAdapter: GroupsAdapter

    companion object {
        private const val ARG_GROUP_CALLSIGN = "groupCallSign"

        @JvmStatic
        fun newInstance(groupCallsign: GroupCallsigns) =
                ActiveGroupFragment().apply {
                    arguments = Bundle().apply {
                        putSerializable(ARG_GROUP_CALLSIGN, groupCallsign)

                    }
                }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            groupCallsign = it.getSerializable(ARG_GROUP_CALLSIGN) as GroupCallsigns
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.fragment_active_group, container, false)
        val groupModel by viewModels<GroupsViewModel>()
        groupsViewModel = groupModel
        val volunteerModel by viewModels<VolunteersViewModel>()
        volunteersViewModel = volunteerModel
        groupsAdapter = GroupsAdapter(activity as AppCompatActivity, volunteersViewModel)
        groupsAdapter.onVolunteerPhoneNumberClickListener = object : OnVolunteerPhoneNumberClickListener {
            override fun onVolunteerPhoneNumberClick(phone: String) {
                val toDial = "tel:$phone"
                startActivity(Intent(Intent.ACTION_DIAL, Uri.parse(toDial)))
            }
        }
        groupsAdapter.onDeleteGroupClickListener = object : OnDeleteGroupClickListener {
            override fun onDeleteGroupClick(group: Group) {
                onClickDeleteGroup(group)
            }
        }
        groupsAdapter.onGroupClickListener = object : OnGroupClickListener {
            override fun onGroupClick(position: Int) {
                val groupId = groupsAdapter.groups[position].id
                val intent = GroupDetailActivity.getIntent(groupId, activity as AppCompatActivity)
                startActivity(intent)
            }
        }

        view.rv_active_group_main.adapter = groupsAdapter
        view.rv_active_group_main.layoutManager = LinearLayoutManager(activity)
        groupsViewModel.getGroupByCallsign(groupCallsign ?: GroupCallsigns.LISA).observe(viewLifecycleOwner, {
            groupsAdapter.groups = it
        })

        view.fab_button_active_group_add.setOnClickListener(this)
        return view
    }

    override fun onStart() {
        super.onStart()
        (activity as AppCompatActivity).supportActionBar?.title = groupCallsign?.getGroupCallsignAsString(context as AppCompatActivity)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.fab_button_active_group_add -> onClickAddNewGroup()
        }
    }

    private fun onClickAddNewGroup() {
        val intent = AddNewGroupActivity.getIntent(groupCallsign ?: GroupCallsigns.LISA, context)
        startActivity(intent)

        view?.fam_menu_active_group_main?.close(true)
    }

    private fun onClickDeleteGroup(group: Group) {
        CoroutineScope(Dispatchers.Main).launch {
            val alertDialog = AlertDialog.Builder(activity)
            alertDialog.setTitle(getString(R.string.warning))
            alertDialog.setMessage(getString(R.string.message_confirm_delete_one))
            alertDialog.setPositiveButton(getString(R.string.delete_all)) { _, _ ->
                groupsViewModel.deleteGroup(group)
            }
            alertDialog.setNegativeButton(getString(R.string.cancel), null)
            alertDialog.show()
        }
    }
}