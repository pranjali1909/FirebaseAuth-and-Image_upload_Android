package com.example.imageupload;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class SignUpActivity extends AppCompatActivity {

    EditText editTextEmail,editTextPassword;
    Button buttonSignup;
    private FirebaseAuth auth;

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        auth=FirebaseAuth.getInstance();
        buttonSignup = (Button) findViewById(R.id.buttonSignup);

        buttonSignup.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                String email = editTextEmail.getText().toString();
                String password = editTextPassword.getText().toString();

                if(TextUtils.isEmpty(email)){
                    Toast.makeText(getApplicationContext(),"Please enter your E-mail address", Toast.LENGTH_LONG).show();
                    return;
                }
                if(TextUtils.isEmpty(password)){
                    Toast.makeText(getApplicationContext(),"Please enter your Password",Toast.LENGTH_LONG).show();
                }
                if (password.length() == 0){
                    Toast.makeText(getApplicationContext(),"Please enter your Password",Toast.LENGTH_LONG).show();
                }
                if (password.length()<8){
                    Toast.makeText(getApplicationContext(),"Password must be more than 8 digit",Toast.LENGTH_LONG).show();
                }
                else{
                    auth.createUserWithEmailAndPassword(email,password)
                            .addOnCompleteListener(SignUpActivity.this, new OnCompleteListener<AuthResult>() {
                                public void onComplete(@NonNull Task<AuthResult> task) {

                                    if (!task.isSuccessful()) {
                                        Toast.makeText(SignUpActivity.this, "ERROR",Toast.LENGTH_LONG).show();
                                    }
                                    else {
                                        startActivity(new Intent(SignUpActivity.this, HomeActivity.class));
                                        finish();
                                    }
                                }
                            });}
            }
        });
    }

    public void navigate_sign_in(View v){
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }
}
