package ru.kireev.mir.registrarlizaalert.data.database.backup_and_restore_db

import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import java.util.*


class Json private constructor(val asJsonObject: JsonObject) {

    class Builder {
        private val `object`: JsonObject = JsonObject()
        fun putItem(key: String?, value: String?): Builder {
            val gson = Gson()
            val je = gson.toJsonTree(value)
            `object`.add(key, je)
            return this
        }

        fun putItem(key: String?, items: ArrayList<Json>): Builder {
            val jsonArray = JsonArray()
            for (item in items) {
                jsonArray.add(item.asJsonObject)
            }
            `object`.add(key, jsonArray)
            return this
        }

        fun build(): Json {
            return Json(`object`)
        }

    }
}