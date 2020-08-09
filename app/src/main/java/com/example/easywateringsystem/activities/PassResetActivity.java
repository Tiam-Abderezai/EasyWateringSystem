package com.example.easywateringsystem.activities;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.easywateringsystem.R;
import com.example.easywateringsystem.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class PassResetActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;

    // EditText member variables
    private EditText mEt_mEmail;


    // Misc member variables
    private Snackbar snackbar;
    private RelativeLayout rel_lay_reset;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pass_reset);

        // Initialize member variables
        mAuth = FirebaseAuth.getInstance();
        mEt_mEmail = (EditText) findViewById(R.id.activity_reset_et_email);
        rel_lay_reset = (RelativeLayout) findViewById(R.id.layout_reset);

    }

    // Creates a new account for user
    public void sendEmail(View view) {
        // Variables email and password used for creating new account for user
        String email = mEt_mEmail.getText().toString();

        // Verify email and password match with email_confirm and password_confirm
        mAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            // Hides the keyboard when create account button is clicked
                            InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                            mgr.hideSoftInputFromWindow(mEt_mEmail.getWindowToken(), 0);
                            // Display to user that account was created when create account button is clicked
                            snackbar = Snackbar.make(rel_lay_reset, "Email was sent.", Snackbar.LENGTH_LONG);
                            snackbar.show();
                        }
                    }
                });

    }
}