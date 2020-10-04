package ru.kireev.mir.registrarlizaalert.ui.activities

import android.Manifest
import android.app.Activity
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.FileProvider
import androidx.core.view.GravityCompat
import com.google.android.material.navigation.NavigationView
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.TedPermission
import kotlinx.android.synthetic.main.activity_tabbed_main.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import ru.kireev.mir.registrarlizaalert.BuildConfig
import ru.kireev.mir.registrarlizaalert.R
import ru.kireev.mir.registrarlizaalert.data.*
import ru.kireev.mir.registrarlizaalert.ui.fragments.tabs.groups.GroupSectionsPagerAdapter
import ru.kireev.mir.registrarlizaalert.ui.fragments.tabs.volunteers.VolunteersSectionsPagerAdapter
import ru.kireev.mir.registrarlizaalert.util.getGroupCallsignAsString
import java.io.File

class TabbedMainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    companion object {
        private const val REQUEST_CODE_GET_PATH_TO_FILE = 111
        private const val SPACE_KEY = " "
        private const val LINE_SEPARATOR = "\n"
        private const val NOTE_SEPARATOR = "\n * * * * * * * * * * * * * *\n"
    }
    private lateinit var mainViewModel: MainViewModel
    private lateinit var groupsViewModel: GroupsViewModel
    private lateinit var volunteersViewModel: VolunteersViewModel
    private var pathToFile = ""
    private var allGroups = listOf<Group>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tabbed_main)
        setSupportActionBar(main_toolbar as Toolbar)
        setupTabbedAdapterToViewPager()
        val toggle = ActionBarDrawerToggle(
                this,
                main_drawer_layout,
                main_toolbar as Toolbar,
                R.string.nav_open_drawer,
                R.string.nav_close_drawer
        )
        main_drawer_layout.addDrawerListener(toggle)
        toggle.syncState()
        nav_view_main.setNavigationItemSelectedListener(this)
        val mainModel by viewModels<MainViewModel>()
        mainViewModel = mainModel
        mainViewModel.deleteOldTable()
        val groupsModel by viewModels<GroupsViewModel>()
        groupsViewModel = groupsModel
        val volunteersModel by viewModels<VolunteersViewModel>()
        volunteersViewModel = volunteersModel
        groupsViewModel.allGroups.observe(this, {
            allGroups = it
        })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return super.onCreateOptionsMenu(menu)

    }

    private fun backupDB() {
        this.getExternalFilesDir(null)?.absolutePath?.let {
            pathToFile = mainViewModel.backupDatabase(it)
            setChooserIntent(pathToFile)
        }
    }

    private fun setChooserIntent(pathToFile: String) {
        val intent = Intent(Intent.ACTION_SEND)
        val file = File(pathToFile)
        val newUri = FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID + ".provider", file)
        intent.putExtra(Intent.EXTRA_STREAM, newUri)
        intent.type = "application/json"
        intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
        val chooser = Intent.createChooser(intent, getString(R.string.chooser_title))
        startActivity(chooser)
    }

    private fun restoreDB() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "application/json"
        startActivityForResult(intent, REQUEST_CODE_GET_PATH_TO_FILE)
    }

    private fun getPermissionStorage(menuId: Int) {

        val permissionListener = object : PermissionListener {
            override fun onPermissionGranted() {
                when (menuId) {
                    R.id.nav_group_backup_db -> backupDB()
                    R.id.nav_group_restore_db -> restoreDB()
                    else -> backupDB()
                }
            }

            override fun onPermissionDenied(deniedPermissions: MutableList<String>?) {
                Toast.makeText(this@TabbedMainActivity, getString(R.string.permission_denied), Toast.LENGTH_SHORT).show()
            }
        }

        TedPermission.with(this)
                .setPermissionListener(permissionListener)
                .setDeniedMessage(getString(R.string.denied_message))
                .setPermissions(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .check()
    }

    private fun restartApplication() {
        val startActivity = Intent(this, SplashActivity::class.java)
        val pendingIntentId = 13
        val pendingIntent = PendingIntent.getActivity(this, pendingIntentId, startActivity, PendingIntent.FLAG_CANCEL_CURRENT)
        val alarmManager = this.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.set(AlarmManager.RTC, System.currentTimeMillis() + 100, pendingIntent)
        finish()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_CODE_GET_PATH_TO_FILE && resultCode == Activity.RESULT_OK) {
            data?.let {
                val uri = it.data
                uri?.let { data ->
                    mainViewModel.restoreDatabase(data, this)
                    restartApplication()
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_group_volunteers -> setupTabbedAdapterToViewPager()
            R.id.nav_group_lisa -> setupTabbedAdapterToViewPager(GroupCallsigns.LISA)
            R.id.nav_group_bort -> setupTabbedAdapterToViewPager(GroupCallsigns.BORT)
            R.id.nav_group_kinolog -> setupTabbedAdapterToViewPager(GroupCallsigns.KINOLOG)
            R.id.nav_group_veter -> setupTabbedAdapterToViewPager(GroupCallsigns.VETER)
            R.id.nav_group_voda -> setupTabbedAdapterToViewPager(GroupCallsigns.VODA)
            R.id.nav_group_pegas -> setupTabbedAdapterToViewPager(GroupCallsigns.PEGAS)
            R.id.nav_group_police -> setupTabbedAdapterToViewPager(GroupCallsigns.POLICE)
            R.id.nav_group_mchs -> setupTabbedAdapterToViewPager(GroupCallsigns.MCHS)
            R.id.nav_group_pso -> setupTabbedAdapterToViewPager(GroupCallsigns.PSO)
            R.id.nav_group_backup_db -> getPermissionStorage(R.id.nav_group_backup_db)
            R.id.nav_group_restore_db -> getPermissionStorage(R.id.nav_group_restore_db)
            R.id.nav_group_sent_info -> onClickSendGroupInfo()
        }
        main_drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }

    private fun setupTabbedAdapterToViewPager(groupCallsign: GroupCallsigns) {
        val groupSectionsPagerAdapter = GroupSectionsPagerAdapter(groupCallsign, this, supportFragmentManager)
        main_view_pager.adapter = groupSectionsPagerAdapter
        main_tabs.setupWithViewPager(main_view_pager)
    }

    private fun setupTabbedAdapterToViewPager() {
        val volunteersSectionsPagerAdapter = VolunteersSectionsPagerAdapter(this, supportFragmentManager)
        main_view_pager.adapter = volunteersSectionsPagerAdapter
        main_tabs.setupWithViewPager(main_view_pager)
    }

    private fun onClickSendGroupInfo() {
        val builder = StringBuilder()
        for (group in allGroups) {
            builder
                    .append(group.groupCallsign.getGroupCallsignAsString(this))
                    .append(SPACE_KEY)
                    .append(group.numberOfGroup)
                    .append(LINE_SEPARATOR)

                    .append(getString(R.string.elder_of_group))
                    .append(LINE_SEPARATOR)
            runBlocking {
                val job = launch {
                    val elder = volunteersViewModel.getVolunteerById(group.elderOfGroupId)
                    builder.append(elder.fullName)
                            .append(LINE_SEPARATOR)
                            .append(elder.phoneNumber)
                            .append(LINE_SEPARATOR)
                            .append(LINE_SEPARATOR)
                }
                job.join()
            }

            builder.append(getString(R.string.searchers))
                    .append(LINE_SEPARATOR)

            runBlocking {
                val job = launch {
                    val volunteers = volunteersViewModel.getVolunteersByIdOfGroup(group.id)
                    for (volunteer in volunteers) {
                        if (volunteer.uniqueId != group.elderOfGroupId) {
                            builder
                                    .append(volunteer.fullName)
                                    .append(LINE_SEPARATOR)
                                    .append(volunteer.phoneNumber)
                                    .append(LINE_SEPARATOR)
                        }
                    }
                }
                job.join()
            }


            builder
                    .append(LINE_SEPARATOR)

                    .append(getString(R.string.label_group_detail_task))
                    .append(LINE_SEPARATOR)
                    .append(group.task)
                    .append(LINE_SEPARATOR)
                    .append(LINE_SEPARATOR)

                    .append(getString(R.string.label_detail_group_navigators))
                    .append(LINE_SEPARATOR)
                    .append(group.navigators)
                    .append(LINE_SEPARATOR)
                    .append(LINE_SEPARATOR)

                    .append(getString(R.string.label_detail_group_compasses))
                    .append(LINE_SEPARATOR)
                    .append(group.compasses)
                    .append(LINE_SEPARATOR)
                    .append(LINE_SEPARATOR)

                    .append(getString(R.string.label_detail_group_walkie_talkies))
                    .append(LINE_SEPARATOR)
                    .append(group.walkieTalkies)
                    .append(LINE_SEPARATOR)
                    .append(LINE_SEPARATOR)

                    .append(getString(R.string.label_detail_group_lamps))
                    .append(LINE_SEPARATOR)
                    .append(group.lamps)
                    .append(LINE_SEPARATOR)
                    .append(LINE_SEPARATOR)

                    .append(getString(R.string.label_detail_group_other))
                    .append(LINE_SEPARATOR)
                    .append(group.others)
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
    }
}