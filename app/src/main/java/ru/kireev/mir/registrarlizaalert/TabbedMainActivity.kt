package ru.kireev.mir.registrarlizaalert

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_tabbed_main.*
import ru.kireev.mir.registrarlizaalert.ui.main.SectionsPagerAdapter

class TabbedMainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tabbed_main)
        val sectionsPagerAdapter = SectionsPagerAdapter(this, supportFragmentManager)
        main_view_pager.adapter = sectionsPagerAdapter
        main_tabs.setupWithViewPager(main_view_pager)
        setSupportActionBar(main_toolbar)
        supportActionBar?.title = getString(R.string.actionbar_label_volunteers)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_item_foxes -> {
                val intent = Intent(this, FoxesMainActivity::class.java)
                startActivity(intent)
            }
        }
        return super.onOptionsItemSelected(item)
    }
}