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

    @Update
    fun updateFox(fox: Fox)

//    @Query("SELECT numberOfFox FROM foxes WHERE ID = (SELECT MAX(ID) FROM foxes)")
//    fun getLastNumberOfFox(): Int

    @Query("SELECT ID FROM foxes ORDER BY ID DESC LIMIT 1")
    fun getLastNumberOfFox():Int

    @Query("SELECT * FROM foxes WHERE ID = :id")
    fun getFoxById(id: Int): Fox
}