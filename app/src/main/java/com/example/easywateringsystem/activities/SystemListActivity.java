package com.example.easywateringsystem.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.easywateringsystem.R;
import com.example.easywateringsystem.adapters.SystemAdapter;
import com.example.easywateringsystem.models.Systems;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class SystemListActivity extends AppCompatActivity {

    private DatabaseReference ref;
    List<String> mSystemKey;
    List<String> mSystemAddress;
    ArrayList<Systems> mSystems = new ArrayList<Systems>();
    List<String> mZoneKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_system);

        mZoneKey = new ArrayList<String>();
        mSystemKey = new ArrayList<String>();
        mSystemAddress = new ArrayList<String>();

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        String userKey = currentUser.getUid();

        // System key generated to uniquely identify System
        // See https://stackoverflow.com/questions/37094631/get-the-pushed-id-for-specific-value-in-firebase-android

        ref = FirebaseDatabase.getInstance()
                .getReference(getString(R.string.firebase_path_Users))
                .child(userKey)
                .child(getString(R.string.firebase_child_Systems));

        ref.addChildEventListener(new ChildEventListener() {
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
                        case "sysAddress: ":
                            sysAddress = ds.getValue(String.class);
                            mSystemAddress.add(sysAddress);
                            break;
                    }
                }
                mSystems.add(new Systems(mSystemAddress.get(mSystemAddress.size() - 1)));
                final SystemAdapter adapter = new SystemAdapter(SystemListActivity.this, mSystems);
                ListView sysList = findViewById(R.id.list);
                sysList.setAdapter(adapter);
                sysList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        if (position == adapter.getItemId(position)) {
                            Intent intent = new Intent(SystemListActivity.this, SystemActivity.class);
                            intent.putExtra(getString(R.string.key_system_key), mSystemKey.get(position));
                            intent.putExtra(getString(R.string.key_system_name), mSystemAddress.get(position));
                            startActivityForResult(intent, position);
                        }
                    }
                });
            }

        @Override
        public void onChildChanged (@NonNull DataSnapshot dataSnapshot, @Nullable String s){

        }

        @Override
        public void onChildRemoved (@NonNull DataSnapshot dataSnapshot){

        }

        @Override
        public void onChildMoved (@NonNull DataSnapshot dataSnapshot, @Nullable String s){

        }

        @Override
        public void onCancelled (@NonNull DatabaseError databaseError){

        }
    });


}


    public void addNew(View view) {
        Intent intent = new Intent(this, MapActivity.class);
        startActivity(intent);
    }
}
