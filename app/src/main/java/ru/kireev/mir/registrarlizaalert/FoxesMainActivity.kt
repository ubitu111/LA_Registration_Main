package ru.kireev.mir.registrarlizaalert

import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_foxes_main.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.kireev.mir.registrarlizaalert.adapters.FoxesAdapter
import ru.kireev.mir.registrarlizaalert.data.Fox
import ru.kireev.mir.registrarlizaalert.data.FoxesViewModel
import ru.kireev.mir.registrarlizaalert.data.VolunteersViewModel
import ru.kireev.mir.registrarlizaalert.listeners.OnDeleteFoxClickListener
import ru.kireev.mir.registrarlizaalert.listeners.OnFoxClickListener
import ru.kireev.mir.registrarlizaalert.listeners.OnVolunteerPhoneNumberClickListener

class FoxesMainActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var foxesViewModel: FoxesViewModel
    private lateinit var volunteersViewModel: VolunteersViewModel
    private lateinit var foxesAdapter: FoxesAdapter

    companion object {
        private const val SPACE_KEY = " "
        private const val LINE_SEPARATOR = "\n"
        private const val NOTE_SEPARATOR = "\n * * * * * * * * * * * * * *\n"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_foxes_main)
        val foxModel by viewModels<FoxesViewModel>()
        foxesViewModel = foxModel
        val volunteerModel by viewModels<VolunteersViewModel>()
        volunteersViewModel = volunteerModel
        foxesAdapter = FoxesAdapter(applicationContext)
        foxesAdapter.onVolunteerPhoneNumberClickListener = object : OnVolunteerPhoneNumberClickListener {
            override fun onVolunteerPhoneNumberClick(phone: String) {
                val toDial = "tel:$phone"
                startActivity(Intent(Intent.ACTION_DIAL, Uri.parse(toDial)))
            }
        }
        foxesAdapter.onDeleteFoxClickListener = object : OnDeleteFoxClickListener {
            override fun onDeleteFoxClick(fox: Fox) {
                onClickDeleteFox(fox)
            }
        }
        foxesAdapter.onFoxClickListener = object : OnFoxClickListener {
            override fun onFoxClick(position: Int) {
                val foxId = foxesAdapter.foxes[position].id
                val intent = Intent(this@FoxesMainActivity, FoxDetailActivity::class.java)
                intent.putExtra("fox_id", foxId)
                startActivity(intent)
            }
        }
        rvFoxesMain.adapter = foxesAdapter
        rvFoxesMain.layoutManager = LinearLayoutManager(applicationContext)
        foxesViewModel.allFoxes.observe(this, Observer {
            foxesAdapter.foxes = it
        })

        fab_button_foxes_add.setOnClickListener(this)
        fab_button_foxes_sent.setOnClickListener(this)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_item_volunteers -> {
                val intent = Intent(this, TabbedMainActivity::class.java)
                startActivity(intent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun onClickAddFox() {
        val intent = Intent(this, AddNewFoxActivity::class.java)
        startActivity(intent)
        fam_menu_foxes_main.close(true)
    }

    private fun onClickSendFox() {
        val builder = StringBuilder()
        for (fox in foxesAdapter.foxes) {
            builder
                    .append(String.format(getString(R.string.foxes_item_number_of_fox), fox.numberOfFox))
                    .append(LINE_SEPARATOR)

                    .append(getString(R.string.elder_of_fox))
                    .append(LINE_SEPARATOR)
                    .append(fox.elderOfFox.fullName)
                    .append(LINE_SEPARATOR)
                    .append(fox.elderOfFox.phoneNumber)
                    .append(LINE_SEPARATOR)
                    .append(LINE_SEPARATOR)

                    .append(getString(R.string.searchers))
                    .append(LINE_SEPARATOR)

            for (volunteer in fox.membersOfFox) {
                builder
                        .append(volunteer.fullName)
                        .append(LINE_SEPARATOR)
                        .append(volunteer.phoneNumber)
                        .append(LINE_SEPARATOR)
            }

            builder
                    .append(LINE_SEPARATOR)

                    .append(getString(R.string.label_fox_detail_task))
                    .append(LINE_SEPARATOR)
                    .append(fox.task)
                    .append(LINE_SEPARATOR)
                    .append(LINE_SEPARATOR)

                    .append(getString(R.string.label_detail_fox_navigators))
                    .append(LINE_SEPARATOR)
                    .append(fox.navigators)
                    .append(LINE_SEPARATOR)
                    .append(LINE_SEPARATOR)

                    .append(getString(R.string.label_detail_fox_compasses))
                    .append(LINE_SEPARATOR)
                    .append(fox.compasses)
                    .append(LINE_SEPARATOR)
                    .append(LINE_SEPARATOR)

                    .append(getString(R.string.label_detail_fox_walkie_talkies))
                    .append(LINE_SEPARATOR)
                    .append(fox.walkieTalkies)
                    .append(LINE_SEPARATOR)
                    .append(LINE_SEPARATOR)

                    .append(getString(R.string.label_detail_fox_lamps))
                    .append(LINE_SEPARATOR)
                    .append(fox.lamps)
                    .append(LINE_SEPARATOR)
                    .append(LINE_SEPARATOR)

                    .append(getString(R.string.label_detail_fox_others))
                    .append(LINE_SEPARATOR)
                    .append(fox.others)
                    .append(LINE_SEPARATOR)
                    .append(LINE_SEPARATOR)

                    .append(NOTE_SEPARATOR)
        }

        val message = builder.toString()
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "text/plain"
        intent.putExtra(Intent.EXTRA_TEXT, message)
        val chosenIntent = Intent.createChooser(intent, getString(R.string.chooser_title))
        startActivity(chosenIntent)

        fam_menu_foxes_main.close(true)
    }

    private fun onClickDeleteFox(fox: Fox) {
        CoroutineScope(Dispatchers.Main).launch {
            val freshFox = foxesViewModel.getFoxById(fox.id)
            val alertDialog = AlertDialog.Builder(this@FoxesMainActivity)
            alertDialog.setTitle(getString(R.string.warning))
            alertDialog.setMessage(getString(R.string.message_confirm_delete_one))
            alertDialog.setPositiveButton(getString(R.string.delete_all)) { _, _ ->
                freshFox.elderOfFox.isAddedToFox = "false"
                volunteersViewModel.insertVolunteer(freshFox.elderOfFox)
                for (volunteer in freshFox.membersOfFox) {
                    volunteer.isAddedToFox = "false"
                    volunteersViewModel.insertVolunteer(volunteer)
                }
                foxesViewModel.deleteFox(freshFox)
            }
            alertDialog.setNegativeButton(getString(R.string.cancel), null)
            alertDialog.show()
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.fab_button_foxes_add -> onClickAddFox()
            R.id.fab_button_foxes_sent -> onClickSendFox()
        }
    }
}