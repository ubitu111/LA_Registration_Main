package ru.kireev.mir.registrarlizaalert.presentation.viewmodel

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.sqlite.db.SimpleSQLiteQuery
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.kireev.mir.registrarlizaalert.data.database.MainDatabase
import ru.kireev.mir.registrarlizaalert.data.database.backup_and_restore_db.Backup
import ru.kireev.mir.registrarlizaalert.data.database.backup_and_restore_db.OnWorkFinishListener
import ru.kireev.mir.registrarlizaalert.data.database.backup_and_restore_db.Restore

class MainViewModel(
    private val db: MainDatabase
) : ViewModel() {

    companion object {
        private const val FILENAME_FOR_BACKUP_AND_RESTORE = "database_backup.json"
        private const val TABLE_NAME_VOLUNTEERS = "volunteers"
        private const val TABLE_NAME_GROUPS = "groups"
    }

    fun backupDatabase(pathToFile: String) : String {
        Backup().Init()
                .database(db)
                .path(pathToFile)
                .fileName(FILENAME_FOR_BACKUP_AND_RESTORE)
                .onWorkFinishListener(object : OnWorkFinishListener {
                    override fun onFinished(success: Boolean, message: String) {
//                        val msg = if (success) {
//                            app.getString(R.string.message_for_success_backup_db)
//                        } else
//                            app.getString(R.string.message_for_error_backup_adn_restore_db)
//
//                        Toast.makeText(app, msg, Toast.LENGTH_SHORT).show()
                    }

                })
                .execute()
        return "$pathToFile/$FILENAME_FOR_BACKUP_AND_RESTORE"
    }

    fun restoreDatabase(pathToFile: Uri,  context: Context) {
        clearAutoIncrementCounter()
        db.openHelper.readableDatabase.execSQL("PRAGMA foreign_keys = off;")
        Restore().Init()
                .database(db)
                .backupFilePath(pathToFile)
                .context(context)
                .onWorkFinishListener(object : OnWorkFinishListener {
                    override fun onFinished(success: Boolean, message: String) {
//                        val msg = if (success) {
//                            app.getString(R.string.message_for_success_restore_db)
//                        } else
//                            app.getString(R.string.message_for_error_backup_adn_restore_db)
//
//                        Toast.makeText(app, msg, Toast.LENGTH_SHORT).show()
                    }
                })
                .execute()
        db.openHelper.readableDatabase.execSQL("PRAGMA foreign_keys = on;")
    }

    fun clearAutoIncrementCounter() {
        val volunteersQuery = SimpleSQLiteQuery("UPDATE sqlite_sequence SET seq = 0 WHERE name = '$TABLE_NAME_VOLUNTEERS';")
        val groupsQuery = SimpleSQLiteQuery("UPDATE sqlite_sequence SET seq = 0 WHERE name = '$TABLE_NAME_GROUPS';")
        viewModelScope.launch (Dispatchers.IO) {
            db.mainDao().clearAutoIncrementCounter(volunteersQuery)
            db.mainDao().clearAutoIncrementCounter(groupsQuery)
        }
    }

    fun deleteOldTable() {
        val query = SimpleSQLiteQuery("DROP TABLE IF EXISTS foxes;")
        viewModelScope.launch(Dispatchers.IO) { db.mainDao().clearAutoIncrementCounter(query) }
    }
}