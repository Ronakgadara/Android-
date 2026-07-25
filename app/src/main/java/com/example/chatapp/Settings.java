package com.example.chatapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

public class Settings extends AppCompatActivity {

    Button logoutBtn, deleteAccountBtn;
    Switch switchDarkMode;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    TextView textChangePassword;
    private static final String PREF_NAME = "theme_pref";
    private static final String IS_DARK_MODE = "isDarkMode";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        sharedPreferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        boolean isDarkMode = sharedPreferences.getBoolean(IS_DARK_MODE, false);
        AppCompatDelegate.setDefaultNightMode(
                isDarkMode ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO
        );

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        switchDarkMode = findViewById(R.id.switchDarkMode);
        logoutBtn = findViewById(R.id.logout);
        deleteAccountBtn = findViewById(R.id.deleteAccount);
        textChangePassword = findViewById(R.id.textChangePassword);

        Toolbar toolbar = findViewById(R.id.chatToolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back_arrow); // Custom icon
        }

        switchDarkMode.setChecked(isDarkMode);

        switchDarkMode.setOnCheckedChangeListener((buttonView, isChecked) -> {
            editor = sharedPreferences.edit();
            editor.putBoolean(IS_DARK_MODE, isChecked);
            editor.apply();
            AppCompatDelegate.setDefaultNightMode(
                    isChecked ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO
            );
            recreate();
        });

        logoutBtn.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            Toast.makeText(Settings.this, "Logout successful", Toast.LENGTH_SHORT).show();
            Intent i = new Intent(getApplicationContext(), Login.class);
            startActivity(i);
            finish();
        });

        deleteAccountBtn.setOnClickListener(v -> showDeleteAccountDialog());

        textChangePassword.setOnClickListener(v -> showChangePasswordDialog());
    }

    private void showChangePasswordDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Change Password");

        View view = getLayoutInflater().inflate(R.layout.dialog_change_password, null);
        EditText oldPass = view.findViewById(R.id.oldPassword);
        EditText newPass = view.findViewById(R.id.newPassword);

        builder.setView(view);

        builder.setPositiveButton("Change", (dialog, which) -> {
            String oldPassword = oldPass.getText().toString().trim();
            String newPassword = newPass.getText().toString().trim();

            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user != null && !oldPassword.isEmpty() && newPassword.length() >= 6) {
                String email = user.getEmail();
                FirebaseAuth.getInstance().signInWithEmailAndPassword(email, oldPassword)
                        .addOnCompleteListener(authTask -> {
                            if (authTask.isSuccessful()) {
                                user.updatePassword(newPassword)
                                        .addOnCompleteListener(passwordTask -> {
                                            if (passwordTask.isSuccessful()) {
                                                Toast.makeText(Settings.this, "Password changed successfully", Toast.LENGTH_SHORT).show();
                                            } else {
                                                Toast.makeText(Settings.this, "Password update failed", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            } else {
                                Toast.makeText(Settings.this, "Old password is incorrect", Toast.LENGTH_SHORT).show();
                            }
                        });
            } else {
                Toast.makeText(Settings.this, "Enter valid old and new password", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    private void showDeleteAccountDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Disable Account");
        builder.setMessage("Are you sure you want to permanently disable your account and clear your all data?");

        builder.setPositiveButton("Disable", (dialog, which) -> {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user != null) {
                String uid = user.getUid();

                // Step 1: Remove user data from Realtime Database
                FirebaseDatabase.getInstance().getReference("User").child(uid).removeValue()
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                // Step 2: Delete the FirebaseAuth user
                                user.delete()
                                        .addOnCompleteListener(deleteTask -> {
                                            if (deleteTask.isSuccessful()) {
                                                Toast.makeText(Settings.this, "Account and data deleted", Toast.LENGTH_SHORT).show();
                                                startActivity(new Intent(getApplicationContext(), Login.class));
                                                finish();
                                            } else {
                                                Toast.makeText(Settings.this, "Failed to delete account", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            } else {
                                Toast.makeText(Settings.this, "Failed to delete user data", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
        builder.show();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}