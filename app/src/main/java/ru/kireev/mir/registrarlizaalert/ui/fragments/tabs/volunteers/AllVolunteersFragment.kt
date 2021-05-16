package ru.kireev.mir.registrarlizaalert.ui.fragments.tabs.volunteers

import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.*
import android.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_tabbed_all_volunteers.view.*
import ru.kireev.mir.registrarlizaalert.R
import ru.kireev.mir.registrarlizaalert.data.database.entity.Volunteer
import ru.kireev.mir.registrarlizaalert.presentation.viewmodel.GroupsViewModel
import ru.kireev.mir.registrarlizaalert.presentation.viewmodel.MainViewModel
import ru.kireev.mir.registrarlizaalert.presentation.viewmodel.VolunteersViewModel
import ru.kireev.mir.registrarlizaalert.ui.activities.AddManuallyActivity
import ru.kireev.mir.registrarlizaalert.ui.adapters.VolunteerAdapter
import ru.kireev.mir.registrarlizaalert.ui.listeners.OnVolunteerClickListener
import ru.kireev.mir.registrarlizaalert.ui.listeners.OnVolunteerPhoneNumberClickListener

class AllVolunteersFragment : Fragment(), View.OnClickListener, SearchView.OnQueryTextListener {

    companion object {
        private const val EXTRA_SIZE = "size"
        private const val EXTRA_VOLUNTEER_ID = "volunteer_id"
    }

    private lateinit var groupsViewModel: GroupsViewModel
    private lateinit var volunteersViewModel: VolunteersViewModel
    private lateinit var mainViewModel: MainViewModel
    private lateinit var adapter: VolunteerAdapter
    private var fullList = listOf<Volunteer>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val root = inflater.inflate(R.layout.fragment_tabbed_all_volunteers, container, false)
        val recyclerView = root.recyclerViewAllVolunteersTab
        val groupModel by viewModels<GroupsViewModel>()
        groupsViewModel = groupModel
        groupsViewModel.allGroups.observe(viewLifecycleOwner, { })
        val model by viewModels<VolunteersViewModel>()
        volunteersViewModel = model
        val mainModel by viewModels<MainViewModel>()
        mainViewModel = mainModel
        adapter = VolunteerAdapter(requireContext(), volunteersViewModel, groupsViewModel)
        adapter.onVolunteerPhoneNumberClickListener = object : OnVolunteerPhoneNumberClickListener {
            override fun onVolunteerPhoneNumberClick(phone: String) {
                val toDial = "tel:$phone"
                startActivity(Intent(Intent.ACTION_DIAL, Uri.parse(toDial)))
            }
        }
        adapter.onVolunteerClickListener = object : OnVolunteerClickListener {
            override fun onVolunteerClick(position: Int) {
                val volunteerId = adapter.volunteers[position].uniqueId
                val intent = Intent(context, AddManuallyActivity::class.java)
                intent.putExtra(EXTRA_VOLUNTEER_ID, volunteerId)
                intent.putExtra(EXTRA_SIZE, adapter.itemCount)
                startActivity(intent)
            }
        }
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(context)

        volunteersViewModel.allVolunteers.observe(viewLifecycleOwner, {
            adapter.volunteers = it
            fullList = it
        })
        val fab = root.fab_buttonDeleteAllTab
        fab.setOnClickListener(this)
        setHasOptionsMenu(true)
        return root
    }

    override fun onClick(v: View?) {
        val alertDialog = AlertDialog.Builder(context)
        alertDialog.setTitle(getString(R.string.warning))
        alertDialog.setMessage(getString(R.string.message_confirm_delete_all))
        alertDialog.setPositiveButton(getString(R.string.delete_all)) { _, _ ->
            volunteersViewModel.deleteAllVolunteers()
            groupsViewModel.deleteAllGroups()
            mainViewModel.clearAutoIncrementCounter()
        }
        alertDialog.setNegativeButton(getString(R.string.cancel), null)
        alertDialog.show()
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        adapter.volunteers = adapter.filterVolunteers(query, fullList)
        return false
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        adapter.volunteers = adapter.filterVolunteers(newText, fullList)
        return false
    }

    override fun onPause() {
        adapter.volunteers = fullList
        super.onPause()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        val searchView = menu.findItem(R.id.search_view).actionView as SearchView
        with(searchView) {
            isFocusable = false
            queryHint = getString(R.string.search_searching)
            setOnQueryTextListener(this@AllVolunteersFragment)
            setOnCloseListener {
                adapter.volunteers = fullList
                false
            }
        }
    }
}