package ru.kireev.mir.registrarlizaalert.ui.main

import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.*
import android.widget.SearchView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_tabbed_all_volunteers.view.*
import ru.kireev.mir.registrarlizaalert.AddManuallyActivity
import ru.kireev.mir.registrarlizaalert.R
import ru.kireev.mir.registrarlizaalert.adapters.VolunteerAdapter
import ru.kireev.mir.registrarlizaalert.data.FoxesViewModel
import ru.kireev.mir.registrarlizaalert.data.Volunteer
import ru.kireev.mir.registrarlizaalert.data.VolunteersViewModel
import ru.kireev.mir.registrarlizaalert.listeners.OnVolunteerClickListener
import ru.kireev.mir.registrarlizaalert.listeners.OnVolunteerPhoneNumberClickListener

class AllVolunteersFragment : Fragment(), View.OnClickListener, SearchView.OnQueryTextListener {

    companion object {
        private const val EXTRA_SIZE = "size"
        private const val EXTRA_VOLUNTEER_ID = "volunteer_id"
    }

    private lateinit var foxesViewModel: FoxesViewModel
    private lateinit var viewModel: VolunteersViewModel
    private lateinit var adapter: VolunteerAdapter
    private var fullList = listOf<Volunteer>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val root = inflater.inflate(R.layout.fragment_tabbed_all_volunteers, container, false)
        val recyclerView = root.recyclerViewAllVolunteersTab
        val foxModel by viewModels<FoxesViewModel>()
        foxesViewModel = foxModel
        foxesViewModel.allFoxes.observe(viewLifecycleOwner, Observer {  })
        val model by viewModels<VolunteersViewModel>()
        viewModel = model
        adapter = VolunteerAdapter(requireContext(), model)
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

        viewModel.allVolunteers.observe(viewLifecycleOwner, Observer {
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
            if (foxesViewModel.allFoxes.value.isNullOrEmpty()) {
                viewModel.deleteAllVolunteers()
            } else {
                Toast.makeText(context, "Перед удалением волонтеров необходимо удалить все лисы!", Toast.LENGTH_SHORT).show()
            }

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