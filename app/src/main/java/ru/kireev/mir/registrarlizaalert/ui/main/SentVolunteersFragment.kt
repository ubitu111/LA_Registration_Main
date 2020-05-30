package ru.kireev.mir.registrarlizaalert.ui.main

import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.*
import android.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_tabbed_sent_volunteers.view.*
import ru.kireev.mir.registrarlizaalert.R
import ru.kireev.mir.registrarlizaalert.adapters.VolunteerAdapter
import ru.kireev.mir.registrarlizaalert.data.Volunteer
import ru.kireev.mir.registrarlizaalert.data.VolunteersViewModel
import ru.kireev.mir.registrarlizaalert.listeners.OnVolunteerLongClickListener
import ru.kireev.mir.registrarlizaalert.listeners.OnVolunteerPhoneNumberClickListener

class SentVolunteersFragment : Fragment(), SearchView.OnQueryTextListener {

    private lateinit var viewModel: VolunteersViewModel
    private lateinit var adapter: VolunteerAdapter
    private var fullList = listOf<Volunteer>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val root = inflater.inflate(R.layout.fragment_tabbed_sent_volunteers, container, false)
        val recyclerView = root.recyclerViewSentVolunteersTab
        adapter = VolunteerAdapter()
        adapter.onVolunteerLongClickListener = object : OnVolunteerLongClickListener {
            override fun onLongVolunteerClick(volunteer: Volunteer) {
                onClickDeleteVolunteer(volunteer)
            }
        }
        adapter.onVolunteerPhoneNumberClickListener = object : OnVolunteerPhoneNumberClickListener {
            override fun onVolunteerPhoneNumberClick(phone: String) {
                val toDial = "tel:$phone"
                startActivity(Intent(Intent.ACTION_DIAL, Uri.parse(toDial)))
            }
        }
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(context)
        val model by viewModels<VolunteersViewModel>()
        viewModel = model
        viewModel.sentVolunteers.observe(viewLifecycleOwner, Observer {
            adapter.volunteers = it
            fullList = it
        })
        setHasOptionsMenu(true)
        return root
    }

    private fun onClickDeleteVolunteer(volunteer: Volunteer) {
        val alertDialog = AlertDialog.Builder(context)
        alertDialog.setTitle(getString(R.string.warning))
        alertDialog.setMessage(getString(R.string.message_confirm_delete_one))
        alertDialog.setPositiveButton(getString(R.string.delete_all)) { _, _ ->
            viewModel.deleteVolunteer(volunteer)
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
            setOnQueryTextListener(this@SentVolunteersFragment)
            setOnCloseListener {
                adapter.volunteers = fullList
                false
            }
        }
    }
}