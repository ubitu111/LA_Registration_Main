package ru.kireev.mir.registrarlizaalert.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

//@Dao
public interface VolunteersDao_old_java {
    @Query("SELECT * FROM volunteers ORDER BY `index` ASC")
    LiveData<List<Volunteer>> getAllVolunteers();

    @Query("SELECT * FROM volunteers WHERE isSent == 'true'")
    LiveData<List<Volunteer>> getSentVolunteers();

    @Query("SELECT * FROM volunteers WHERE isSent == 'false'")
    LiveData<List<Volunteer>> getNotSentVolunteers();

    @Query("SELECT * FROM volunteers WHERE isAddedToFox == 'true'")
    LiveData<List<Volunteer>> getAddedToFoxVolunteers();

    @Query("SELECT * FROM volunteers WHERE isAddedToFox == 'false'")
    LiveData<List<Volunteer>> getNotAddedToFoxVolunteers();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertVolunteer(Volunteer volunteer);

    @Delete
    void deleteVolunteer(Volunteer volunteer);

    @Query("DELETE FROM volunteers")
    void deleteAllVolunteers();
}
