package ru.kireev.mir.registrarlizaalert.ui.fragments.tabs.groups

import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_active_group.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import ru.kireev.mir.registrarlizaalert.R
import ru.kireev.mir.registrarlizaalert.data.database.GroupCallsigns
import ru.kireev.mir.registrarlizaalert.data.database.entity.Group
import ru.kireev.mir.registrarlizaalert.presentation.extention.getGroupCallsignAsString
import ru.kireev.mir.registrarlizaalert.presentation.viewmodel.GroupsViewModel
import ru.kireev.mir.registrarlizaalert.presentation.viewmodel.VolunteersViewModel
import ru.kireev.mir.registrarlizaalert.ui.activities.AddNewGroupActivity
import ru.kireev.mir.registrarlizaalert.ui.activities.GroupDetailActivity
import ru.kireev.mir.registrarlizaalert.ui.adapters.GroupsAdapter
import ru.kireev.mir.registrarlizaalert.ui.listeners.OnClickGroupOptionsMenu
import ru.kireev.mir.registrarlizaalert.ui.listeners.OnGroupClickListener
import ru.kireev.mir.registrarlizaalert.ui.listeners.OnVolunteerPhoneNumberClickListener

class ActiveGroupFragment : Fragment(), View.OnClickListener {

    private val groupsViewModel: GroupsViewModel by inject()
    private val volunteersViewModel: VolunteersViewModel by inject()

    private var groupCallsign: GroupCallsigns? = null

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
        groupsAdapter = GroupsAdapter(activity as AppCompatActivity, volunteersViewModel)
        groupsAdapter.onVolunteerPhoneNumberClickListener = object : OnVolunteerPhoneNumberClickListener {
            override fun onVolunteerPhoneNumberClick(phone: String) {
                val toDial = "tel:$phone"
                startActivity(Intent(Intent.ACTION_DIAL, Uri.parse(toDial)))
            }
        }
        groupsAdapter.onClickGroupOptionsMenu = object : OnClickGroupOptionsMenu {
            override fun onGroupOptionsMenuClick(textView: TextView, group: Group) {
                showPopup(textView, group)
            }

        }
        groupsAdapter.onGroupClickListener = object : OnGroupClickListener {
            override fun onGroupClick(position: Int) {
                val groupId = groupsAdapter.groups[position].id
                val intent = GroupDetailActivity.getIntent(groupId, activity as AppCompatActivity, false)
                startActivity(intent)
            }
        }

        view.rv_active_group_main.adapter = groupsAdapter
        view.rv_active_group_main.layoutManager = LinearLayoutManager(activity)
        groupsViewModel.getGroupByCallsignNotArchived(groupCallsign ?: GroupCallsigns.LISA).observe(viewLifecycleOwner) {
            groupsAdapter.groups = it
        }

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

    private fun showDialogForAddToArchive(group: Group) {
        CoroutineScope(Dispatchers.Main).launch {
            val alertDialog = AlertDialog.Builder(activity)
            alertDialog.setTitle(getString(R.string.warning))
            alertDialog.setMessage(getString(R.string.message_confirm_add_to_archive))
            alertDialog.setPositiveButton(getString(R.string.group_options_menu_send_to_archive)) { _, _ ->
                groupsViewModel.insertInArchive(group)
            }
            alertDialog.setNegativeButton(getString(R.string.cancel), null)
            alertDialog.show()
        }
    }

    private fun showPopup(textView: TextView, group: Group) {
        val popup = PopupMenu(context, textView)
        popup.inflate(R.menu.group_item_options_menu)
        popup.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.group_options_menu_archive -> {
                    showDialogForAddToArchive(group)
                    true
                }
            }
            false
        }
        popup.show()
    }


}