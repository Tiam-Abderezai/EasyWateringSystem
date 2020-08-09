package com.example.easywateringsystem.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.easywateringsystem.R;
import com.example.easywateringsystem.activities.ZoneActivity;
import com.example.easywateringsystem.adapters.ZoneAdapter;
import com.example.easywateringsystem.models.Zone;
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

public class ZoneFragment extends Fragment {
    private DatabaseReference ref;
    ArrayList<Zone> mZones = new ArrayList<>();
    List<String> mKeys;
    List<String> mValues;
    List<String> mZoneKey;
    List<String> mImages;
    List<String> mNumbers;
    List<String> mNames;
    List<Integer> mPositions;
    List<String> mCount;

    private Bundle mSelectedZoneKey;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.item_list, container, false);
        final String systemKey = getArguments().getString(getString(R.string.key_system_key));
        final String systemAddress = getArguments().getString(getString(R.string.key_system_name));
        final Serializable zoneKey = getArguments().getSerializable(getString(R.string.key_zone_key));
//        ArrayList<String> mZoneKey;
        final ArrayList<String> mZoneKey = (ArrayList<String>) getArguments().getSerializable(getString(R.string.key_zone_key));

//        final ArrayList<String> mZoneKey = new ArrayList<String>(zoneKey);

//        mZoneKey = new ArrayList<String>();
        mNumbers = new ArrayList<String>();
        mImages = new ArrayList<String>();
        mNames = new ArrayList<String>();
        mPositions = new ArrayList<Integer>();
        mKeys = new ArrayList<String>();
        mValues = new ArrayList<String>();
        mCount = new ArrayList<String>();

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        final String userKey = currentUser.getUid();
        // See https://stackoverflow.com/questions/37094631/get-the-pushed-id-for-specific-value-in-firebase-android

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
                    String image;
                    String zoneKey;
                    String number;
                    String name;
                    int position;
                    ImageView imageView = null;
                    switch (keyNumber) {
                        case "zoneKey":
                            zoneKey = ds.getValue(String.class);
                            mZoneKey.add(zoneKey);
                            break;
                        case "zoneImage":
                            image = ds.getValue(String.class);
                            mImages.add(image);
                            break;
                        case "zoneId":
                            number = ds.getValue(String.class);
                            mNumbers.add(number);
                            break;
                        case "zoneName":
                            name = ds.getValue(String.class);
                            mNames.add(name);
                            break;
                        case "zonePos":
                            position = ds.getValue(Integer.class);
                            mPositions.add(position);
                            break;
                    }

                }
                mZones.add(new Zone(mNumbers.get(mNumbers.size() - 1), mNames.get(mNames.size() - 1), mImages.get(mImages.size() - 1)));
//                mZones.add(new Zone(mImages.get(mImages.size() - 1)));
                final ZoneAdapter adapter = new ZoneAdapter(getActivity(), mZones);
                ListView listView = rootView.findViewById(R.id.list);
                listView.setAdapter(adapter);

                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        if (position == adapter.getItemId(position)) {
//                            int selectedPosition = position;
                            Bundle bundleZoneKey = new Bundle();
                            Intent intent = new Intent(getActivity(), ZoneActivity.class);
                            intent.putExtra(getString(R.string.key_system_key), systemKey);
                            intent.putExtra(getString(R.string.key_system_name), systemAddress);
                            intent.putExtra(getString(R.string.key_zone_id), mNumbers.get(position));
                            intent.putExtra(getString(R.string.key_zone_image), mImages.get(position));
                            intent.putExtra(getString(R.string.key_zone_name), mNames.get(position));
                            intent.putExtra(getString(R.string.key_zone_key), mZoneKey.get(position));
                            intent.putExtra(getString(R.string.key_zone_position), mPositions.get(position));
                            startActivityForResult(intent, position);
                        }
                    }
                });
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

        mZoneKey.clear();
        return rootView;

    }


}