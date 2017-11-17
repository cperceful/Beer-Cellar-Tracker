package com.cperceful.beercellartracker;

import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {

    private AutoCompleteTextView emailText;
    private EditText usernameText;
    private EditText passwordText;
    private EditText confirmPasswordText;
    private Button registerButton;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference firebaseDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        emailText = findViewById(R.id.autoCompleteText_email_register);
        usernameText = findViewById(R.id.editText_username);
        passwordText = findViewById(R.id.editText_password_register);
        confirmPasswordText = findViewById(R.id.editText_confirmPassword_register);
        registerButton = findViewById(R.id.button_register_register);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance().getReference();

        confirmPasswordText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId == R.id.register || actionId == EditorInfo.IME_NULL){
                    attemptRegistration();
                    return true;
                }
                return false;
            }
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptRegistration();
            }
        });


    }

    private void attemptRegistration(){
        //TODO: do
        emailText.setError(null);
        passwordText.setError(null);

        String email = emailText.getText().toString();
        String password = passwordText.getText().toString();

        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isEmpty(password) || !isPasswordValid(password)){
            cancel = true;
            focusView = passwordText;
            passwordText.setError(getString(R.string.password_invalid_error));
        }

        if (TextUtils.isEmpty(email)){
            cancel = true;
            focusView = emailText;
            emailText.setError(getString(R.string.email_empty_error));
        } else if (!isEmailValid(email)){
            cancel = true;
            focusView = emailText;
            emailText.setError(getString(R.string.email_invalid_error));
        }

        if (cancel){
            focusView.requestFocus();
        } else {
            createUser();
        }


    }

    private boolean isEmailValid(String email){
        EmailValidator validator = new EmailValidator();
        return validator.validateEmail(email);
    }

    private boolean isPasswordValid(String password){
        return password.length() > 5 && password.equals(confirmPasswordText.getText().toString());
    }

    private void createUser(){
        final String email = emailText.getText().toString();
        String password = passwordText.getText().toString();
        final String username = usernameText.getText().toString();

        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                Log.d(LoginActivity.DEBUG_TAG, "Registration success: " + task.isSuccessful());

                if (!task.isSuccessful()){
                    Log.d(LoginActivity.DEBUG_TAG, "Registration failed");
                    generateErrorMessage("Apparently user creation failed. My bad");
                } else {
                    User user = new User(email, username);
                    String userId = task.getResult().getUser().getUid();
                    firebaseDatabase.child("users").child(userId).setValue(user);
                }
            }
        });
    }

    private void generateErrorMessage(String message){
        new AlertDialog.Builder(this)
                .setTitle("oh dear")
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }
}
