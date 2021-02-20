package com.example.imageupload;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    private EditText EditTextEmail, EditTextPassword;
    private FirebaseAuth auth;
    private Button buttonsignin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Get Firebase auth instance
        auth = FirebaseAuth.getInstance();
        // set the view now
        setContentView(R.layout.activity_login);
        EditTextEmail = (EditText) findViewById(R.id.EditTextEmail);
        EditTextPassword = (EditText) findViewById(R.id.EditTextPassword);
        buttonsignin = (Button) findViewById(R.id.buttonsignin);
        //Get Firebase auth instance
        auth = FirebaseAuth.getInstance();

        buttonsignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = EditTextEmail.getText().toString();
                final String password = EditTextPassword.getText().toString();
                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(getApplicationContext(), "Enter your mail address", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(getApplicationContext(), "Enter your password", Toast.LENGTH_SHORT).show();
                    return;
                }
                //authenticate user
                auth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (!task.isSuccessful()) {
                                    // there was an error
                                    if (password.length() < 8) {
                                        Toast.makeText(getApplicationContext(),"Password must be more than 8 digit",Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(getApplicationContext(),"Error",Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                                    startActivity(intent);
                                    finish();
                                }
                            }
                        });
            }
        });
    }

    public void navigate_sign_up(View v) {
        Intent intent = new Intent(this, SignUpActivity.class);
        startActivity(intent);
    }

}