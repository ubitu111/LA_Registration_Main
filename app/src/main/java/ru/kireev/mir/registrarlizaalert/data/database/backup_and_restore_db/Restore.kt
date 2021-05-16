package ru.kireev.mir.registrarlizaalert.data.database.backup_and_restore_db

import android.content.Context
import android.net.Uri
import androidx.room.RoomDatabase
import com.google.gson.Gson
import com.google.gson.JsonObject
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader


class Restore {
    private val STRING_FOR_NULL_VALUE = "!!!string_for_null_value!!!"

    @Throws(Exception::class)
    private fun convertStreamToString(inputStream: InputStream?): String {
        val sb = StringBuilder()
        inputStream?.let {
            val reader = BufferedReader(InputStreamReader(it))
            var line = reader.readLine()
            while (line != null) {
                sb.append(line).append("\n")
                line = reader.readLine()
            }
            reader.close()
        }

        return sb.toString()
    }

    inner class Init {
        private var database: RoomDatabase? = null
        private var backupFilePath: Uri? = null
        private var context: Context? = null
        private var onWorkFinishListener: OnWorkFinishListener? = null

        fun database(database: RoomDatabase?): Init {
            this.database = database
            return this
        }

        fun context(context: Context): Init {
            this.context = context
            return this
        }

        fun backupFilePath(backupFilePath: Uri): Init {
            this.backupFilePath = backupFilePath
            return this
        }

        fun onWorkFinishListener(onWorkFinishListener: OnWorkFinishListener?): Init {
            this.onWorkFinishListener = onWorkFinishListener
            return this
        }

        fun execute() {
            try {
                if (database == null) {
                    onWorkFinishListener?.onFinished(false, "Database not specified")
                    return
                }

                val fin = backupFilePath?.let { context?.contentResolver?.openInputStream(it) }
                val data = convertStreamToString(fin)
                fin?.close()
                val jsonDB = Gson().fromJson(data, JsonObject::class.java)
                for (table in jsonDB.keySet()) {
                    val c = database?.query("delete from $table", null)
                    c?.close()
                    val tableArray = jsonDB[table].asJsonArray
                    for (i in 0 until tableArray.size()) {
                        val row = tableArray[i].asJsonObject
                        var query = "insert into $table ("
                        for (column in row.keySet()) {
                            query = "$query$column,"
                        }
                        query = query.substring(0, query.lastIndexOf(","))
                        query = "$query) "
                        query += "values("
                        for (column in row.keySet()) {
                            val value = row[column].asString
                            query = if (value == STRING_FOR_NULL_VALUE) {
                                query + "NULL" + ","
                            } else {
                                "$query\'$value\',"
                            }
                        }
                        query = query.substring(0, query.lastIndexOf(","))
                        query = "$query);"
                        val cc = database?.query(query, null)
                        val pp = cc?.count
                    }
                }
                onWorkFinishListener?.onFinished(true, "success")
            } catch (e: Exception) {
                onWorkFinishListener?.onFinished(false, e.toString())
            }
        }
    }
}