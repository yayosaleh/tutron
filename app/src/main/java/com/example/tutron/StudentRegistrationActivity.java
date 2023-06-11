package com.example.tutron;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.Arrays;

public class StudentRegistrationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_registration);

        // Declare component variables
        EditText editTextFirstName, editTextLastName, editTextEmail,
                editTextPassword, editTextAddress, editTextCity,
                editTextProvince, editTextPostalCode, editTextCreditCard,
                editTextExpiry, editTextCVV;
        Button btnSubmit;

        // Initialize component variables
        editTextFirstName = findViewById(R.id.editTextStudentFirstName);
        editTextLastName = findViewById(R.id.editTextStudentLastName);
        editTextEmail = findViewById(R.id.editTextStudentEmail);
        editTextPassword = findViewById(R.id.editTextStudentPassword);
        editTextAddress = findViewById(R.id.editTextAddress);
        editTextCity = findViewById(R.id.editTextCity);
        editTextProvince = findViewById(R.id.editTextProvince);
        editTextPostalCode = findViewById(R.id.editTextPostalCode);
        editTextCreditCard = findViewById(R.id.editTextCreditCard);
        editTextExpiry = findViewById(R.id.editTextExpiry);
        editTextCVV = findViewById(R.id.editTextCVV);
        btnSubmit = findViewById(R.id.btnStudentSubmit);

        // Create ArrayList with all editTexts for easy validation
        ArrayList<EditText> editTexts = new ArrayList<>(Arrays.asList(
                editTextFirstName,
                editTextLastName,
                editTextEmail,
                editTextPassword,
                editTextAddress,
                editTextCity,
                editTextProvince,
                editTextPostalCode,
                editTextCreditCard,
                editTextExpiry,
                editTextCVV
        ));

        // Create on click listener for submit button
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Check all text inputs are non-empty
                if (!RegistrationUtil.validTextInputs(editTexts)){
                    Toast.makeText(StudentRegistrationActivity.this,
                            "Please fill all fields!",
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                // Create Student object
                String address = editTextAddress.getText().toString() + "," +
                        editTextCity.getText().toString() + "," +
                        editTextProvince.getText().toString() + "," +
                        editTextPostalCode.getText().toString();

                String creditCardInfo = editTextCreditCard.getText().toString() + "," +
                        editTextExpiry.getText().toString() + "," +
                        editTextCVV.getText().toString();

                Student student = new Student(editTextFirstName.getText().toString(),
                        editTextLastName.getText().toString(), address, creditCardInfo);

                // Attempt to create account and register student
                RegistrationUtil.createAccount(editTextEmail.getText().toString(),
                        editTextPassword.getText().toString(), student, null, StudentRegistrationActivity.this);
            }
        });
    }
}