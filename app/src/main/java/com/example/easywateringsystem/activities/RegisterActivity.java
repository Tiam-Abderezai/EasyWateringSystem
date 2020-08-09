package com.example.easywateringsystem.activities;

import android.content.Context;
import android.content.Intent;
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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    // EditText member variables
    private EditText mEt_mNameFirst;
    private EditText mEt_mNameLast;
    private EditText mEt_mEmail;
    private EditText mEt_mEmailConfirm;
    private EditText mEt_mPassword;
    private EditText mEt_mPasswordConfirm;

    // Misc member variables
    private Snackbar snackbar;
    private RelativeLayout rel_lay_reg;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        // Initialize member variables
        mAuth = FirebaseAuth.getInstance();

        mEt_mNameFirst = (EditText) findViewById(R.id.activity_registration_et_name_first);
        mEt_mNameLast = (EditText) findViewById(R.id.activity_registration_et_name_last);
        mEt_mEmail = (EditText) findViewById(R.id.activity_registration_et_email);
        mEt_mEmailConfirm = (EditText) findViewById(R.id.activity_registration_et_email_confirm);
        mEt_mPassword = (EditText) findViewById(R.id.activity_registration_et_password);
        mEt_mPasswordConfirm = (EditText) findViewById(R.id.activity_registration_et_password_confirm);

        rel_lay_reg = (RelativeLayout) findViewById(R.id.layout_registration);


    }

    // Creates a new account for user
    public void createAccount(View view) {
        // Variables email and password used for creating new account for user
        String email = mEt_mEmail.getText().toString();
        String email_confirm = mEt_mEmailConfirm.getText().toString();
        String password = mEt_mPassword.getText().toString();
        String password_confirm = mEt_mPasswordConfirm.getText().toString();

        // Verify email and password match with email_confirm and password_confirm
        if (email.equals(email_confirm) && password.equals(password_confirm)) {
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            // Variables created to be stored in DB upon signing-up
                            String nameFirst = mEt_mNameFirst.getText().toString();
                            String nameLast = mEt_mNameLast.getText().toString();
                            String email = mEt_mEmail.getText().toString();
                            String password = mEt_mPassword.getText().toString();
//                            Systems systems = new Systems(
//                                    "address"
//                            );
                            // Store user data in real time DB
                            if (task.isSuccessful()) {
                                User.test();
                                final User user = new User(
                                        nameFirst,
                                        nameLast,
                                        email,
                                        password,
                                        email
                                );


                                // Add user info to the Realtime DB

                                FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                                String userKey = currentUser.getUid();


                                // System key generated to uniquely identify System
                                // See https://stackoverflow.com/questions/37094631/get-the-pushed-id-for-specific-value-in-firebase-android

                                HashMap hashMap = new HashMap();

                                hashMap.put(getString(R.string.hashmap_email), email);
                                hashMap.put(getString(R.string.hashmap_nameFirst), nameFirst);
                                hashMap.put(getString(R.string.hashmap_nameLast), nameLast);
                                hashMap.put(getString(R.string.hashmap_image), "");

                                FirebaseDatabase.getInstance().getReference()
                                        .child(getString(R.string.firebase_path_Users))
                                        .child(userKey)
                                        .child(getString(R.string.firebase_child_userData))
                                        .setValue(hashMap);

                                mAuth.signOut();
                                // Hides the keyboard when create account button is clicked
                                InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                                mgr.hideSoftInputFromWindow(mEt_mPassword.getWindowToken(), 0);
                                // Display to user that account was created when create account button is clicked
                                // When saved, Snackbar shows the message and then after finished showing it goes to previous activity.
                                // See https://stackoverflow.com/questions/34578375/how-to-wait-to-snackbar-i-want-to-know-when-it-is-closed
                                Snackbar.make(view, "Account created! Try logging in.", 1200)
                                        .addCallback(new Snackbar.Callback() {
                                            @Override
                                            public void onDismissed(Snackbar snackbar, int event) {
                                                super.onDismissed(snackbar, event);
                                                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                                                startActivity(intent);
                                            }
                                        }).show();

//                                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
//                                startActivity(intent);
                                // Hides the keyboard when create account button is clicked
                                InputMethodManager mgrCreated = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                                mgrCreated.hideSoftInputFromWindow(mEt_mPassword.getWindowToken(), 0);
                                // Display to user that account was created when create account button is clicked

                                // Sign in success, update UI with the signed-in user's information
//                            FirebaseUser user = mAuth.getCurrentUser();
//                            updateUI(user);
                            } else {
                                // If sign in fails, display a message to the user.
                                // Hides the keyboard when create account button is clicked
                                InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                                mgr.hideSoftInputFromWindow(mEt_mPassword.getWindowToken(), 0);
                                // Display to user that account was created when create account button is clicked
                                snackbar = Snackbar.make(rel_lay_reg, "Account already exists.", Snackbar.LENGTH_LONG);
                                snackbar.show();

//                            updateUI(null);
                            }

                        }

                    });

        } else {
            // Hides the keyboard when email/password don't match their confirms
            InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            mgr.hideSoftInputFromWindow(mEt_mPassword.getWindowToken(), 0);
            // Display to user that account was created when create account button is clicked
            snackbar = Snackbar.make(rel_lay_reg, "Mismatch occurred for either email or password. Try again.", Snackbar.LENGTH_LONG);
            snackbar.show();
        }
    }

}