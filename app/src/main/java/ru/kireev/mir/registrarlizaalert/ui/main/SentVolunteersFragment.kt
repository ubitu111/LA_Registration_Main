package ru.kireev.mir.registrarlizaalert.ui.main

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_tabbed_sent_volunteers.view.*
import ru.kireev.mir.registrarlizaalert.R
import ru.kireev.mir.registrarlizaalert.adapters.VolunteerAdapter
import ru.kireev.mir.registrarlizaalert.data.MainViewModel
import ru.kireev.mir.registrarlizaalert.data.Volunteer

class SentVolunteersFragment : Fragment() {

    private lateinit var viewModel: MainViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val root = inflater.inflate(R.layout.fragment_tabbed_sent_volunteers, container, false)
        val recyclerView = root.recyclerViewSentVolunteersTab
        val adapter = VolunteerAdapter()
        adapter.onVolunteerLongClickListener = object : VolunteerAdapter.OnVolunteerLongClickListener {
            override fun onLongClick(volunteer: Volunteer) {
                onClickDeleteVolunteer(volunteer)
            }
        }
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(context)
        val model by viewModels<MainViewModel>()
        viewModel = model
        viewModel.sentVolunteers.observe(viewLifecycleOwner, Observer {
            adapter.volunteers = it
        })
        return root
    }

    private fun onClickDeleteVolunteer(volunteer: Volunteer) {
        val alertDialog = AlertDialog.Builder(context)
        alertDialog.setTitle(getString(R.string.warning))
        alertDialog.setMessage(getString(R.string.message_confirm_delete_one))
        alertDialog.setPositiveButton(getString(R.string.delete_all)) { _, _->
            viewModel.deleteVolunteer(volunteer)
        }
        alertDialog.setNegativeButton(getString(R.string.cancel), null)
        alertDialog.show()
    }
}