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
import androidx.recyclerview.widget.RecyclerView
import com.github.clans.fab.FloatingActionMenu
import kotlinx.android.synthetic.main.fragment_tabbed_not_sent_volunteers.view.*
import ru.kireev.mir.registrarlizaalert.AddManuallyActivity
import ru.kireev.mir.registrarlizaalert.BarCodeScannerActivity
import ru.kireev.mir.registrarlizaalert.R
import ru.kireev.mir.registrarlizaalert.adapters.VolunteerAdapter
import ru.kireev.mir.registrarlizaalert.data.Volunteer
import ru.kireev.mir.registrarlizaalert.data.VolunteersViewModel
import ru.kireev.mir.registrarlizaalert.listeners.OnVolunteerLongClickListener
import ru.kireev.mir.registrarlizaalert.listeners.OnVolunteerPhoneNumberClickListener

class NotSentVolunteersFragment : Fragment(), View.OnClickListener, SearchView.OnQueryTextListener {
    companion object {
        private const val EXTRA_SIZE = "size"
    }

    private lateinit var viewModel: VolunteersViewModel
    private lateinit var adapter: VolunteerAdapter
    private lateinit var famMenu: FloatingActionMenu
    private var isSentToInforg = false
    private var fullList = listOf<Volunteer>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val root = inflater.inflate(R.layout.fragment_tabbed_not_sent_volunteers, container, false)
        val recyclerView = root.recyclerViewNotSentVolunteersTab
        famMenu = root.fam_menu_tab
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
        viewModel.notSentVolunteers.observe(viewLifecycleOwner, Observer {
            adapter.volunteers = it
            fullList = it
        })
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (dy > 0 && famMenu.visibility == View.VISIBLE) {
                    famMenu.hideMenu(true)
                } else if (dy < 0 && famMenu.visibility != View.VISIBLE) {
                    famMenu.showMenu(true)
                }
            }
        })
        root.fab_buttonAddManuallyTab.setOnClickListener(this)
        root.fab_buttonAddByScannerTab.setOnClickListener(this)
        root.fab_buttonSentNewTab.setOnClickListener(this)
        setHasOptionsMenu(true)
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
                    .append("\n")
                    .append(volunteer.surname)
                    .append("\n")
            if (volunteer.callSign.isNotEmpty()) {
                builder.append(volunteer.callSign)
                        .append("\n")
            }
            builder.append(volunteer.phoneNumber)
                    .append("\n")
                    .append(volunteer.carMark)
                    .append("\n")
                    .append(volunteer.carModel)
                    .append("\n")
                    .append(volunteer.carRegistrationNumber)
                    .append("\n")
                    .append(volunteer.carColor)
                    .append("\n----------------------\n")
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
            setOnQueryTextListener(this@NotSentVolunteersFragment)
            setOnCloseListener {
                adapter.volunteers = fullList
                false
            }
        }
    }
}