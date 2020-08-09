package com.example.easywateringsystem.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.easywateringsystem.R;
import com.example.easywateringsystem.activities.DayActivity;
import com.example.easywateringsystem.adapters.DayAdapter;
import com.example.easywateringsystem.models.Day;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


public class DayFragment extends Fragment {
    private DatabaseReference ref;
    List<String> mZoneKey;
    List<String> mZoneNumber;
    List<String> mZoneName;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.item_list, container, false);
        ArrayList<Day> days = new ArrayList<>();
        days.add(new Day(getString(R.string.day_MON)));
        days.add(new Day(getString(R.string.day_TUE)));
        days.add(new Day(getString(R.string.day_WED)));
        days.add(new Day(getString(R.string.day_THU)));
        days.add(new Day(getString(R.string.day_FRI)));
        days.add(new Day(getString(R.string.day_SAT)));
        days.add(new Day(getString(R.string.day_SUN)));

        final DayAdapter adapter = new DayAdapter(getActivity(), days);
        final ListView listView = rootView.findViewById(R.id.list);
        listView.setAdapter(adapter);

        mZoneKey = new ArrayList<String>();
        mZoneNumber = new ArrayList<String>();
        mZoneName = new ArrayList<String>();

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        final String userKey = currentUser.getUid();
        // See https://stackoverflow.com/questions/37094631/get-the-pushed-id-for-specific-value-in-firebase-android
        String systemKey = getArguments().getString(getString(R.string.key_system_key));
//        String systemAddress = getArguments().getString("KEY_SYSTEM_NAME");
        final Serializable zoneKey = getArguments().getSerializable(getString(R.string.key_zone_key));

        ref = FirebaseDatabase.getInstance()
                .getReference(getString(R.string.firebase_path_Users))
                .child(userKey)
                .child(getString(R.string.firebase_child_Systems))
                .child(systemKey)
                .child(getString(R.string.firebase_child_Zones));

        ref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    // Gets specified value from Firebase DB
                    // See https://stackoverflow.com/questions/46047189/how-to-get-particular-value-using-datasnapshot-in-firebase
                    String keyNumber = ds.getKey();
                    String zoneKey;
                    String number;
                    String name;
                    switch (keyNumber) {
                        case "zoneKey":
                            zoneKey = ds.getValue(String.class);
                            mZoneKey.add(zoneKey);
                            break;
                        case "zoneId":
                            number = ds.getValue(String.class);
                            mZoneNumber.add(number);
                            break;
                        case "zoneName":
                            name = ds.getValue(String.class);
                            mZoneName.add(name);
                            break;
                    }
                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                            String systemKey = getArguments().getString(getString(R.string.key_system_key));
                            String systemAddress = getArguments().getString(getString(R.string.key_system_name));
                            Serializable zoneKey = getArguments().getSerializable(getString(R.string.key_zone_key));

                            Intent intent = new Intent(getActivity(), DayActivity.class);
                            String day = adapter.getItem(position).getDay();
                            intent.putExtra(getString(R.string.key_day), day);
                            intent.putExtra(getString(R.string.key_system_key), systemKey);
                            intent.putExtra(getString(R.string.key_system_name), systemAddress);
                            intent.putStringArrayListExtra(getString(R.string.key_zone_key), (ArrayList<String>) zoneKey);

                            startActivity(intent);

                        }
                    });

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


        return rootView;
    }


}
