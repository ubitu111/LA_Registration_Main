package ru.kireev.mir.laregistrationmain.ui.main;

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
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import ru.kireev.mir.laregistrationmain.R;
import ru.kireev.mir.laregistrationmain.adapters.VolunteerAdapter;
import ru.kireev.mir.laregistrationmain.data.MainViewModel;
import ru.kireev.mir.laregistrationmain.data.Volunteer;

/**
 * A placeholder fragment containing a simple view.
 */
public class SentVolunteersFragment extends Fragment {

    private MainViewModel mainViewModel;
    private VolunteerAdapter adapter;
    private RecyclerView recyclerView;
    private LiveData<List<Volunteer>> volunteers;

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_tabbed_sent_volunteers, container, false);
        recyclerView = root.findViewById(R.id.recyclerViewSentVolunteersTab);
        adapter = new VolunteerAdapter();
        adapter.setOnVolunteerLongClickListener(new VolunteerAdapter.OnVolunteerLongClickListener() {
            @Override
            public void onLongClick(int position) {
                onClickDeleteVolunteer(adapter.getVolunteers().get(position));
            }
        });
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mainViewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        volunteers = mainViewModel.getSentVolunteers();
        volunteers.observe(this, new Observer<List<Volunteer>>() {
            @Override
            public void onChanged(List<Volunteer> volunteers) {
                adapter.setVolunteers(volunteers);
            }
        });
        return root;
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