package ru.kireev.mir.registrarlizaalert.ui.main

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.clans.fab.FloatingActionMenu
import kotlinx.android.synthetic.main.fragment_tabbed_not_sent_volunteers.view.*
import ru.kireev.mir.registrarlizaalert.AddManuallyActivity
import ru.kireev.mir.registrarlizaalert.BarCodeScannerActivity
import ru.kireev.mir.registrarlizaalert.R
import ru.kireev.mir.registrarlizaalert.adapters.VolunteerAdapter
import ru.kireev.mir.registrarlizaalert.data.MainViewModel
import ru.kireev.mir.registrarlizaalert.data.Volunteer

class NotSentVolunteersFragment : Fragment(), View.OnClickListener {
    companion object {
        private const val EXTRA_SIZE = "size"
    }

    private lateinit var viewModel: MainViewModel
    private lateinit var adapter: VolunteerAdapter
    private lateinit var famMenu: FloatingActionMenu
    private var isSentToInforg = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val root = inflater.inflate(R.layout.fragment_tabbed_not_sent_volunteers, container, false)
        val recyclerView = root.recyclerViewNotSentVolunteersTab
        famMenu = root.fam_menu_tab
        adapter = VolunteerAdapter()
        adapter.onVolunteerLongClickListener = object : VolunteerAdapter.OnVolunteerLongClickListener {
            override fun onLongClick(volunteer: Volunteer) {
                onClickDeleteVolunteer(volunteer)
            }
        }
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(context)
        val model by viewModels<MainViewModel>()
        viewModel = model
        viewModel.notSentVolunteers.observe(viewLifecycleOwner, Observer {
            adapter.volunteers = it
        })
        root.fab_buttonAddManuallyTab.setOnClickListener(this)
        root.fab_buttonAddByScannerTab.setOnClickListener(this)
        root.fab_buttonSentNewTab.setOnClickListener(this)
        return root
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.fab_buttonAddManuallyTab -> onClickAddManuallyTab()
            R.id.fab_buttonAddByScannerTab -> onClickAddByScannerTab()
            R.id.fab_buttonSentNewTab -> onClickSentNewTab()
        }
    }

    private fun onClickAddManuallyTab() {
        val intent = Intent(context, AddManuallyActivity::class.java)
        intent.putExtra(EXTRA_SIZE, adapter.itemCount)
        startActivity(intent)
        famMenu.close(true)
    }

    private fun onClickAddByScannerTab() {
        val intent = Intent(context, BarCodeScannerActivity::class.java)
        intent.putExtra(EXTRA_SIZE, adapter.itemCount)
        startActivity(intent)
        famMenu.close(true)
    }

    private fun onClickSentNewTab() {
        isSentToInforg = true
        val builder = StringBuilder()
        for (volunteer in adapter.volunteers) {
            builder.append(volunteer.name)
                    .append(" ")
                    .append(volunteer.surname)
                    .append(" ")
                    .append(volunteer.callSign)
                    .append(" ")
                    .append(volunteer.phoneNumber)
                    .append(" ")
                    .append(volunteer.carMark)
                    .append(" ")
                    .append(volunteer.carModel)
                    .append(" ")
                    .append(volunteer.carRegistrationNumber)
                    .append(" ")
                    .append(volunteer.carColor)
                    .append("\n")
        }

        val message = builder.toString()
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "text/plain"
        intent.putExtra(Intent.EXTRA_TEXT, message)
        val chosenIntent = Intent.createChooser(intent, getString(R.string.chooser_title))
        startActivity(chosenIntent)

        famMenu.close(true)
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

    override fun onResume() {
        if (isSentToInforg) {
            val alertDialog = AlertDialog.Builder(context)
            alertDialog.setTitle(getString(R.string.attention))
            alertDialog.setMessage(getString(R.string.message_confirm_sent_to_inforg))
            alertDialog.setPositiveButton(getString(R.string.sent_successfully)) { _, _ ->
                for (volunteer in adapter.volunteers) {
                    viewModel.deleteVolunteer(volunteer)
                    volunteer.isSent = "true"
                    viewModel.insertVolunteer(volunteer)
                }
            }
            alertDialog.setNegativeButton(getString(R.string.not_sent), null)
            alertDialog.show()
            isSentToInforg = false
        }
        super.onResume()
    }
}