package ru.kireev.mir.laregistrationmain.data;

import android.app.Application;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

public class MainViewModel extends AndroidViewModel {

    private static VolunteersDatabase database;
    private LiveData<List<Volunteer>> allVolunteers;
    private LiveData<List<Volunteer>> sentVolunteers;
    private LiveData<List<Volunteer>> notSentVolunteers;

    public MainViewModel(@NonNull Application application) {
        super(application);
        database = VolunteersDatabase.getInstance(getApplication());
        allVolunteers = database.volunteersDao().getAllVolunteers();
        sentVolunteers = database.volunteersDao().getSentVolunteers();
        notSentVolunteers = database.volunteersDao().getNotSentVolunteers();
    }

    public LiveData<List<Volunteer>> getAllVolunteers() {
        return allVolunteers;
    }

    public LiveData<List<Volunteer>> getSentVolunteers() {
        return sentVolunteers;
    }

    public LiveData<List<Volunteer>> getNotSentVolunteers() {
        return notSentVolunteers;
    }

    public void insertVolunteer (Volunteer volunteer) {
        new InsertTask().execute(volunteer);
    }

    public void deleteVolunteer (Volunteer volunteer) {
        new DeleteTask().execute(volunteer);
    }

    public void deleteAllVolunteers () {
        new DeleteAllTask().execute();
    }

    private static class InsertTask extends AsyncTask<Volunteer, Void, Void> {

        @Override
        protected Void doInBackground(Volunteer... volunteers) {
            if (volunteers != null && volunteers.length > 0) {
                database.volunteersDao().insertVolunteer(volunteers[0]);
            }
            return null;
        }
    }

    private static class DeleteTask extends AsyncTask<Volunteer, Void, Void>{

        @Override
        protected Void doInBackground(Volunteer... volunteers) {
            if (volunteers != null && volunteers.length > 0) {
                database.volunteersDao().deleteVolunteer(volunteers[0]);
            }
            return null;
        }
    }

    private static class DeleteAllTask extends AsyncTask<Void, Void, Void>{

        @Override
        protected Void doInBackground(Void... voids) {
            database.volunteersDao().deleteAllVolunteers();
            return null;
        }
    }
}
