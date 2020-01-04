package ru.kireev.mir.laregistrationmain;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import java.util.List;

import ru.kireev.mir.laregistrationmain.adapters.VolunteerAdapter;
import ru.kireev.mir.laregistrationmain.data.MainViewModel;
import ru.kireev.mir.laregistrationmain.data.Volunteer;

public class SentVolunteersActivity extends AppCompatActivity {

    private MainViewModel mainViewModel;
    private VolunteerAdapter adapter;
    private RecyclerView recyclerView;
    private LiveData<List<Volunteer>> volunteers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sent_volunteers);
        recyclerView = findViewById(R.id.recyclerViewSentVolunteers);
        adapter = new VolunteerAdapter();
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        mainViewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        volunteers = mainViewModel.getSentVolunteers();
        volunteers.observe(this, new Observer<List<Volunteer>>() {
            @Override
            public void onChanged(List<Volunteer> volunteers) {
                adapter.setVolunteers(volunteers);
            }
        });
    }
}
