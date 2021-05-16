package ru.kireev.mir.registrarlizaalert.data.database.backup_and_restore_db

import androidx.room.RoomDatabase
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import java.io.File
import java.io.FileWriter
import java.util.*


class Backup {

    companion object {
        private val SQLITE_TABLES = listOf("android_metadata", "room_master_table", "sqlite_sequence")
        private const val STRING_FOR_NULL_VALUE = "!!!string_for_null_value!!!"
    }

    private fun isInSQLiteTables(table: String): Boolean {
        return SQLITE_TABLES.contains(table)
    }

    inner class Init {
        private var database: RoomDatabase? = null
        private var path: String = ""
        private var fileName: String = ""
        private var onWorkFinishListener: OnWorkFinishListener? = null
        fun database(database: RoomDatabase?): Init {
            this.database = database
            return this
        }

        fun path(path: String): Init {
            this.path = path
            return this
        }

        fun fileName(fileName: String): Init {
            this.fileName = fileName
            return this
        }

        fun onWorkFinishListener(onWorkFinishListener: OnWorkFinishListener?): Init {
            this.onWorkFinishListener = onWorkFinishListener
            return this
        }

        fun execute() {
            if (database == null) {
                onWorkFinishListener?.onFinished(false, "Database not specified")
                return
            }
            val tablesCursor = database?.query("SELECT name FROM sqlite_master WHERE type='table'", null) ?: return
            val tables = ArrayList<String>()
            val dbBuilder = Json.Builder()
            if (tablesCursor.moveToFirst()) {
                while (!tablesCursor.isAfterLast) {
                    tables.add(tablesCursor.getString(0))
                    tablesCursor.moveToNext()
                }
                for (table in tables) {
                    if (isInSQLiteTables(table)) continue
                    val rows = ArrayList<Json>()
                    val rowsCursor = database?.query("select * from $table", null) ?: return
                    val tableSqlCursor = database?.query("select sql from sqlite_master where name= \'$table\'", null) ?: return
                    tableSqlCursor.moveToFirst()
                    if (rowsCursor.moveToFirst()) {
                        do {
                            val columnCount = rowsCursor.columnCount
                            val rowBuilder = Json.Builder()
                            for (i in 0 until columnCount) {
                                val columnName = rowsCursor.getColumnName(i)
                                rowBuilder.putItem(columnName, if (rowsCursor.getString(i) != null) rowsCursor.getString(i) else STRING_FOR_NULL_VALUE)
                            }
                            rows.add(rowBuilder.build())
                        } while (rowsCursor.moveToNext())
                    }
                    dbBuilder.putItem(table, rows)
                }
                val jsonDB = dbBuilder.build().asJsonObject
                val gson = Gson()
                val type = object : TypeToken<JsonObject?>() {}.type
                val jsonTextDB = gson.toJson(jsonDB, type)
                try {
                    val data = jsonTextDB.toByteArray(charset("UTF8"))
                    val root = File(path)
                    if (!root.exists()) {
                        root.mkdirs()
                    }
                    val dFile = File(root, fileName)
                    val writer = FileWriter(dFile)
                    writer.append(String(data))
                    writer.flush()
                    writer.close()
                    onWorkFinishListener?.onFinished(true, "success")
                } catch (e: Exception) {
                    onWorkFinishListener?.onFinished(false, e.toString())
                }
            }
        }
    }
}