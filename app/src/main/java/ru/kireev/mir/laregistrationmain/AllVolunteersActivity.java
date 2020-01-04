package ru.kireev.mir.laregistrationmain;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;

import java.util.List;

import ru.kireev.mir.laregistrationmain.adapters.VolunteerAdapter;
import ru.kireev.mir.laregistrationmain.data.MainViewModel;
import ru.kireev.mir.laregistrationmain.data.Volunteer;

public class AllVolunteersActivity extends AppCompatActivity {

    private MainViewModel mainViewModel;
    private VolunteerAdapter adapter;
    private RecyclerView recyclerView;
    private LiveData<List<Volunteer>> volunteers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_volunteers);
        recyclerView = findViewById(R.id.recyclerViewAllVolunteers);
        adapter = new VolunteerAdapter();
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        mainViewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        volunteers = mainViewModel.getAllVolunteers();
        volunteers.observe(this, new Observer<List<Volunteer>>() {
            @Override
            public void onChanged(List<Volunteer> volunteers) {
                adapter.setVolunteers(volunteers);
            }
        });
    }

    public void onClickDeleteAll(View view) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle("Предупреждение");
        alertDialog.setMessage("Подтвердите удаление всех записей");
        alertDialog.setPositiveButton("Удалить", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mainViewModel.deleteAllVolunteers();
            }
        });
        alertDialog.setNegativeButton("Отмена", null);
        alertDialog.show();
    }
}
