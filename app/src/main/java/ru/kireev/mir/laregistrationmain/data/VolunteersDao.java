package ru.kireev.mir.laregistrationmain.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface VolunteersDao {
    @Query("SELECT * FROM volunteers ORDER BY `index` ASC")
    LiveData<List<Volunteer>> getAllVolunteers();

    @Query("SELECT * FROM volunteers WHERE isSent == 'true'")
    LiveData<List<Volunteer>> getSentVolunteers();

    @Query("SELECT * FROM volunteers WHERE isSent == 'false'")
    LiveData<List<Volunteer>> getNotSentVolunteers();

    @Insert
    void insertVolunteer(Volunteer volunteer);

    @Delete
    void deleteVolunteer(Volunteer volunteer);

    @Query("DELETE FROM volunteers")
    void deleteAllVolunteers();
}
