package ru.kireev.mir.registrarlizaalert;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;

import java.util.ArrayList;
import java.util.List;

import ru.kireev.mir.registrarlizaalert.adapters.VolunteerAutoCompleteAdapter;
import ru.kireev.mir.registrarlizaalert.data.MainViewModel;
import ru.kireev.mir.registrarlizaalert.data.Volunteer;
import ru.kireev.mir.registrarlizaalert.databinding.ActivityAddNewFoxBinding;

public class AddNewFoxActivity_old_java extends AppCompatActivity {
    private ActivityAddNewFoxBinding binding;
    private MainViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddNewFoxBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        final VolunteerAutoCompleteAdapter autoCompleteAdapter = new VolunteerAutoCompleteAdapter(this, new ArrayList<>());

        viewModel = new ViewModelProvider(this).get(MainViewModel.class);
        viewModel.getNotAddedToFoxVolunteers().observe(this, new Observer<List<Volunteer>>() {
            @Override
            public void onChanged(List<Volunteer> volunteers) {
                autoCompleteAdapter.setFullList(volunteers);
            }
        });
        binding.actvAddFoxElder.setAdapter(autoCompleteAdapter);
        binding.actvAddFoxSearcher.setAdapter(autoCompleteAdapter);
        binding.actvAddFoxSearcher.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Volunteer volunteer = (Volunteer) parent.getItemAtPosition(position);
                volunteer.setAddedToFox("true");
                viewModel.insertVolunteer(volunteer);
            }
        });
        binding.actvAddFoxElder.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Volunteer volunteer = (Volunteer) parent.getItemAtPosition(position);
                volunteer.setAddedToFox("true");
                viewModel.insertVolunteer(volunteer);
            }
        });
    }

    public void onClickSaveFox(View view) {
    }

    public void onClickAddNewMember(View view) {
    }

//    private List<Volunteer> getMock(){
//        List<Volunteer> mock = new ArrayList<>();
//        mock.add(new Volunteer(1,"Vova","Kireev","Mir",null,null,null,null,null,null,null));
//        mock.add(new Volunteer(1,"Вова","Киреев","Мир",null,null,null,null,null,null,null));
//        mock.add(new Volunteer(1,"Саша","Киреева","Коса",null,null,null,null,null,null,null));
//        mock.add(new Volunteer(1,"Полина","Черных","Пони",null,null,null,null,null,null,null));
//        mock.add(new Volunteer(1,"Антон","Деревягин","Умненький",null,null,null,null,null,null,null));
//        return mock;
//    }
}

