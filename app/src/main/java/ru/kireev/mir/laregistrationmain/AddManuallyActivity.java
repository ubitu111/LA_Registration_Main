package ru.kireev.mir.laregistrationmain;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import ru.kireev.mir.laregistrationmain.data.MainViewModel;
import ru.kireev.mir.laregistrationmain.data.Volunteer;

public class AddManuallyActivity extends AppCompatActivity {

    private EditText editTextName;
    private EditText editTextSurname;
    private EditText editTextCallSign;
    private EditText editTextPhoneNumber;
    private String name;
    private String surname;
    private String callSign;
    private String phoneNumber;
    MainViewModel mainViewModel;
    int index;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_manually);

        editTextName = findViewById(R.id.editTextName);
        editTextSurname = findViewById(R.id.editTextSurname);
        editTextCallSign = findViewById(R.id.editTextCallSign);
        editTextPhoneNumber = findViewById(R.id.editTextPhoneNumber);
        mainViewModel = ViewModelProviders.of(this).get(MainViewModel.class);

        Intent intent = getIntent();
        index = intent.getIntExtra("size",0);
    }

    public void onClickSaveData(View view) {
        name = editTextName.getText().toString();
        surname = editTextSurname.getText().toString();
        callSign = editTextCallSign.getText().toString();
        phoneNumber = editTextPhoneNumber.getText().toString();
        if (name.isEmpty() || surname.isEmpty() || phoneNumber.isEmpty()) {
            Toast.makeText(this, "Заполните поля Имя, Фамилия и Номер телефона!", Toast.LENGTH_SHORT).show();
        }
        else {
            Volunteer volunteer;
            if (index == 0) {
                volunteer = new Volunteer(0, name, surname, callSign, phoneNumber, "false");
            }
            else {
                volunteer = new Volunteer(index, name, surname, callSign, phoneNumber, "false");
            }
            mainViewModel.insertVolunteer(volunteer);
            onBackPressed();
        }

    }
}
