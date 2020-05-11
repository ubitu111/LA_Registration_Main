package ru.kireev.mir.registrarlizaalert.ui.main;

import android.content.DialogInterface;
import android.content.Intent;
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
import com.github.clans.fab.FloatingActionMenu;

import java.util.List;

import ru.kireev.mir.registrarlizaalert.AddManuallyActivity;
import ru.kireev.mir.registrarlizaalert.BarCodeScannerActivity;
import ru.kireev.mir.registrarlizaalert.R;
import ru.kireev.mir.registrarlizaalert.adapters.VolunteerAdapter;
import ru.kireev.mir.registrarlizaalert.data.MainViewModel;
import ru.kireev.mir.registrarlizaalert.data.Volunteer;

/**
 * A placeholder fragment containing a simple view.
 */
public class NotSentVolunteersFragment extends Fragment implements View.OnClickListener {

    private MainViewModel mainViewModel;
    private VolunteerAdapter adapter;
    private RecyclerView recyclerView;
    private LiveData<List<Volunteer>> volunteers;
    private FloatingActionMenu famMenu;
    private FloatingActionButton fabManually;
    private FloatingActionButton fabScanner;
    private FloatingActionButton fabSent;
    private boolean isSentToInforg = false;


    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_tabbed_not_sent_volunteers, container, false);

        recyclerView = root.findViewById(R.id.recyclerViewNotSentVolunteersTab);
        famMenu = root.findViewById(R.id.fam_menu_tab);
        adapter = new VolunteerAdapter();
        adapter.setOnVolunteerLongClickListener(new VolunteerAdapter.OnVolunteerLongClickListener() {
            @Override
            public void onLongClick(int position) {
                onClickDeleteVolunteer(adapter.getVolunteers().get(position));
            }
        });
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mainViewModel = new ViewModelProvider(this).get(MainViewModel.class);
        volunteers = mainViewModel.getNotSentVolunteers();
        volunteers.observe(getViewLifecycleOwner(), new Observer<List<Volunteer>>() {
            @Override
            public void onChanged(List<Volunteer> volunteers) {
                adapter.setVolunteers(volunteers);
            }
        });

        fabManually = root.findViewById(R.id.fab_buttonAddManuallyTab);
        fabScanner = root.findViewById(R.id.fab_buttonAddByScannerTab);
        fabSent = root.findViewById(R.id.fab_buttonSentNewTab);
        fabManually.setOnClickListener(this);
        fabScanner.setOnClickListener(this);
        fabSent.setOnClickListener(this);

        return root;
    }



    private void onClickAddManuallyTab() {
        Intent intent = new Intent(getContext(), AddManuallyActivity.class);
        intent.putExtra("size", adapter.getItemCount());
        startActivity(intent);
        famMenu.close(true);
    }

    private void onClickAddByScannerTab() {
        Intent intent = new Intent(getContext(), BarCodeScannerActivity.class);
        intent.putExtra("size", adapter.getItemCount());
        startActivity(intent);
        famMenu.close(true);
    }

    private void onClickSentNewTab() {
        isSentToInforg = true;
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
                    .append(" ")
                    .append(volunteer.getCarMark())
                    .append(" ")
                    .append(volunteer.getCarModel())
                    .append(" ")
                    .append(volunteer.getCarRegistrationNumber())
                    .append(" ")
                    .append(volunteer.getCarColor())
                    .append("\n");

        }
        String message = builder.toString();
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, message);
        Intent choosenIntent = Intent.createChooser(intent,getString(R.string.chooser_title));
        startActivity(choosenIntent);

        famMenu.close(true);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab_buttonAddManuallyTab :
                onClickAddManuallyTab();
                break;
            case R.id.fab_buttonAddByScannerTab :
                onClickAddByScannerTab();
                break;
            case R.id.fab_buttonSentNewTab :
                onClickSentNewTab();
                break;
        }
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

    @Override
    public void onResume() {
        if (isSentToInforg) {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext());
            alertDialog.setTitle(getString(R.string.attention));
            alertDialog.setMessage(getString(R.string.message_confirm_sent_to_inforg));
            alertDialog.setPositiveButton(getString(R.string.sent_successfully), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    List<Volunteer> volunteers = adapter.getVolunteers();
                    for (Volunteer volunteer : volunteers) {
                        mainViewModel.deleteVolunteer(volunteer);
                        volunteer.setSent("true");
                        mainViewModel.insertVolunteer(volunteer);
                    }
                }
            });
            alertDialog.setNegativeButton(getString(R.string.not_sent), null);
            alertDialog.show();
            isSentToInforg = false;
        }

        super.onResume();
    }
}