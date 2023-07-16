package com.example.tutron;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;

public class TutorRegistrationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutor_registration);

        // Declare component variables
        EditText editTextFirstName, editTextLastName, editTextEmail,
                editTextPassword, editTextExperience;
        RadioGroup nativeLanguageRadioGroup, educationLevelRadioGroup;
        Button btnSubmit;

        // Initialize component variables
        editTextFirstName = findViewById(R.id.editTextTutorFirstName);
        editTextLastName = findViewById(R.id.editTextTutorLastName);
        editTextEmail = findViewById(R.id.editTextTutorEmail);
        editTextPassword = findViewById(R.id.editTextTutorPassword);
        editTextExperience = findViewById(R.id.editTextExperience);
        nativeLanguageRadioGroup = findViewById(R.id.radioGroupNativeLanguage);
        educationLevelRadioGroup = findViewById(R.id.radioGroupEducationLevel);
        btnSubmit = findViewById(R.id.btnTutorSubmit);

        // Create ArrayList with all editTexts for easy validation
        ArrayList<EditText> editTexts = new ArrayList<>(Arrays.asList(
                editTextFirstName,
                editTextLastName,
                editTextEmail,
                editTextPassword,
                editTextExperience
        ));

        // Create on click listener for submit button
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Check all inputs are filled
                if (!RegistrationUtil.validTextInputs(editTexts) ||
                        nativeLanguageRadioGroup.getCheckedRadioButtonId() == -1 ||
                        educationLevelRadioGroup.getCheckedRadioButtonId() == -1) {
                    Toast.makeText(TutorRegistrationActivity.this,
                            "Please fill all fields!", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Create Tutor object
                RadioButton rBtnEducationLevel = findViewById(educationLevelRadioGroup.getCheckedRadioButtonId());
                RadioButton rBtnNativeLanguage = findViewById(nativeLanguageRadioGroup.getCheckedRadioButtonId());
                String educationLevel = rBtnEducationLevel.getText().toString();
                String nativeLanguage = rBtnNativeLanguage.getText().toString();
                Tutor tutor = new Tutor(null, editTextFirstName.getText().toString(), editTextLastName.getText().toString(),
                        educationLevel, nativeLanguage, editTextExperience.getText().toString(), 0);

                // Attempt to create account and register tutor
                RegistrationUtil.createAccount(editTextEmail.getText().toString(),
                        editTextPassword.getText().toString(), null, tutor, TutorRegistrationActivity.this);
            }
        });
    }
}