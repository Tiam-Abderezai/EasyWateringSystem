package com.example.easywateringsystem.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.easywateringsystem.R;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class SystemActivity extends AppCompatActivity {
    private Bundle mSelectedSysKey;
    private Bundle mSelectedSysName;

    private DatabaseReference ref;

    private TextView mTV_SystemName;
    List<String> mSysKey;
    List<String> mSysName;
    List<String> mNumbers;
    List<String> mNames;
    private Snackbar snackbar;
    private RelativeLayout rel_lay_system;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_system);
        mSelectedSysKey = getIntent().getExtras();
        mSelectedSysName = getIntent().getExtras();
        if (mSelectedSysName != null) {
            String key = mSelectedSysKey.getString(getString(R.string.key_system_key));
            String name = mSelectedSysName.getString(getString(R.string.key_system_name));
            mTV_SystemName = findViewById(R.id.title_system);
            mTV_SystemName.setText(name);

        }
        String systemKey = mSelectedSysKey.getString(getString(R.string.key_system_key));
        String systemName = mSelectedSysName.getString(getString(R.string.key_system_name));

        mSysKey = new ArrayList<String>();
        mSysName = new ArrayList<String>();

        mSysKey.add(systemKey);
        mSysName.add(systemName);

        rel_lay_system = (RelativeLayout) findViewById(R.id.layout_system);

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        String userKey = currentUser.getUid();
        // See https://stackoverflow.com/questions/37094631/get-the-pushed-id-for-specific-value-in-firebase-android

    }


    public void saveSystemEdit(View view) {
        String sysKey = mSysKey.get(0);
        Intent intent = new Intent(this, SystemEditActivity.class);
        intent.putExtra(getString(R.string.key_sytem_key), sysKey);
        startActivity(intent);

    }

    public void deleteSystem(View view) {
        mSelectedSysKey = getIntent().getExtras();
        mSelectedSysName = getIntent().getExtras();

        String sysKey = mSelectedSysKey.getString(getString(R.string.key_system_key));
        String sysName = mSelectedSysName.getString(getString(R.string.key_system_name));

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        String userKey = currentUser.getUid();
        // See https://stackoverflow.com/questions/37094631/get-the-pushed-id-for-specific-value-in-firebase-android
        ref = FirebaseDatabase.getInstance()
                .getReference(getString(R.string.firebase_path_Users))
                .child(userKey)
                .child(getString(R.string.firebase_child_Systems))
                .child(sysKey);



        ref.removeValue();

        final Intent intent = new Intent(this, HomepageActivity.class);

        // When saved, Snackbar shows the message and then after finished showing it goes to previous activity.
        // See https://stackoverflow.com/questions/34578375/how-to-wait-to-snackbar-i-want-to-know-when-it-is-closed
        Snackbar.make(view, "System deleted!", 1200)
                .addCallback(new Snackbar.Callback() {
                    @Override
                    public void onDismissed(Snackbar snackbar, int event) {
                        super.onDismissed(snackbar, event);
                        startActivity(intent);
                    }
                }).show();
    }


}