package com.example.easywateringsystem.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.easywateringsystem.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.ArrayList;
import java.util.List;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private DatabaseReference ref;

    // EditText member variables
    private EditText mEt_mEmail;
    private EditText mEt_mPassword;

    // Misc member variables
    private Snackbar snackbar;
    private RelativeLayout rel_lay_login;
    private ImageView mIv_Logo;
    List<String> mSystemKey;
    List<String> mSystemAddress;
private boolean openNewSys;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        // Initialize member variables
        mAuth = FirebaseAuth.getInstance();
        mIv_Logo = findViewById(R.id.login_logo);
        mEt_mEmail = (EditText) findViewById(R.id.activity_login_et_email);
        mEt_mPassword = (EditText) findViewById(R.id.activity_login_et_password);
        rel_lay_login = (RelativeLayout) findViewById(R.id.layout_login);
        mSystemKey = new ArrayList<String>();
        mSystemAddress = new ArrayList<String>();

        // Checks if user is already logged in. If logged in, then loads Homepage
        // Otherwise, loads launcher which then loads login page.
        if (mAuth.getCurrentUser()!=null){
            // System key generated to uniquely identify System
            // See https://stackoverflow.com/questions/37094631/get-the-pushed-id-for-specific-value-in-firebase-android
            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
            String userKey = currentUser.getUid();
            final Intent intentHomePage = new Intent(this, HomepageActivity.class);

            ref = FirebaseDatabase.getInstance()
                    .getReference(getString(R.string.firebase_path_Users))
                    .child(userKey)
                    .child(getString(R.string.firebase_child_Systems));

            Query sysByOrder = ref.orderByKey();
            sysByOrder.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                    for (DataSnapshot ds : dataSnapshot.getChildren()) {

                        String key = ds.getKey();
                        String sysKey;
                        String sysAddress;

                        switch (key) {
                            case "sysKey: ":
                                sysKey = ds.getValue(String.class);
                                mSystemKey.add(sysKey);
                                break;
                        }


                    }

                    if (mSystemKey.size()!=0) {
                        startActivity(intentHomePage);
                    }
                    else {
                        openNewSys = true;
                    }
                }

                @Override
                public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                }

                @Override
                public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                }

                @Override
                public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
              String size = mSystemKey.toString();

            openNewSys = true;


        }


        if (mSystemKey.size()==0 && openNewSys) {
            String email = mEt_mEmail.getText().toString();
            String password = mEt_mPassword.getText().toString();
            final Intent intentNewSystem = new Intent(this, LinkNewSysActivity.class);

            intentNewSystem.putExtra(getString(R.string.key_user_key), email + " " + password);

            startActivity(intentNewSystem);
            // Sign in success, update UI with the signed-in user's information
//                            Log.d(TAG, "signInWithEmail:success");
//                            FirebaseUser user = mAuth.getCurrentUser();
//                            updateUI(user);
        }
//
    }

    public void clickLogin(final View view) {
        // Variables email and password used for creating new account for user
        String email = mEt_mEmail.getText().toString();
        String password = mEt_mPassword.getText().toString();
        final Intent intentNewSystem = new Intent(this, LinkNewSysActivity.class);
        final Intent intentHomePage = new Intent(this, HomepageActivity.class);
        intentNewSystem.putExtra(getString(R.string.key_user_key), email + " " + password);
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) {
// Hides the keyboard when create account button is clicked
                            InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                            mgr.hideSoftInputFromWindow(mEt_mPassword.getWindowToken(), 0);
                            // Display to user that account was created when create account button is clicked
                            snackbar = Snackbar.make(rel_lay_login, "Logged in successfully.", Snackbar.LENGTH_LONG);
                            snackbar.show();

                            // System key generated to uniquely identify System
                            // See https://stackoverflow.com/questions/37094631/get-the-pushed-id-for-specific-value-in-firebase-android
                            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                            String userKey = currentUser.getUid();


                            ref = FirebaseDatabase.getInstance()
                                    .getReference(getString(R.string.firebase_path_Users))
                                    .child(userKey)
                                    .child(getString(R.string.firebase_child_Systems));

                            Query sysByOrder = ref.orderByKey();
                            sysByOrder.addChildEventListener(new ChildEventListener() {
                                @Override
                                public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                                    for (DataSnapshot ds : dataSnapshot.getChildren()) {

                                        String key = ds.getKey();
                                        String sysKey;
                                        String sysAddress;

                                        switch (key) {
                                            case "sysKey: ":
                                                sysKey = ds.getValue(String.class);
                                                mSystemKey.add(sysKey);
                                                break;
                                        }


                                    }
                                    if (mSystemKey.size()!=0) {
                                        startActivity(intentHomePage);
                                    }

                                }

                                @Override
                                public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                                }

                                @Override
                                public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                                }

                                @Override
                                public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                            if (mSystemKey.size()==0) {
                                String email = mEt_mEmail.getText().toString();
                                String password = mEt_mPassword.getText().toString();

                                intentNewSystem.putExtra(getString(R.string.key_user_key), email + " " + password);

                                startActivity(intentNewSystem);
                                // Sign in success, update UI with the signed-in user's information
//                            Log.d(TAG, "signInWithEmail:success");
//                            FirebaseUser user = mAuth.getCurrentUser();
//                            updateUI(user);
                            }
                            String size = mSystemKey.toString();

//                            if (size.equals("[]")) {
//                                startActivity(intentNewSystem);
//                                // Sign in success, update UI with the signed-in user's information
////                            Log.d(TAG, "signInWithEmail:success");
////                            FirebaseUser user = mAuth.getCurrentUser();
////                            updateUI(user);
//                            }
//                            if (!size.equals("[]")) {
//                                startActivity(intentHomePage);
//                            }
                        } else {
                            // If sign in fails, display a message to the user.
                            InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                            mgr.hideSoftInputFromWindow(mEt_mPassword.getWindowToken(), 0);
                            // Display to user that account was created when create account button is clicked
                            snackbar = Snackbar.make(rel_lay_login, "Logged FAILED. Please try again.", Snackbar.LENGTH_LONG);
                            snackbar.show();
//                            Log.w(TAG, "signInWithEmail:failure", task.getException());
//                            Toast.makeText(EmailPasswordActivity.this, "Authentication failed.",
//                                    Toast.LENGTH_SHORT).show();
//                            updateUI(null);
                        }

                        // ...
                    }
                });
    }

    // If user doesn't have a system already AND user is
    // registered already, then loads "AddNewSysActivity"
    public void loadAddNewSysActivity(View view) {
        Intent intent = new Intent(this, PassResetActivity.class);
        startActivity(intent);
    }

    // Loads "RegisterActivity" when clicked
    public void loadRegisterActivity(View view) {
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
    }

    // Loads "RecoverActivity" when clicked
    public void loadRecoverActivity(View view) {
        Intent intent = new Intent(this, PassResetActivity.class);
        startActivity(intent);
    }
}

