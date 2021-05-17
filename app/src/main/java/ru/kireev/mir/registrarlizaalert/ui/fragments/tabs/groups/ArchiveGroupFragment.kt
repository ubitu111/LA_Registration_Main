package ru.kireev.mir.registrarlizaalert.ui.fragments.tabs.groups

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_archive_group.view.*
import org.koin.android.ext.android.inject
import ru.kireev.mir.registrarlizaalert.R
import ru.kireev.mir.registrarlizaalert.data.database.GroupCallsigns
import ru.kireev.mir.registrarlizaalert.presentation.viewmodel.GroupsViewModel
import ru.kireev.mir.registrarlizaalert.presentation.viewmodel.VolunteersViewModel
import ru.kireev.mir.registrarlizaalert.ui.activities.GroupDetailActivity
import ru.kireev.mir.registrarlizaalert.ui.adapters.GroupsAdapter
import ru.kireev.mir.registrarlizaalert.ui.listeners.OnGroupClickListener

class ArchiveGroupFragment : Fragment() {

    private val groupsViewModel: GroupsViewModel by inject()
    private val volunteersViewModel: VolunteersViewModel by inject()

    private var groupCallsign: GroupCallsigns? = null

    private lateinit var groupsAdapter: GroupsAdapter

    companion object {
        private const val ARG_GROUP_CALLSIGN = "groupCallSign"

        @JvmStatic
        fun newInstance(groupCallsign: GroupCallsigns) =
                ArchiveGroupFragment().apply {
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
        val view = inflater.inflate(R.layout.fragment_archive_group, container, false)

        groupsAdapter = GroupsAdapter(activity as AppCompatActivity, volunteersViewModel)

        groupsAdapter.onGroupClickListener = object : OnGroupClickListener {
            override fun onGroupClick(position: Int) {
                val groupId = groupsAdapter.groups[position].id
                val intent = GroupDetailActivity.getIntent(groupId, activity as AppCompatActivity, true)
                startActivity(intent)
            }
        }

        view.rv_archive_group_main.adapter = groupsAdapter
        view.rv_archive_group_main.layoutManager = LinearLayoutManager(activity)
        groupsViewModel.getGroupByCallsignArchived(groupCallsign ?: GroupCallsigns.LISA)
                .observe(viewLifecycleOwner) {
                    groupsAdapter.groups = it
                }
        return view
    }
}