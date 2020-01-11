package ru.kireev.mir.laregistrationmain;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import ru.kireev.mir.laregistrationmain.data.MainViewModel;
import ru.kireev.mir.laregistrationmain.data.Volunteer;

public class AddManuallyActivity extends AppCompatActivity {

    private EditText editTextName;
    private EditText editTextSurname;
    private EditText editTextCallSign;
    private EditText editTextPhoneNumber;
    private CheckBox checkBoxHaveACar;
    private EditText editTextCarMark;
    private EditText editTextCarModel;
    private EditText editTextCarRegistrationNumber;
    private EditText editTextCarColor;
    private String name;
    private String surname;
    private String callSign;
    private String phoneNumber;
    private boolean haveACar;
    private String carMark;
    private String carModel;
    private String carRegistrationNumber;
    private String carColor;
    private MainViewModel mainViewModel;
    int index;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_manually);

        editTextName = findViewById(R.id.editTextName);
        editTextSurname = findViewById(R.id.editTextSurname);
        editTextCallSign = findViewById(R.id.editTextCallSign);
        editTextPhoneNumber = findViewById(R.id.editTextPhoneNumber);
        checkBoxHaveACar = findViewById(R.id.checkBoxHaveACar);
        editTextCarMark = findViewById(R.id.editTextCarMark);
        editTextCarModel = findViewById(R.id.editTextCarModel);
        editTextCarRegistrationNumber = findViewById(R.id.editTextCarRegistrationNumber);
        editTextCarColor = findViewById(R.id.editTextCarColor);
        mainViewModel = ViewModelProviders.of(this).get(MainViewModel.class);

        checkBoxHaveACar.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    editTextCarMark.setVisibility(View.VISIBLE);
                    editTextCarModel.setVisibility(View.VISIBLE);
                    editTextCarRegistrationNumber.setVisibility(View.VISIBLE);
                    editTextCarColor.setVisibility(View.VISIBLE);
                }
                else {
                    editTextCarMark.setVisibility(View.INVISIBLE);
                    editTextCarModel.setVisibility(View.INVISIBLE);
                    editTextCarRegistrationNumber.setVisibility(View.INVISIBLE);
                    editTextCarColor.setVisibility(View.INVISIBLE);
                }
            }
        });

        Intent intent = getIntent();
        index = intent.getIntExtra("size",0);
    }

    public void onClickSaveData(View view) {
        name = editTextName.getText().toString();
        surname = editTextSurname.getText().toString();
        callSign = editTextCallSign.getText().toString();
        phoneNumber = editTextPhoneNumber.getText().toString();
        haveACar = checkBoxHaveACar.isChecked();
        carMark = editTextCarMark.getText().toString();
        carModel = editTextCarModel.getText().toString();
        carRegistrationNumber = editTextCarRegistrationNumber.getText().toString();
        carColor = editTextCarColor.getText().toString();

        if (name.isEmpty() || surname.isEmpty() || phoneNumber.isEmpty()) {
            Toast.makeText(this, "Заполните поля Имя, Фамилия и Номер телефона!", Toast.LENGTH_SHORT).show();
        }
        else if (haveACar && (carMark.isEmpty() || carModel.isEmpty() || carRegistrationNumber.isEmpty() || carColor.isEmpty())) {
            Toast.makeText(this, "Заполните все данные об автомобиле!", Toast.LENGTH_SHORT).show();
        }
        else {
            Volunteer volunteer;
            if (index == 0) {
                volunteer = new Volunteer(0, name, surname, callSign, phoneNumber, "false",
                        carMark, carModel, carRegistrationNumber, carColor);
            }
            else {
                volunteer = new Volunteer(index, name, surname, callSign, phoneNumber, "false",
                        carMark, carModel, carRegistrationNumber, carColor);
            }
            mainViewModel.insertVolunteer(volunteer);
            onBackPressed();
        }

    }
}
