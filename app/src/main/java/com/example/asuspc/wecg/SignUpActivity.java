package com.example.asuspc.wecg;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener{

    //defining view objects
    private EditText editTextFirstName, editTextLastName, editTextEmail, editTextPassword, editTextDob, editTextDoctor;
    private RadioGroup radioGender;
    private Button buttonSignup;
    private int mYear, mMonth, mDay;

    private static final String FIRSTNAME_KEY = "firstName";
    private static final String LASTNAME_KEY = "lastName";
    private static final String EMAIL_KEY = "email";
    private static final String GENDER_KEY = "gender";
    private static final String DOB_KEY = "dob";
    private static final String DOC_ID_KEY = "doctorSTR";

    //defining firebaseauth object
    private FirebaseAuth firebaseAuth;
    String user_id;

    //defining firestore oject
    private FirebaseFirestore firebaseFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        //initializing views
        buttonSignup = (Button) findViewById(R.id.buttonSignUp);

        editTextFirstName = (EditText) findViewById(R.id.editTextFirstName);
        editTextLastName = (EditText) findViewById(R.id.editTextLastName);
        editTextEmail = (EditText) findViewById(R.id.editTextEmail);
        editTextPassword = (EditText) findViewById(R.id.editTextPassword);
        radioGender = (RadioGroup) findViewById(R.id.radioGender);
        editTextDob = (EditText) findViewById(R.id.editTextDob);
        editTextDoctor = (EditText) findViewById(R.id.editTextDoctor);

        //attaching listener to button
        editTextDob.setOnClickListener(this);
        buttonSignup.setOnClickListener(this);

        //initializing firebase auth object
        firebaseAuth = FirebaseAuth.getInstance();

        firebaseFirestore = FirebaseFirestore.getInstance();

    }

    private void registerUser(){

        //getting email and password from edit texts
        String email = editTextEmail.getText().toString().trim();
        String password  = editTextPassword.getText().toString().trim();

        //checking if email and passwords are empty
        if(TextUtils.isEmpty(email)){
            Toast.makeText(this,"Please enter email",Toast.LENGTH_LONG).show();
            return;
        }

        if(TextUtils.isEmpty(password)){
            Toast.makeText(this,"Please enter password",Toast.LENGTH_LONG).show();
            return;
        }

        //if the email and password are not empty
        //displaying a progress dialog
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Registering Please Wait...");
        progressDialog.show();

        //creating a new user
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        //checking if success
                        if(task.isSuccessful()){
                            addUser();
                            //display some message here
                            Toast.makeText(SignUpActivity.this,"Successfully registered",Toast.LENGTH_LONG).show();
                            //calling next activity
                            startActivity(new Intent(SignUpActivity.this, MainActivity.class));
                        }else{
                            //display some message here
                            Toast.makeText(SignUpActivity.this,"Registration Error",Toast.LENGTH_LONG).show();
                        }
                        progressDialog.dismiss();
                    }
                });

    }

    private void addUser() {
        //getting the user values
        final RadioButton radioSex = (RadioButton) findViewById(radioGender.getCheckedRadioButtonId());

        //getting the values to save
        user_id = firebaseAuth.getCurrentUser().getUid();
        String firstName = editTextFirstName.getText().toString().trim();
        String lastName = editTextLastName.getText().toString().trim();
        String email = editTextEmail.getText().toString().trim();
        String gender = radioSex.getText().toString();
        String dob = editTextDob.getText().toString().trim();
        String docId = editTextDoctor.getText().toString().trim();

        Map< String, Object > users = new HashMap< >();

        users.put(FIRSTNAME_KEY, firstName);
        users.put(LASTNAME_KEY, lastName);
        users.put(EMAIL_KEY, email);
        users.put(GENDER_KEY, gender);
        users.put(DOB_KEY, dob);
        users.put(DOC_ID_KEY, docId);

        firebaseFirestore.collection("patients").document(user_id).set(users)
                .addOnSuccessListener(new OnSuccessListener< Void >() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(SignUpActivity.this, "User info added",
                                Toast.LENGTH_SHORT).show();
                    }

                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(SignUpActivity.this, "ERROR" + e.toString(),
                                Toast.LENGTH_SHORT).show();
                        Log.d("TAG", e.toString());
                    }
                });
   }

    @Override
    public void onClick(View view) {
        if(view == buttonSignup){
            //calling register method on click
            registerUser();
        }
        if(view == editTextDob) {
            // Get Current Date
            final Calendar c = Calendar.getInstance();
            mYear = c.get(Calendar.YEAR);
            mMonth = c.get(Calendar.MONTH);
            mDay = c.get(Calendar.DAY_OF_MONTH);


            DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                    new DatePickerDialog.OnDateSetListener() {

                        @Override
                        public void onDateSet(DatePicker view, int year,
                                              int monthOfYear, int dayOfMonth) {

                            editTextDob.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);

                        }
                    }, mYear, mMonth, mDay);
            datePickerDialog.show();
        }
    }
}
