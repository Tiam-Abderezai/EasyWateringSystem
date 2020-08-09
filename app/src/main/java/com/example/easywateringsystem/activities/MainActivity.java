package com.example.easywateringsystem.activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseUser firebaseUser;
    private DatabaseReference ref;
    List<String> mSystemKey;

    private String mCurrentUserId;
    //    private FirebaseUserMetadata metadata;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSystemKey = new ArrayList<String>();

//        setContentView(R.layout.activity_main);
//        loadLauncherPage();
//        loadLoginPage();
//        loadAddNewSysPage();
//        enterAddress();
//        homePage();
//        crudSystem();
//        crudZone();
//        zoneActivity();
//        scheduleActivity();
//        logOutUser();

            loadLauncherPage();

    }

    void loadLoginPage() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    void loadLauncherPage() {
        Intent intent = new Intent(this, LauncherActivity.class);
        startActivity(intent);
    }

    void loadAddNewSysPage() {
        Intent intent = new Intent(this, LinkNewSysActivity.class);
        startActivity(intent);
    }

    public void enterAddress() {
        Intent intent = new Intent(this, MapActivity.class);
        startActivity(intent);
    }

    public void loadHomePage() {
        Intent intent = new Intent(this, HomepageActivity.class);
        startActivity(intent);
    }


    public void crudZone() {
        Intent intent = new Intent(this, ZoneCRUDActivity.class);
        startActivity(intent);
    }


    public void zoneActivity() {
        Intent intent = new Intent(this, ZoneActivity.class);
        startActivity(intent);
    }

    public void scheduleActivity() {
        Intent intent = new Intent(this, ScheduleActivity.class);
        startActivity(intent);
    }


}
