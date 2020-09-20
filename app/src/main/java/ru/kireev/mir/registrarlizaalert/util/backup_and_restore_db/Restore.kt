package ru.kireev.mir.registrarlizaalert.util.backup_and_restore_db

import androidx.room.RoomDatabase
import com.google.gson.Gson
import com.google.gson.JsonObject
import java.io.*


class Restore {
    private val STRING_FOR_NULL_VALUE = "!!!string_for_null_value!!!"
    @Throws(Exception::class)
    private fun convertStreamToString(`is`: InputStream): String {
        val reader = BufferedReader(InputStreamReader(`is`))
        val sb = StringBuilder()
        var line: String
        while (reader.readLine().also { line = it } != null) {
            sb.append(line).append("\n")
        }
        reader.close()
        return sb.toString()
    }

    inner class Init {
        private var database: RoomDatabase? = null
        private var backupFilePath: String = ""
        private var onWorkFinishListener: OnWorkFinishListener? = null

        fun database(database: RoomDatabase?): Init {
            this.database = database
            return this
        }

        fun backupFilePath(backupFilePath: String): Init {
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
                val fl = File(backupFilePath)
                val fin = FileInputStream(fl)
                val data = convertStreamToString(fin)
                fin.close()
                val jsonTextDB: String
                jsonTextDB = data
                val jsonDB = Gson().fromJson(jsonTextDB, JsonObject::class.java)
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
                        query = "$query)"
                        val cc = database?.query(query, null)
                        cc?.close()
                    }
                }
                onWorkFinishListener?.onFinished(true, "success")
            } catch (e: Exception) {
                onWorkFinishListener?.onFinished(false, e.toString())
            }
        }
    }
}