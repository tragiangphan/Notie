package com.example.notesapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SignupLoginActivity extends AppCompatActivity {
    TextInputLayout usernameEditText, passwordEditText;
    Button loginButton;
    ProgressBar loadingProgressBar;
    DatabaseReference userDatabase;
    ActionBar actionBar;
    CheckBox chkRemember;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_login);
        setUpDatabse();
        setControl();
        setEvent();
    }

    private void setEvent() {
        login();
        checkAlreadyLogIn();
        hideActionBar();
    }

    private void hideActionBar() {
        View decorView = getWindow().getDecorView();
        // Hide the status bar.
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);

        actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.hide();
    }

    private void checkAlreadyLogIn() {
        if (StaticUtilities.getUsername(SignupLoginActivity.this) != null && chkRemember.isChecked()) {
            startActivity(new Intent(SignupLoginActivity.this, MainActivity.class));
            finish();
        }
        if (StaticUtilities.getUsername(SignupLoginActivity.this) != null) {
            startActivity(new Intent(SignupLoginActivity.this, MainActivity.class));
        }
    }

    private void login() {
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (usernameEditText.getEditText().getText().toString().isEmpty()) {
                    usernameEditText.setError("Email is empty!");
                    usernameEditText.requestFocus();
                    return;
                }
                if (passwordEditText.getEditText().getText().toString().isEmpty()) {
                    passwordEditText.setError("Password is empty!");
                    passwordEditText.requestFocus();
                    return;
                }
                loadingProgressBar.setVisibility(View.VISIBLE);
                ProcessLogin(usernameEditText.getEditText().getText().toString(), passwordEditText.getEditText().getText().toString());
            }
        });
    }

    private void setUpDatabse() {
        userDatabase = FirebaseDatabase.getInstance().getReference("users");
    }

    private void setControl() {
        usernameEditText = findViewById(R.id.username);
        passwordEditText = findViewById(R.id.password);
        loginButton = findViewById(R.id.btnLogin);
        loadingProgressBar = findViewById(R.id.loading);
        chkRemember = findViewById(R.id.chkRemember);
    }

    private void ProcessLogin(String username, String password) {
        userDatabase.child(username).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String username1 = snapshot.child("username").getValue(String.class);
                    String password1 = snapshot.child("password").getValue(String.class);
                    String created = snapshot.child("createTime").getValue(String.class);
                    UserModel userModel = snapshot.getValue(UserModel.class);
                    if (password1.equalsIgnoreCase(password)) {
                        loadingProgressBar.setVisibility(View.GONE);
                        StaticUtilities.StoreLoggedUsername(SignupLoginActivity.this, username);
                        startActivity(new Intent(SignupLoginActivity.this, MainActivity.class));
                        finish();
                    } else {
                        loadingProgressBar.setVisibility(View.GONE);
                        Toast.makeText(SignupLoginActivity.this, "Invalid Username or Password!", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    CreateNewUser(username, password);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void CreateNewUser(String username, String password) {
        String id = userDatabase.push().getKey();

        UserModel userModel = new UserModel(id, username, password);
        userDatabase.child(username).setValue(userModel);
        StaticUtilities.StoreLoggedUsername(SignupLoginActivity.this, username);
        startActivity(new Intent(SignupLoginActivity.this, MainActivity.class));
        finish();
    }
}