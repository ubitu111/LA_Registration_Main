package ru.kireev.mir.laregistrationmain;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.github.clans.fab.FloatingActionMenu;

import java.util.List;

import ru.kireev.mir.laregistrationmain.adapters.VolunteerAdapter;
import ru.kireev.mir.laregistrationmain.data.MainViewModel;
import ru.kireev.mir.laregistrationmain.data.Volunteer;

public class NotSentVolunteersActivity extends AppCompatActivity {
    private MainViewModel mainViewModel;
    private VolunteerAdapter adapter;
    private RecyclerView recyclerView;
    private LiveData<List<Volunteer>> volunteers;
    private FloatingActionMenu famMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_not_sent_volunteers);
        recyclerView = findViewById(R.id.recyclerViewNotSentVolunteers);
        famMenu = findViewById(R.id.fam_menu);
        adapter = new VolunteerAdapter();
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        mainViewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        volunteers = mainViewModel.getNotSentVolunteers();
        volunteers.observe(this, new Observer<List<Volunteer>>() {
            @Override
            public void onChanged(List<Volunteer> volunteers) {
                adapter.setVolunteers(volunteers);
            }
        });
    }

    public void onClickAddManually(View view) {
        Intent intent = new Intent(this, AddManuallyActivity.class);
        intent.putExtra("size", adapter.getItemCount());
        startActivity(intent);
        famMenu.close(true);
    }

    public void onClickAddByScanner(View view) {
        Intent intent = new Intent(this, BarCodeScannerActivity.class);
        intent.putExtra("size", adapter.getItemCount());
        startActivity(intent);
        famMenu.close(true);
    }

    public void onClickSentNew(View view) {
        StringBuilder builder = new StringBuilder();
        List<Volunteer> volunteers = adapter.getVolunteers();
        for (Volunteer volunteer : volunteers) {
            builder.append(volunteer.getName())
                    .append(" ")
                    .append(volunteer.getSurname())
                    .append(" ")
                    .append(volunteer.getCallSign())
                    .append(" ")
                    .append(volunteer.getPhoneNumber())
                    .append("\n");

        }
        String message = builder.toString();
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, message);
        Intent choosenIntent = Intent.createChooser(intent,getString(R.string.chooser_title));
        startActivity(choosenIntent);

        for (Volunteer volunteer : volunteers) {
            mainViewModel.deleteVolunteer(volunteer);
            volunteer.setSent("true");
            mainViewModel.insertVolunteer(volunteer);
        }

        famMenu.close(true);
    }

}
