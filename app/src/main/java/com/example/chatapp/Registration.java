package com.example.chatapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Registration extends AppCompatActivity {
    TextView log; Button b;
    EditText fname, lname, email, number, pwd, confirm_pwd;
    RadioGroup rg;
    RadioButton r1, r2;
    FirebaseAuth mAuth;
    DatabaseReference databaseRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeUtils.applyTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        mAuth = FirebaseAuth.getInstance();
        databaseRef = FirebaseDatabase.getInstance().getReference("User");

        log = findViewById(R.id.textView7);
        b = findViewById(R.id.button3);
        fname = findViewById(R.id.editTextText2);
        lname = findViewById(R.id.editTextText);
        email = findViewById(R.id.editTextTextEmailAddress3);
        number = findViewById(R.id.editTextPhone2);
        pwd = findViewById(R.id.editTextTextPassword4);
        confirm_pwd = findViewById(R.id.editTextTextPassword5);
        rg = findViewById(R.id.radioGroup);
        r1 = findViewById(R.id.radioButton);
        r2 = findViewById(R.id.radioButton2);

        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String firstName = fname.getText().toString().trim();
                String lastName = lname.getText().toString().trim();
                String emailInput = email.getText().toString().trim();
                String phoneInput = number.getText().toString().trim();
                String password = pwd.getText().toString().trim();
                String confirmPassword = confirm_pwd.getText().toString().trim();
                String gender = r1.isChecked() ? "Male" : r2.isChecked() ? "Female" : "";

                if (firstName.isEmpty() || lastName.isEmpty() || emailInput.isEmpty() || phoneInput.isEmpty() || password.isEmpty() || confirmPassword.isEmpty() || gender.isEmpty()) {
                    Toast.makeText(Registration.this, "Each field must be filled or selected!", Toast.LENGTH_SHORT).show();
                    return;
                } else if (!firstName.matches("[a-zA-Z]+") || !lastName.matches("[a-zA-Z]+")) {
                    Toast.makeText(Registration.this, "First and Last name must contain only letters!", Toast.LENGTH_SHORT).show();
                    return;
                } else if (phoneInput.length() != 10) {
                    Toast.makeText(Registration.this, "Enter a valid mobile number!", Toast.LENGTH_SHORT).show();
                    return;
                } else if (!Patterns.EMAIL_ADDRESS.matcher(emailInput).matches()) {
                    Toast.makeText(Registration.this, "Invalid email address!", Toast.LENGTH_SHORT).show();
                    return;
                } else if (password.length() < 6) {
                    Toast.makeText(Registration.this, "Password must be at least 6 characters!", Toast.LENGTH_SHORT).show();
                    return;
                } else if (!password.equals(confirmPassword)) {
                    Toast.makeText(Registration.this, "Password and Confirm Password must match!", Toast.LENGTH_SHORT).show();
                    return;
                }

                mAuth.createUserWithEmailAndPassword(emailInput, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    String userId = mAuth.getCurrentUser().getUid();
                                    User user = new User(firstName,lastName,phoneInput,emailInput,password,gender);
                                    if (userId != null) {
                                        databaseRef.child(userId).setValue(user)
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        Toast.makeText(Registration.this, "You are registered and your data saved.", Toast.LENGTH_SHORT).show();
                                                        startActivity(new Intent(getApplicationContext(), Login.class));
                                                        finish();
                                                    }
                                                })
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Toast.makeText(Registration.this, "Data saving failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                    }
                                } else {
                                    Toast.makeText(Registration.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });

        log.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i1 = new Intent(getApplicationContext(), Login.class);
                startActivity(i1);
            }
        });
    }

}