package ru.kireev.mir.registrarlizaalert.data

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface FoxesDao {
    @Query("SELECT * FROM foxes")
    fun getAllFoxes(): LiveData<List<Fox>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertFox(fox: Fox)

    @Delete
    fun deleteFox(fox: Fox)

    @Query("DELETE FROM foxes")
    fun deleteAllFoxes()

    @Query("SELECT numberOfFox FROM foxes WHERE ID = (SELECT MAX(ID) FROM foxes)")
    fun getLastNumberOfFox(): Int

}