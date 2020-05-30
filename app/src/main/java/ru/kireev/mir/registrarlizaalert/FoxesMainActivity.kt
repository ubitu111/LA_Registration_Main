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
import ru.kireev.mir.registrarlizaalert.adapters.FoxesAdapter
import ru.kireev.mir.registrarlizaalert.data.Fox
import ru.kireev.mir.registrarlizaalert.data.FoxesViewModel
import ru.kireev.mir.registrarlizaalert.data.VolunteersViewModel
import ru.kireev.mir.registrarlizaalert.listeners.OnFoxLongClickListener
import ru.kireev.mir.registrarlizaalert.listeners.OnVolunteerPhoneNumberClickListener

class FoxesMainActivity : AppCompatActivity() {

    private lateinit var foxesViewModel: FoxesViewModel
    private lateinit var volunteersViewModel: VolunteersViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_foxes_main)
        val foxModel by viewModels<FoxesViewModel>()
        foxesViewModel = foxModel
        val volunteerModel by viewModels<VolunteersViewModel>()
        volunteersViewModel = volunteerModel
        val foxesAdapter = FoxesAdapter(applicationContext)
        foxesAdapter.onVolunteerPhoneNumberClickListener = object : OnVolunteerPhoneNumberClickListener {
            override fun onVolunteerPhoneNumberClick(phone: String) {
                val toDial = "tel:$phone"
                startActivity(Intent(Intent.ACTION_DIAL, Uri.parse(toDial)))
            }
        }
        foxesAdapter.onFoxLongClickListener = object : OnFoxLongClickListener {
            override fun onLongFoxClick(fox: Fox) {
                onClickDeleteFox(fox)
            }
        }

        rvFoxesMain.adapter = foxesAdapter
        rvFoxesMain.layoutManager = LinearLayoutManager(applicationContext)
        foxesViewModel.allFoxes.observe(this, Observer {
            foxesAdapter.foxes = it
        })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_item_volunteers -> {
                val intent = Intent(this, TabbedMainActivity::class.java)
                startActivity(intent)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    public fun onClickAddFox(view: View) {
        val intent = Intent(this, AddNewFoxActivity::class.java)
        startActivity(intent)
    }

    private fun onClickDeleteFox(fox: Fox) {
        val alertDialog = AlertDialog.Builder(this)
        alertDialog.setTitle(getString(R.string.warning))
        alertDialog.setMessage(getString(R.string.message_confirm_delete_one))
        alertDialog.setPositiveButton(getString(R.string.delete_all)) { _, _ ->
            fox.elderOfFox.isAddedToFox = "false"
            volunteersViewModel.insertVolunteer(fox.elderOfFox)
            for (volunteer in fox.membersOfFox) {
                volunteer.isAddedToFox = "false"
                volunteersViewModel.insertVolunteer(volunteer)
            }
            foxesViewModel.deleteFox(fox)
        }
        alertDialog.setNegativeButton(getString(R.string.cancel), null)
        alertDialog.show()
    }
}