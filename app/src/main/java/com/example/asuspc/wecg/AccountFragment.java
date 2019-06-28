package com.example.asuspc.wecg;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;

public class AccountFragment extends Fragment implements View.OnClickListener {

    //defining view objects
    private EditText editTextFirstName, editTextLastName, editTextEmail, editTextDob, editTextDoctor;
    public TextView textViewRecords;
    private RadioGroup radioGender;
    private Button buttonUpdate,buttonLogout;
    private int mYear, mMonth, mDay;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;

    String user_id;

    private static final String FIRSTNAME_KEY = "firstName";
    private static final String LASTNAME_KEY = "lastName";
    private static final String EMAIL_KEY = "email";
    private static final String GENDER_KEY = "gender";
    private static final String DOB_KEY = "dob";
    private static final String DOC_ID_KEY = "doctorSTR";
    private static final String RECORDS_KEY = "records";

    //fragment view
    View view;

    //doc reference
    public DocumentReference user;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_account, container, false);
        return view;
    }

    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //initializing firebase auth object
        firebaseAuth = FirebaseAuth.getInstance();

        firebaseFirestore = FirebaseFirestore.getInstance();

        //initializing views
        buttonUpdate = (Button) view.findViewById(R.id.buttonUpdate);
        buttonLogout = (Button) view.findViewById(R.id.buttonLogout);
        editTextFirstName = (EditText) view.findViewById(R.id.editTextFirstName);
        editTextLastName = (EditText) view.findViewById(R.id.editTextLastName);
        editTextEmail = (EditText) view.findViewById(R.id.editTextEmail);
        radioGender = (RadioGroup) view.findViewById(R.id.radioGender);
        editTextDob = (EditText) view.findViewById(R.id.editTextDob);
        editTextDoctor = (EditText) view.findViewById(R.id.editTextDoctor);
        textViewRecords = (TextView) view.findViewById(R.id.textViewRecords);

        //set title for toolbar
        getActivity().setTitle(R.string.action_account);

        editTextEmail.setFocusable(false);
        editTextEmail.setOnClickListener(this);
        editTextDob.setOnClickListener(this);
        buttonUpdate.setOnClickListener(this);
        buttonLogout.setOnClickListener(this);

        //setting up the user profile
        user_id = firebaseAuth.getCurrentUser().getUid();

        displayUser();
    }

    public void displayUser() {
        user = firebaseFirestore.collection("patients").document(user_id);

        user.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task< DocumentSnapshot > task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot doc = task.getResult();
                    if(doc.get(FIRSTNAME_KEY) != null)
                        editTextFirstName.setText(doc.get(FIRSTNAME_KEY).toString());
                    if(doc.get(LASTNAME_KEY) != null)
                        editTextLastName.setText(doc.get(LASTNAME_KEY).toString());
                    editTextEmail.setText(doc.get(EMAIL_KEY).toString());
                    if(doc.get(GENDER_KEY).toString().equals("Male")){
                        radioGender.check(R.id.Male);
                    } else
                        radioGender.check(R.id.Female);
                    if(doc.get(DOB_KEY) != null)
                        editTextDob.setText(doc.get(DOB_KEY).toString());
                    if(doc.get(DOC_ID_KEY) != null)
                        editTextDoctor.setText(doc.get(DOC_ID_KEY).toString());
                    if(doc.get(RECORDS_KEY) != null) {
                        String records = doc.get(RECORDS_KEY).toString();
                        textViewRecords.setText(records.substring(1, records.length()-1).replaceAll(", ", "\n"));
                    }
                }

            }

        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
    }

    private void updateUser() {
        //setting the email field to be uneditable
        editTextEmail.getFreezesText();

        //getting the values to save
        final RadioButton radioSex = (RadioButton) view.findViewById(radioGender.getCheckedRadioButtonId());
        String firstName = editTextFirstName.getText().toString().trim();
        String lastName = editTextLastName.getText().toString().trim();
        String gender = radioSex.getText().toString();
        String dob = editTextDob.getText().toString().trim();
        String docId = editTextDoctor.getText().toString().trim();

        //getting the document reference
        DocumentReference users = firebaseFirestore.collection("patients").document(user_id);
        users.update(FIRSTNAME_KEY, firstName);
        users.update(LASTNAME_KEY, lastName);
        users.update(GENDER_KEY, gender);
        users.update(DOB_KEY, dob);
        users.update(DOC_ID_KEY, docId)
            .addOnSuccessListener(new OnSuccessListener< Void >() {
                @Override
                public void onSuccess(Void aVoid) {
                Toast.makeText(view.getContext(), "Updated Successfully", Toast.LENGTH_SHORT).show();
            }

        });

        displayUser();
    }

    @Override
    public void onClick(View view) {
        if (view == buttonUpdate) {
            updateUser();
        }

        if (view == editTextDob){
            // Get Current Date
            final Calendar c = Calendar.getInstance();
            mYear = c.get(Calendar.YEAR);
            mMonth = c.get(Calendar.MONTH);
            mDay = c.get(Calendar.DAY_OF_MONTH);


            DatePickerDialog datePickerDialog = new DatePickerDialog(view.getContext(),
                    new DatePickerDialog.OnDateSetListener() {

                        @Override
                        public void onDateSet(DatePicker view, int year,
                                              int monthOfYear, int dayOfMonth) {

                            editTextDob.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);

                        }
                    }, mYear, mMonth, mDay);
            datePickerDialog.show();
        }
        if (view == buttonLogout){
            firebaseAuth.signOut();
            startActivity(new Intent(view.getContext(), FirstScreenActivity.class));
        }
        if (view == editTextEmail){
            Toast.makeText(view.getContext(),"You can't update your email yet",Toast.LENGTH_SHORT).show();
        }

    }
}