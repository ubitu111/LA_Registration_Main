package ru.kireev.mir.registrarlizaalert

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
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.TedPermission
import kotlinx.android.synthetic.main.activity_tabbed_main.*
import ru.kireev.mir.registrarlizaalert.data.MainViewModel
import ru.kireev.mir.registrarlizaalert.ui.main.SectionsPagerAdapter
import java.io.File

class TabbedMainActivity : AppCompatActivity() {

    companion object {
        private const val REQUEST_CODE_GET_PATH_TO_FILE = 111
    }
    private lateinit var mainViewModel: MainViewModel
    private var pathToFile = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tabbed_main)
        val sectionsPagerAdapter = SectionsPagerAdapter(this, supportFragmentManager)
        main_view_pager.adapter = sectionsPagerAdapter
        main_tabs.setupWithViewPager(main_view_pager)
        setSupportActionBar(main_toolbar)
        supportActionBar?.title = getString(R.string.actionbar_label_volunteers)
        val mainModel by viewModels<MainViewModel>()
        mainViewModel = mainModel
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return super.onCreateOptionsMenu(menu)

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_item_foxes -> {
                val intent = Intent(this, FoxesMainActivity::class.java)
                startActivity(intent)
                true
            }
            R.id.menu_backup_db -> {
                getPermissionStorage(R.id.menu_backup_db)
                true
            }
            R.id.menu_restore_db -> {
                getPermissionStorage(R.id.menu_restore_db)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
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
                    R.id.menu_backup_db -> backupDB()
                    R.id.menu_restore_db -> restoreDB()
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
                    val cursor = contentResolver.query(data, null, null, null, null)
                    cursor?.moveToFirst()
                    val path = cursor?.getString(0)
                    cursor?.close()
                    if (path != null) {
                        mainViewModel.restoreDatabase(path)
                        restartApplication()
                    }
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }
}