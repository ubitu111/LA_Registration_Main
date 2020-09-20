package ru.kireev.mir.registrarlizaalert

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.AdapterView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_add_new_fox.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.kireev.mir.registrarlizaalert.adapters.NewMemberOfFoxAdapter
import ru.kireev.mir.registrarlizaalert.adapters.VolunteerAutoCompleteAdapter
import ru.kireev.mir.registrarlizaalert.data.*
import ru.kireev.mir.registrarlizaalert.listeners.OnClickDeleteNewMemberOfFoxListener
import ru.kireev.mir.registrarlizaalert.listeners.OnVolunteerItemClickListener
import java.text.SimpleDateFormat
import java.util.*
import kotlin.properties.Delegates

class AddNewFoxActivity : AppCompatActivity() {
    private lateinit var volunteersViewModel: VolunteersViewModel
    private lateinit var foxesViewModel: FoxesViewModel
    private var elder: Volunteer? = null
    private val searchersList = mutableListOf<Volunteer>()
    private var numberOfFox by Delegates.notNull<Int>()
    private var selectedSearcherVolunteer: Volunteer? = null
    private var isVolunteerElderSelected = false
    private var isVolunteerSearcherSelected = false
    private var volunteersForUpdate = mutableListOf<Volunteer>()
    private lateinit var addNewMemberOfFoxAdapter: NewMemberOfFoxAdapter

