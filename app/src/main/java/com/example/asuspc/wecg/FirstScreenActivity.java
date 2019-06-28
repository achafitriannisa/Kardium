package com.example.asuspc.wecg;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;

public class FirstScreenActivity extends AppCompatActivity implements View.OnClickListener {
    private FirebaseAuth mAuth;
    private Button buttonSignIn, buttonSignUp;

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_firstscreen);

        mAuth = FirebaseAuth.getInstance();
        buttonSignIn = (Button)findViewById(R.id.buttonSignIn);
        buttonSignUp = (Button)findViewById(R.id.buttonSignUp);

        buttonSignIn.setOnClickListener(this);
        buttonSignUp.setOnClickListener(this);

        if(mAuth.getCurrentUser() != null) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }
    }


    public void onClick (View view){
        if(view == buttonSignIn){
            startActivity(new Intent(this, SignInActivity.class));
            finish();
        } else if (view == buttonSignUp){
            startActivity(new Intent(this, SignUpActivity.class));
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        Intent a = new Intent(Intent.ACTION_MAIN);
        a.addCategory(Intent.CATEGORY_HOME);
        a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(a);
    }

}
