package ru.kireev.mir.registrarlizaalert.ui.fragments.tabs.volunteers

import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.*
import android.widget.SearchView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.clans.fab.FloatingActionMenu
import kotlinx.android.synthetic.main.fragment_tabbed_not_sent_volunteers.view.*
import org.koin.android.ext.android.inject
import ru.kireev.mir.registrarlizaalert.R
import ru.kireev.mir.registrarlizaalert.data.database.entity.Volunteer
import ru.kireev.mir.registrarlizaalert.presentation.viewmodel.GroupsViewModel
import ru.kireev.mir.registrarlizaalert.presentation.viewmodel.VolunteersViewModel
import ru.kireev.mir.registrarlizaalert.ui.activities.AddManuallyActivity
import ru.kireev.mir.registrarlizaalert.ui.activities.BarCodeScannerActivity
import ru.kireev.mir.registrarlizaalert.ui.adapters.VolunteerAdapter
import ru.kireev.mir.registrarlizaalert.ui.listeners.OnVolunteerClickListener
import ru.kireev.mir.registrarlizaalert.ui.listeners.OnVolunteerPhoneNumberClickListener

class NotSentVolunteersFragment : Fragment(), View.OnClickListener, SearchView.OnQueryTextListener {
    companion object {
        private const val EXTRA_SIZE = "size"
        private const val EXTRA_VOLUNTEER_ID = "volunteer_id"
        private const val SPACE_KEY = " "
        private const val LINE_SEPARATOR = "\n"
        private const val NOTE_SEPARATOR = "\n * * * * * * * * * * * * * *\n"
        private const val BUNDLE_KEY_IS_SENT_TO_INFORG = "is_sent_to_inforg"
    }

    private val viewModel: VolunteersViewModel by inject()
    private val groupsViewModel: GroupsViewModel by inject()

    private lateinit var adapter: VolunteerAdapter
    private lateinit var famMenu: FloatingActionMenu
    private var isSentToInforg = false
    private var fullList = listOf<Volunteer>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val root = inflater.inflate(R.layout.fragment_tabbed_not_sent_volunteers, container, false)
        savedInstanceState?.let {
            isSentToInforg = it.getBoolean(BUNDLE_KEY_IS_SENT_TO_INFORG)
        }
        val recyclerView = root.recyclerViewNotSentVolunteersTab
        famMenu = root.fam_menu_tab
        adapter = VolunteerAdapter(requireContext(), viewModel, groupsViewModel)
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

        viewModel.notSentVolunteers.observe(viewLifecycleOwner, {
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
            builder
                    .append(getString(R.string.fullName))
                    .append(SPACE_KEY)
                    .append(volunteer.fullName)
                    .append(LINE_SEPARATOR)

                    .append(getString(R.string.call_sign))
                    .append(SPACE_KEY)
                    .append(volunteer.callSign)
                    .append(LINE_SEPARATOR)

                    .append(getString(R.string.forum_nickname))
                    .append(SPACE_KEY)
                    .append(volunteer.nickName)
                    .append(LINE_SEPARATOR)

                    .append(getString(R.string.region))
                    .append(SPACE_KEY)
                    .append(volunteer.region)
                    .append(LINE_SEPARATOR)

                    .append(getString(R.string.phone_number))
                    .append(SPACE_KEY)
                    .append(volunteer.phoneNumber)
                    .append(LINE_SEPARATOR)

                    .append(getString(R.string.info_about_car))
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
                for (volunteer in adapter.volunteers) {
                    volunteer.isSent = "true"
                    viewModel.updateVolunteer(volunteer)
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

    override fun onStart() {
        super.onStart()
        (activity as AppCompatActivity).supportActionBar?.title = getString(R.string.actionbar_label_volunteers)
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

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean(BUNDLE_KEY_IS_SENT_TO_INFORG, isSentToInforg)
    }
}