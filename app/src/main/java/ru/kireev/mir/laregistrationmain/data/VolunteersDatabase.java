package ru.kireev.mir.laregistrationmain.data;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {Volunteer.class}, version = 2, exportSchema = false)
public abstract class VolunteersDatabase extends RoomDatabase {
    private static VolunteersDatabase database;
    private static final String DB_NAME = "volunteers.db";
    private static final Object LOCK = new Object();

    public static VolunteersDatabase getInstance(Context context) {
        synchronized (LOCK) {
            if (database == null) {
                database = Room.databaseBuilder(context, VolunteersDatabase.class, DB_NAME)
                        .fallbackToDestructiveMigration()
                        .build();
            }
        }
        return database;
    }

    public abstract VolunteersDao volunteersDao();
}