    private lateinit var autoCompleteAdapter: VolunteerAutoCompleteAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_new_fox)

        autoCompleteAdapter = VolunteerAutoCompleteAdapter(this, arrayListOf())
        val volunteersModel by viewModels<VolunteersViewModel>()
        volunteersViewModel = volunteersModel
        val foxesModel by viewModels<FoxesViewModel>()
        foxesViewModel = foxesModel

        CoroutineScope(Dispatchers.Main).launch {
            numberOfFox = foxesViewModel.getLastNumberOfFox() + 1
        }

        volunteersViewModel.getVolunteersByStatusAndNotAddedToFox(getString(R.string.volunteer_status_active)).observe(this, {
            autoCompleteAdapter.fullList = it
            volunteersForUpdate = it.toMutableList()
        })


        actvAddFoxElder.setAdapter(autoCompleteAdapter)
        actvAddFoxSearcher.setAdapter(autoCompleteAdapter)

        //адаптер для RecycleView, в который добавляются новые поля AutoComplete
        addNewMemberOfFoxAdapter = NewMemberOfFoxAdapter(autoCompleteAdapter)
        addNewMemberOfFoxAdapter.onClickDeleteMemberListener = object : OnClickDeleteNewMemberOfFoxListener {
            override fun onClickDeleteMember(position: Int) {
                val fieldActv = addNewMemberOfFoxAdapter.newMembers[position]
                if (fieldActv.isNotEmpty()) {
                    for (searcher in searchersList) {
                        if (searcher.toString() == fieldActv && notContainsStringVolunteerInListForUpdate(fieldActv)) {
                            searchersList.remove(searcher)
                            volunteersForUpdate.add(searcher)
                            autoCompleteAdapter.fullList = volunteersForUpdate
                            break
                        }
                    }
                }
                addNewMemberOfFoxAdapter.deleteMember(position)
            }
        }
        rvAddFox.adapter = addNewMemberOfFoxAdapter
        rvAddFox.layoutManager = LinearLayoutManager(applicationContext)

        //показывает раскрывающийся список при касании на actv
        actvAddFoxElder.setOnClickListener {
            autoCompleteAdapter.filter.filter("")
            actvAddFoxElder.showDropDown()
        }
        actvAddFoxSearcher.setOnClickListener {
            autoCompleteAdapter.filter.filter("")
            actvAddFoxSearcher.showDropDown()
        }

        //заносит выбранного волонтера из общего списка в список ролей
        actvAddFoxElder.setOnItemClickListener { parent, _, position, _ ->
            val volunteer = parent.getItemAtPosition(position) as Volunteer
            elder = volunteer
            volunteersForUpdate.remove(volunteer)
            autoCompleteAdapter.fullList = volunteersForUpdate
            isVolunteerElderSelected = true
        }
        actvAddFoxSearcher.setOnItemClickListener { parent, _, position, _ ->
            onVolunteerItemClick(parent, position, -1)
        }
        addNewMemberOfFoxAdapter.onVolunteerItemClickListener = object : OnVolunteerItemClickListener {
            override fun onItemClick(parent: AdapterView<*>, position: Int, adapterPosition: Int) {
                onVolunteerItemClick(parent, position, adapterPosition)
            }
        }

        actvAddFoxElder.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (isVolunteerElderSelected) {
                    isVolunteerElderSelected = false
                    elder?.let {
                        volunteersForUpdate.add(it)
                        autoCompleteAdapter.fullList = volunteersForUpdate
                        elder = null
                    }
                }
            }
        })
        actvAddFoxSearcher.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                untilTextChanged(s)
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                whenTextChanged(s)
            }
        })
        addNewMemberOfFoxAdapter.textChangedListener = object : NewMemberOfFoxAdapter.TextChangedListener {
            override fun beforeChanged(s: CharSequence?) {
                untilTextChanged(s)
            }

            override fun onChanged(s: CharSequence?) {
                whenTextChanged(s)
            }
        }
    }

    private fun containsVolunteerInSearcherList(volunteer: Volunteer): Boolean {
        for (searcher in searchersList) {
            if (searcher.uniqueId == volunteer.uniqueId) return true
        }
        return false
    }

    private fun notContainsStringVolunteerInListForUpdate(fieldACTV: String): Boolean {
        for (volunteer in volunteersForUpdate) {
            if (volunteer.toString() == fieldACTV) return false
        }
        return true
    }

    private fun onVolunteerItemClick(parent: AdapterView<*>, position: Int, adapterPosition: Int) {
        val volunteer = parent.getItemAtPosition(position) as Volunteer
        if (searchersList.isEmpty()) {
            searchersList.add(volunteer)
        } else if (!containsVolunteerInSearcherList(volunteer)) {
            searchersList.add(volunteer)
        }
        volunteersForUpdate.remove(volunteer)
        autoCompleteAdapter.fullList = volunteersForUpdate
        selectedSearcherVolunteer = volunteer
        isVolunteerSearcherSelected = true
        if (adapterPosition != -1) {
            addNewMemberOfFoxAdapter.newMembers[adapterPosition] = volunteer.toString()
        }
    }

    private fun whenTextChanged(s: CharSequence?) {
        isVolunteerSearcherSelected = false
        s?.let {
            val fieldActv = it.toString()
            if (fieldActv.isNotEmpty()) {
                for (searcher in searchersList) {
                    if (searcher.toString() == fieldActv && notContainsStringVolunteerInListForUpdate(fieldActv)) {
                        searchersList.remove(searcher)
                        volunteersForUpdate.add(searcher)
                        autoCompleteAdapter.fullList = volunteersForUpdate
                        break
                    }
                }
            }
        }
    }

    private fun untilTextChanged(s: CharSequence?) {
        s?.let {
            val fieldActv = it.toString()
            if (fieldActv.isNotEmpty()) {
                for (searcher in searchersList) {
                    if (searcher.toString() == fieldActv && notContainsStringVolunteerInListForUpdate(fieldActv)) {
                        searchersList.remove(searcher)
                        volunteersForUpdate.add(searcher)
                        autoCompleteAdapter.fullList = volunteersForUpdate
                        break
                    }
                }
            }
        }
    }

    fun onClickSaveFox(view: View) {
        if (elder == null || searchersList.isEmpty() || searchersList.size != addNewMemberOfFoxAdapter.itemCount + 1) {
            Toast.makeText(this, getString(R.string.select_elder_and_searchers_from_list), Toast.LENGTH_SHORT).show()
        } else {
            elder?.let {
                val currentDate = Date()
                val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
                val dateOfCreation = dateFormat.format(currentDate)

                CoroutineScope(Dispatchers.Main).launch {
                 val idOfFox = foxesViewModel.insertFox(Fox(
                        0,
                        numberOfFox,
                        it.uniqueId,
                        dateOfCreation = dateOfCreation,
                        nameOfGroup = GroupCallsigns.LISA            //TODO: Изменить хардкод группы
                ))

                    it.groupId = idOfFox
                    volunteersViewModel.updateVolunteer(it)

                    for (searcher in searchersList) {
                        searcher.groupId = idOfFox
                        volunteersViewModel.updateVolunteer(searcher)
                    }

                }
                onBackPressed()
            }
        }
    }

    fun onClickAddNewMember(view: View) {
        isVolunteerSearcherSelected = false
        addNewMemberOfFoxAdapter.addMember("")
    }

}