package ru.kireev.mir.registrarlizaalert.data

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface FoxesDao {
    @Query("SELECT * FROM foxes")
    fun getAllFoxes(): LiveData<List<Fox>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertFox(fox: Fox): Long

    @Delete
    fun deleteFox(fox: Fox)

    @Query("DELETE FROM foxes")
    fun deleteAllFoxes()

    @Update
    fun updateFox(fox: Fox)

    @Query("SELECT * FROM foxes WHERE ID = :id")
    fun getFoxById(id: Int): Fox

    @Query("SELECT id FROM foxes WHERE numberOfFox == :numberOfFox")
    fun getFoxIdByNumber(numberOfFox: Int): Int
}