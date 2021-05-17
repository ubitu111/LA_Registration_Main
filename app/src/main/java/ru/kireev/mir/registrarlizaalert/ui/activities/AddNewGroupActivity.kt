package ru.kireev.mir.registrarlizaalert.ui.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.AdapterView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_add_new_group.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import ru.kireev.mir.registrarlizaalert.R
import ru.kireev.mir.registrarlizaalert.data.database.GroupCallsigns
import ru.kireev.mir.registrarlizaalert.data.database.entity.Group
import ru.kireev.mir.registrarlizaalert.data.database.entity.Volunteer
import ru.kireev.mir.registrarlizaalert.presentation.extention.getGroupCallsignAsString
import ru.kireev.mir.registrarlizaalert.presentation.viewmodel.GroupsViewModel
import ru.kireev.mir.registrarlizaalert.presentation.viewmodel.VolunteersViewModel
import ru.kireev.mir.registrarlizaalert.ui.adapters.NewMemberOfGroupAdapter
import ru.kireev.mir.registrarlizaalert.ui.adapters.VolunteerAutoCompleteAdapter
import ru.kireev.mir.registrarlizaalert.ui.listeners.OnClickDeleteNewMemberOfGroupListener
import ru.kireev.mir.registrarlizaalert.ui.listeners.OnVolunteerItemClickListener
import java.text.SimpleDateFormat
import java.util.*
import kotlin.properties.Delegates

class AddNewGroupActivity : AppCompatActivity(), View.OnClickListener {

    private val volunteersViewModel: VolunteersViewModel by inject()
    private val groupsViewModel: GroupsViewModel by inject()

    private var groupCallsign: GroupCallsigns? = null
    private var elder: Volunteer? = null
    private var searchersList = mutableListOf<Volunteer>()
    private var numberOfGroup by Delegates.notNull<Int>()
    private val deletedOnEditGroupSearchers = mutableListOf<Volunteer>()
    private var isVolunteerElderSelected = false

    private var volunteersForUpdate = mutableListOf<Volunteer>()
    private lateinit var addNewMemberOfGroupAdapter: NewMemberOfGroupAdapter
    private lateinit var autoCompleteAdapter: VolunteerAutoCompleteAdapter
    private var isGroupEdit = false
    private var groupId = 0
    private var group: Group? = null

