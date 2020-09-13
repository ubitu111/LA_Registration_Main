package ru.kireev.mir.registrarlizaalert.data

import androidx.room.Dao
import androidx.room.RawQuery
import androidx.sqlite.db.SupportSQLiteQuery

@Dao
interface MainDao {

    @RawQuery
    fun clearAutoIncrementCounter(query: SupportSQLiteQuery): Boolean
}