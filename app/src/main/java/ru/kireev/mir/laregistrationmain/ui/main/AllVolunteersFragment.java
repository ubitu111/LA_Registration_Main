package ru.kireev.mir.laregistrationmain.ui.main;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.clans.fab.FloatingActionButton;

import java.util.List;

import ru.kireev.mir.laregistrationmain.R;
import ru.kireev.mir.laregistrationmain.adapters.VolunteerAdapter;
import ru.kireev.mir.laregistrationmain.data.MainViewModel;
import ru.kireev.mir.laregistrationmain.data.Volunteer;

/**
 * A placeholder fragment containing a simple view.
 */
public class AllVolunteersFragment extends Fragment implements View.OnClickListener {

    private MainViewModel mainViewModel;
    private VolunteerAdapter adapter;
    private RecyclerView recyclerView;
    private LiveData<List<Volunteer>> volunteers;
    private FloatingActionButton fab;

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_tabbed_all_volunteers, container, false);
        recyclerView = root.findViewById(R.id.recyclerViewAllVolunteersTab);
        adapter = new VolunteerAdapter();
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mainViewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        volunteers = mainViewModel.getAllVolunteers();
        volunteers.observe(this, new Observer<List<Volunteer>>() {
            @Override
            public void onChanged(List<Volunteer> volunteers) {
                adapter.setVolunteers(volunteers);
            }
        });
        fab = root.findViewById(R.id.fab_buttonDeleteAllTab);
        fab.setOnClickListener(this);
        return root;
    }

    private void onClickDeleteAllTab() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext());
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

    @Override
    public void onClick(View v) {
        onClickDeleteAllTab();
    }
}