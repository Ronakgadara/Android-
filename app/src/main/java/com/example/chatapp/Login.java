package com.example.chatapp;

import android.content.Intent;
import android.graphics.Region;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;
import com.onesignal.OneSignal;

public class Login extends AppCompatActivity {

    private Button b;
    private EditText u_email, u_password;
    private TextView reg,forgetpwd;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeUtils.applyTheme(this);
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();

        setContentView(R.layout.activity_login);

        b = findViewById(R.id.buttonLogin);
        u_email = findViewById(R.id.editTextEmail);
        u_password = findViewById(R.id.editTextPassword);
        reg = findViewById(R.id.textViewRegister);
        forgetpwd = findViewById(R.id.forgetPwd);

        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = u_email.getText().toString().trim();
                String password = u_password.getText().toString().trim();

                if (email.isEmpty() || password.isEmpty()) {
                    Toast.makeText(Login.this, "Enter email and password", Toast.LENGTH_SHORT).show();
                    return;
                }

                mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                                    Log.d("OneSignal", "Logging into OneSignal with UID: " + uid);
                                    OneSignal.login(uid);
                                    Toast.makeText(Login.this, "Login successful", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(Login.this, Dashboard.class));
                                    finish();
                                } else {
                                    Toast.makeText(Login.this, "Authentication failed", Toast.LENGTH_LONG).show();
                                }
                            }
                        });
            }
        });

        reg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(),Registration.class);
                startActivity(i);
                finish();
            }
        });

        forgetpwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(),ForgetPassword.class);
                startActivity(i);
                finish();
            }
        });
    }
}