    companion object {
        private const val ARG_GROUP_CALLSIGN = "group_callsign"
        private const val ARG_IS_GROUP_EDIT = "is_group_edit"
        private const val ARG_GROUP_ID = "group_id"

        fun getIntent(groupCallsign: GroupCallsigns, context: Context?): Intent {
            val intent = Intent(context, AddNewGroupActivity::class.java)
            intent.putExtra(ARG_GROUP_CALLSIGN, groupCallsign)
            return intent
        }

        fun getIntentForEditGroup(groupCallsign: GroupCallsigns, context: Context?, groupId: Int): Intent {
            val intent = Intent(context, AddNewGroupActivity::class.java)
            intent.putExtra(ARG_GROUP_CALLSIGN, groupCallsign)
            intent.putExtra(ARG_IS_GROUP_EDIT, true)
            intent.putExtra(ARG_GROUP_ID, groupId)
            return intent
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_new_group)

        groupCallsign = intent.getSerializableExtra(ARG_GROUP_CALLSIGN) as GroupCallsigns
        isGroupEdit = intent.getBooleanExtra(ARG_IS_GROUP_EDIT, false)
        groupId = intent.getIntExtra(ARG_GROUP_ID, 0)

        setSupportActionBar(new_group_toolbar as Toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = groupCallsign?.getGroupCallsignAsString(this)

        autoCompleteAdapter = VolunteerAutoCompleteAdapter(this, arrayListOf())

        CoroutineScope(Dispatchers.Main).launch {
            numberOfGroup = groupsViewModel.getLastNumberOfGroup(groupCallsign
                    ?: GroupCallsigns.LISA) + 1
        }

        volunteersViewModel.getVolunteersByStatusAndNotAddedToGroup(getString(R.string.volunteer_status_active)).observe(this, {
            autoCompleteAdapter.fullList = it
            volunteersForUpdate = it.toMutableList()
        })


        actv_add_group_elder.setAdapter(autoCompleteAdapter)
        actv_add_group_searcher.setAdapter(autoCompleteAdapter)

        //адаптер для RecycleView, в который добавляются новые поля AutoComplete
        addNewMemberOfGroupAdapter = NewMemberOfGroupAdapter(autoCompleteAdapter)
        addNewMemberOfGroupAdapter.onClickDeleteMemberListener = object : OnClickDeleteNewMemberOfGroupListener {
            override fun onClickDeleteMember(position: Int) {
                val fieldActv = addNewMemberOfGroupAdapter.newMembers[position]
                if (fieldActv.isNotEmpty()) {
                    for (searcher in searchersList) {
                        if (searcher.toString() == fieldActv && notContainsStringVolunteerInListForUpdate(fieldActv)) {
                            searchersList.remove(searcher)
                            volunteersForUpdate.add(searcher)
                            autoCompleteAdapter.fullList = volunteersForUpdate
                            if (isGroupEdit && !deletedOnEditGroupSearchers.contains(searcher)) {
                                deletedOnEditGroupSearchers.add(searcher)
                            }
                            break
                        }
                    }
                }
                addNewMemberOfGroupAdapter.deleteMember(position)
            }
        }
        rv_add_group.adapter = addNewMemberOfGroupAdapter
        rv_add_group.layoutManager = LinearLayoutManager(applicationContext)

        //показывает раскрывающийся список при касании на actv
        actv_add_group_elder.setOnClickListener {
            autoCompleteAdapter.filter.filter("")
            actv_add_group_elder.showDropDown()
        }
        actv_add_group_searcher.setOnClickListener {
            autoCompleteAdapter.filter.filter("")
            actv_add_group_searcher.showDropDown()
        }

        //заносит выбранного волонтера из общего списка в список ролей
        actv_add_group_elder.setOnItemClickListener { parent, _, position, _ ->
            val volunteer = parent.getItemAtPosition(position) as Volunteer
            elder = volunteer
            volunteersForUpdate.remove(volunteer)
            autoCompleteAdapter.fullList = volunteersForUpdate
            isVolunteerElderSelected = true
        }
        actv_add_group_searcher.setOnItemClickListener { parent, _, position, _ ->
            onVolunteerItemClick(parent, position, -1)
        }
        addNewMemberOfGroupAdapter.onVolunteerItemClickListener = object : OnVolunteerItemClickListener {
            override fun onItemClick(parent: AdapterView<*>, position: Int, adapterPosition: Int) {
                onVolunteerItemClick(parent, position, adapterPosition)
            }
        }

        actv_add_group_elder.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (isVolunteerElderSelected) {
                    isVolunteerElderSelected = false
                    elder?.let {
                        volunteersForUpdate.add(it)
                        autoCompleteAdapter.fullList = volunteersForUpdate
                        if (isGroupEdit && !deletedOnEditGroupSearchers.contains(it)) {
                            deletedOnEditGroupSearchers.add(it)
                        }
                        elder = null
                    }
                }
            }
        })
        actv_add_group_searcher.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                untilTextChanged(s)
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                whenTextChanged(s)
            }
        })
        addNewMemberOfGroupAdapter.textChangedListener = object : NewMemberOfGroupAdapter.TextChangedListener {
            override fun beforeChanged(s: CharSequence?) {
                untilTextChanged(s)
            }

            override fun onChanged(s: CharSequence?) {
                whenTextChanged(s)
            }
        }

        if (isGroupEdit) {
            CoroutineScope(Dispatchers.Main).launch {
                group = groupsViewModel.getGroupById(groupId)
                val elderOfGroup = volunteersViewModel.getVolunteerById(group?.elderOfGroupId)
                val groupVolunteers = volunteersViewModel.getVolunteersByIdOfGroup(groupId).toMutableList()

                groupVolunteers.remove(elderOfGroup)
                actv_add_group_elder.setText(elderOfGroup.toString())
                actv_add_group_searcher.setText(groupVolunteers[0].toString())
                if (groupVolunteers.size > 1) {
                    for (i in 1 until groupVolunteers.size) {
                        addNewMemberOfGroupAdapter.addMember(groupVolunteers[i].toString())
                    }
                }
                elder = elderOfGroup
                isVolunteerElderSelected = true
                searchersList = groupVolunteers

            }
        }

        add_new_member_of_new_group.setOnClickListener(this)
        b_add_group_save_group.setOnClickListener(this)
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
        if (adapterPosition != -1) {
            addNewMemberOfGroupAdapter.newMembers[adapterPosition] = volunteer.toString()
        }
    }

    private fun whenTextChanged(s: CharSequence?) {
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

    private fun onClickSaveGroup() {
        if (elder == null || searchersList.isEmpty() || searchersList.size != addNewMemberOfGroupAdapter.itemCount + 1) {
            Toast.makeText(this, getString(R.string.select_elder_and_searchers_from_list), Toast.LENGTH_SHORT).show()
        } else if (isGroupEdit) {
            editGroup()
        } else {
            createNewGroup()
        }
    }

    private fun onClickAddNewMember() {
        addNewMemberOfGroupAdapter.addMember("")
    }

    private fun createNewGroup() {
        elder?.let {
            val currentDate = Date()
            val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
            val dateOfCreation = dateFormat.format(currentDate)

            CoroutineScope(Dispatchers.Main).launch {
                val idOfGroup = groupsViewModel.insertGroup(
                    Group(
                        0,
                        numberOfGroup,
                        it.uniqueId,
                        dateOfCreation = dateOfCreation,
                        groupCallsign = groupCallsign ?: GroupCallsigns.LISA
                )
                )

                it.groupId = idOfGroup
                volunteersViewModel.updateVolunteer(it)

                for (searcher in searchersList) {
                    searcher.groupId = idOfGroup
                    volunteersViewModel.updateVolunteer(searcher)
                }

            }
            onBackPressed()
        }
    }

    private fun editGroup() {
        elder?.let {
            group?.elderOfGroupId = it.uniqueId
            group?.let { it1 -> groupsViewModel.updateGroup(it1) }

            it.groupId = group?.id
            volunteersViewModel.updateVolunteer(it)

            for (searcher in searchersList) {
                searcher.groupId = group?.id
                volunteersViewModel.updateVolunteer(searcher)
            }

            for (deletedSearcher in deletedOnEditGroupSearchers) {
                volunteersForUpdate.remove(deletedSearcher)
                deletedSearcher.groupId = null
                volunteersViewModel.updateVolunteer(deletedSearcher)
            }
            onBackPressed()
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.add_new_member_of_new_group -> onClickAddNewMember()
            R.id.b_add_group_save_group -> onClickSaveGroup()
        }
    }
}