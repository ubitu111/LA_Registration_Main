package ru.kireev.mir.registrarlizaalert.ui.main;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.clans.fab.FloatingActionButton;

import java.util.List;

import ru.kireev.mir.registrarlizaalert.R;
import ru.kireev.mir.registrarlizaalert.adapters.VolunteerAdapter;
import ru.kireev.mir.registrarlizaalert.data.MainViewModel;
import ru.kireev.mir.registrarlizaalert.data.Volunteer;

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
        adapter.setOnVolunteerLongClickListener(new VolunteerAdapter.OnVolunteerLongClickListener() {
            @Override
            public void onLongClick(Volunteer volunteer) {
                onClickDeleteVolunteer(volunteer);
            }
        });
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mainViewModel = new ViewModelProvider(this).get(MainViewModel.class);
        volunteers = mainViewModel.getAllVolunteers();
        volunteers.observe(getViewLifecycleOwner(), new Observer<List<Volunteer>>() {
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
        alertDialog.setTitle(getString(R.string.warning));
        alertDialog.setMessage(getString(R.string.message_confirm_delete_all));
        alertDialog.setPositiveButton(getString(R.string.delete_all), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mainViewModel.deleteAllVolunteers();
            }
        });
        alertDialog.setNegativeButton(getString(R.string.cancel), null);
        alertDialog.show();
    }

    @Override
    public void onClick(View v) {
        onClickDeleteAllTab();
    }

    private void onClickDeleteVolunteer (final Volunteer volunteer) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext());
        alertDialog.setTitle(getString(R.string.warning));
        alertDialog.setMessage(getString(R.string.message_confirm_delete_one));
        alertDialog.setPositiveButton(getString(R.string.delete_all), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mainViewModel.deleteVolunteer(volunteer);
            }
        });
        alertDialog.setNegativeButton(getString(R.string.cancel), null);
        alertDialog.show();
    }
}