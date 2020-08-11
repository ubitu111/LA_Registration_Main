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
import kotlinx.android.synthetic.main.fragment_tabbed_sent_volunteers.view.*
import ru.kireev.mir.registrarlizaalert.R
import ru.kireev.mir.registrarlizaalert.adapters.VolunteerAdapter
import ru.kireev.mir.registrarlizaalert.data.Volunteer
import ru.kireev.mir.registrarlizaalert.data.VolunteersViewModel
import ru.kireev.mir.registrarlizaalert.listeners.OnVolunteerPhoneNumberClickListener

class SentVolunteersFragment : Fragment(), SearchView.OnQueryTextListener, View.OnClickListener {

    companion object {
        private const val SPACE_KEY = " "
        private const val LINE_SEPARATOR = "\n"
        private const val NOTE_SEPARATOR = "* * * * * * * * * * * * * *\n"
    }

    private lateinit var viewModel: VolunteersViewModel
    private lateinit var adapter: VolunteerAdapter
    private var fullList = listOf<Volunteer>()
    private var isSentToInforg = false
    private var leftVolunteers = listOf<Volunteer>()
    private lateinit var famMenu: FloatingActionMenu

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val root = inflater.inflate(R.layout.fragment_tabbed_sent_volunteers, container, false)
        val recyclerView = root.recyclerViewSentVolunteersTab
        val model by viewModels<VolunteersViewModel>()
        famMenu = root.fam_menu_sent
        viewModel = model
        adapter = VolunteerAdapter(requireContext(), model)
        adapter.onVolunteerPhoneNumberClickListener = object : OnVolunteerPhoneNumberClickListener {
            override fun onVolunteerPhoneNumberClick(phone: String) {
                val toDial = "tel:$phone"
                startActivity(Intent(Intent.ACTION_DIAL, Uri.parse(toDial)))
            }
        }
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(context)
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

        viewModel.sentVolunteers.observe(viewLifecycleOwner, Observer {
            adapter.volunteers = it
            fullList = it
        })
        viewModel.getVolunteersWithStatus(getString(R.string.volunteer_status_left)).observe(viewLifecycleOwner, Observer {
            leftVolunteers = it
        })
        setHasOptionsMenu(true)
        root.fab_buttonSentLeft.setOnClickListener(this)
        return root
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

    override fun onClick(v: View?) {
        when(v?.id) {
            R.id.fab_buttonSentLeft -> onClickSentLeft()
        }
    }

    private fun onClickSentLeft() {
        isSentToInforg = true
        val builder = StringBuilder()
        for (volunteer in leftVolunteers) {

            builder
                    .append(resources.getString(R.string.fullName))
                    .append(SPACE_KEY)
                    .append(volunteer.fullName)
                    .append(LINE_SEPARATOR)

                    .append(resources.getString(R.string.call_sign))
                    .append(SPACE_KEY)
                    .append(volunteer.callSign)
                    .append(LINE_SEPARATOR)

                    .append(resources.getString(R.string.forum_nickname))
                    .append(SPACE_KEY)
                    .append(volunteer.nickName)
                    .append(LINE_SEPARATOR)

                    .append(resources.getString(R.string.phone_number))
                    .append(SPACE_KEY)
                    .append(volunteer.phoneNumber)
                    .append(LINE_SEPARATOR)

                    .append(resources.getString(R.string.info_about_car))
                    .append(SPACE_KEY)
                    .append(volunteer.car)
                    .append(LINE_SEPARATOR)

                    .append(NOTE_SEPARATOR)
        }

        val message = builder.toString()
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "text/plain"
        intent.putExtra(Intent.EXTRA_TEXT, message)
        val chosenIntent = Intent.createChooser(intent, getString(R.string.chooser_title))
        startActivity(chosenIntent)

        famMenu.close(true)
    }

    override fun onResume() {
        if (isSentToInforg) {
            val alertDialog = AlertDialog.Builder(context)
            alertDialog.setTitle(getString(R.string.attention))
            alertDialog.setMessage(getString(R.string.message_confirm_sent_to_inforg))
            alertDialog.setPositiveButton(getString(R.string.sent_successfully)) { _, _ ->
                for (volunteer in leftVolunteers) {
                    volunteer.notifyThatLeft = "true"
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