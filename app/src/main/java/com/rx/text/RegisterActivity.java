package com.rx.text;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;


public class RegisterActivity extends AppCompatActivity {
    //  private AwesomeValidation awesomeValidation;
    EditText firstName, surName, password, email, mobile_number;
    String patternPassword = "^[A-Za-z\\s]{3}[0-9]{3}$";
    String patternTelepone = "^[0-9]{2}[0-9]{8}$";
    String patternName = "[a-zA-Z\\\\s]+";
    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    Button register;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sms);

//definindo o objeto AwesomeValidationject
        //  awesomeValidation = new AwesomeValidation(ValidationStyle.BASIC);
        firstName = (EditText) findViewById(R.id.firstName);
        surName = (EditText) findViewById(R.id.surName);
        // designation = (EditText) findViewById(R.id.designation);
        password = (EditText) findViewById(R.id.password);
        email = (EditText) findViewById(R.id.email);
        mobile_number = (EditText) findViewById(R.id.mobile_number);
        register = (Button) findViewById(R.id.register);

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String first_Name = (String) firstName.getText().toString();
                String SurName = (String) surName.getText().toString();
                String Password = (String) password.getText().toString();
                String Email = (String) email.getText().toString();
                String Mobile_number = (String) mobile_number.getText().toString();

                if (Email != null &&
                        !Email.isEmpty() && first_Name != null && first_Name.isEmpty() &&
                        !SurName.isEmpty() && Password != null &&
                        !Password.isEmpty() && Mobile_number != null &&
                        Mobile_number.isEmpty()) {


                } else if (Email.isEmpty()) {
                    email.setError("Email is required!");
                } else if (first_Name.isEmpty()) {
                    firstName.setError(getResources().getString(R.string.first_name));
                } else if (SurName.isEmpty()) {
                    surName.setError(getResources().getString(R.string.last_name));
                } else if (Password.isEmpty()) {
                    password.setError(getResources().getString(R.string.clinic_name));
                } else if (Mobile_number.isEmpty()) {
                    mobile_number.setError(getResources().getString(R.string.number));
                } else if (Password.matches(patternPassword)) {
                    password.setError("invalid password");
                } else if (Email.matches(emailPattern) && Email.length() > 0) {
                    mobile_number.setError("invalid email");
                }


                //checkDataEntered();
            }
        });

        findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


    }


}