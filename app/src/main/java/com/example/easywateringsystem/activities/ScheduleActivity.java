package com.example.easywateringsystem.activities;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import com.example.easywateringsystem.R;
import com.example.easywateringsystem.fragments.TimePickerFragment;
import com.example.easywateringsystem.models.Zone;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ScheduleActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener, TimePickerDialog.OnTimeSetListener {
    private Bundle mSelectedDay;
    private Bundle mSelectedSysKey;
    private Bundle mSelectedSysName;
    private Bundle mSelectedZoneArray;
    private Bundle mSelectedZonePosition;

    private DatabaseReference ref;
    private Spinner mZoneSpinner;
    private TextView mTV_ScheduleDay;
    private TextView mTV_ZoneNumber;
    private TextView mTV_ScheduleStart;
    private TextView mTV_ScheduleEnd;

    private Button mBT_ScheduleStart;
    private Button mBT_ScheduleEnd;
    private boolean mBoolClickedStart;
    private boolean mBoolClickedEnd;

    private Snackbar snackbar;
    private RelativeLayout rel_lay_schedule;

    ArrayList<Zone> mZones = new ArrayList<>();
    List<String> mScheduleStart;
    List<String> mScheduleFinish;
    List<String> mZoneKey;
    List<String> mZoneName;
    List<String> mSysKey;
    List<String> mSysName;
    List<String> mNumbers;
    List<String> mNames;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);
        mTV_ScheduleDay = findViewById(R.id.schedule_day);
        mTV_ZoneNumber = findViewById(R.id.zone_number);
        mSelectedDay = getIntent().getExtras();
        mSelectedSysKey = getIntent().getExtras();
        mSelectedSysName = getIntent().getExtras();
        mSelectedZonePosition = getIntent().getExtras();

        if (mSelectedDay != null) {
            String value = mSelectedDay.getString(getString(R.string.key_day));
            mTV_ScheduleDay.setText(value);
        }
        String systemKey = mSelectedSysKey.getString(getString(R.string.key_system_key));
        String systemName = mSelectedSysName.getString(getString(R.string.key_system_name));

        rel_lay_schedule = (RelativeLayout) findViewById(R.id.layout_schedule);


        mSysKey = new ArrayList<String>();
        mSysName = new ArrayList<String>();

        mSysKey.add(systemKey);
        mSysName.add(systemName);

        mSelectedZoneArray = getIntent().getExtras();
        ArrayList<String> zoneArray = mSelectedZoneArray.getStringArrayList(getString(R.string.key_zone_key));


        mNumbers = new ArrayList<String>();
        mNames = new ArrayList<String>();

//        mScheduleStart = new ArrayList<String>();

        mZoneSpinner = findViewById(R.id.schedule_drp_dwn);

        mZoneKey = new ArrayList<String>();
        mZoneName = new ArrayList<String>();

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        String userKey = currentUser.getUid();
        // See https://stackoverflow.com/questions/37094631/get-the-pushed-id-for-specific-value-in-firebase-android

        ref = FirebaseDatabase.getInstance()
                .getReference(getString(R.string.firebase_path_Users))
                .child(userKey)
                .child(getString(R.string.firebase_path_Systems))
                .child(systemKey)
                .child(getString(R.string.firebase_child_Zones));

        ref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    // Gets specified value from Firebase DB
                    // See https://stackoverflow.com/questions/46047189/how-to-get-particular-value-using-datasnapshot-in-firebase
                    String refKey = ds.getRef().child(getString(R.string.firebase_child_Zones)).getRef().toString();
                    // Extracts reference key from Zones
                    // See https://stackoverflow.com/questions/16597303/extract-string-between-two-strings-in-java
                    Pattern pattern = Pattern.compile("Zones/(.*?)/zoneId", Pattern.DOTALL);
                    Matcher matcher = pattern.matcher(refKey);
                    while (matcher.find()) {
                        mZoneKey.add(matcher.group(1));
                    }

                    String key = ds.getKey();
                    String number;
                    String name;

                    switch (key) {
                        case "zoneId":
                            number = ds.getValue(String.class);
                            mNumbers.add(number);
                            break;
                        case "zoneName":
                            name = ds.getValue(String.class);
                            mNames.add(name);
                            break;
                    }

                }
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(ScheduleActivity.this, android.R.layout.simple_spinner_item);
                mZoneSpinner.setAdapter(adapter);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                mZoneSpinner.setOnItemSelectedListener(ScheduleActivity.this);

                int zonePosition = mSelectedZonePosition.getInt(getString(R.string.key_zone_position));
                mZoneSpinner.setSelection(zonePosition);

                for (int i = 0; i <= mNames.size() - 1; i++) {
                    adapter.add(mNames.get(i));
                    adapter.notifyDataSetChanged();
                }
//


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
////

        mBT_ScheduleStart = (Button) findViewById(R.id.timepicker_start);
        mBT_ScheduleStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment timePicker = new TimePickerFragment();
                timePicker.show(getSupportFragmentManager(), "START TIME");
                mBoolClickedStart = true;
            }
        });


        mBT_ScheduleEnd = (Button) findViewById(R.id.timepicker_end);
        mBT_ScheduleEnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment timePicker = new TimePickerFragment();
                timePicker.show(getSupportFragmentManager(), "END TIME");
                mBoolClickedEnd = true;
            }
        });

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        mTV_ZoneNumber.setText(mNumbers.get(position));
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }


    public void saveSchedule(View view) {
        String tvStart = getString(R.string.blank_schedule);
        String tvFinish = getString(R.string.blank_schedule);
        if (mScheduleStart != null) {
            tvStart = mScheduleStart.get(0);
        }
        if (mScheduleFinish != null) {
            tvFinish = mScheduleFinish.get(0);
        }

        String sysKey = mSysKey.get(0);
        String sysAddress = mSysName.get(0);
        String zoneName = mZoneSpinner.getSelectedItem().toString();
        String zoneNumber = mNumbers.get(mZoneSpinner.getSelectedItemPosition());
        String zoneKey = mZoneKey.get(mZoneSpinner.getSelectedItemPosition());

        String day = mSelectedDay.getString(getString(R.string.key_day));
//        ArrayList<Day> dayScheduled = new ArrayList<>();

//        dayScheduled.add(new Day(day));

//        Day dayScheduled = new Day(day);

//        final DayAdapter adapter = new DayAdapter(ScheduleActivity.this, dayScheduled);
//        final ListView listView = findViewById(R.id.list);
//        listView.setAdapter(adapter.getItem(1).g.setBackgroundColor(Color.BLUE));

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        String userKey = currentUser.getUid();


        // System key generated to uniquely identify System
        // See https://stackoverflow.com/questions/37094631/get-the-pushed-id-for-specific-value-in-firebase-android

        HashMap hashMap = new HashMap();

        String scheduleKey = FirebaseDatabase.getInstance().getReference()
                .child(getString(R.string.firebase_path_Users))
                .child(userKey)
                .child(getString(R.string.firebase_child_Systems))
                .child(sysKey)
                .child(getString(R.string.firebase_child_Zone))
                .child(zoneKey)
                .child(getString(R.string.firebase_child_Schedules))
                .push()
                .getKey();

        hashMap.put(getString(R.string.hashmap_scheduleKey), scheduleKey);
        hashMap.put(getString(R.string.hashmap_day), day);
        hashMap.put(getString(R.string.hashmap_start), tvStart);
        hashMap.put(getString(R.string.hashmap_finish), tvFinish);

        FirebaseDatabase.getInstance().getReference()
                .child(getString(R.string.firebase_path_Users))
                .child(userKey)
                .child(getString(R.string.firebase_child_Systems))
                .child(sysKey)
                .child(getString(R.string.firebase_child_Zones))
                .child(zoneKey)
                .child(getString(R.string.firebase_child_Schedules))
                .child(scheduleKey)
                .setValue(hashMap);

        // used to back out of an activity
        // See https://stackoverflow.com/questions/15393899/how-to-close-activity-and-go-back-to-previous-activity-in-android
        mSelectedZoneArray = getIntent().getExtras();
        ArrayList<String> zoneArray = mSelectedZoneArray.getStringArrayList(getString(R.string.key_zone_key));

        final Intent intent = new Intent(this, DayActivity.class);
        intent.putStringArrayListExtra(getString(R.string.key_zone_key), (ArrayList<String>) zoneArray);
        intent.putExtra(getString(R.string.key_day), day);
        intent.putExtra(getString(R.string.key_sytem_key), sysKey);
        intent.putExtra(getString(R.string.key_zone_position), mZoneSpinner.getSelectedItemPosition());

        // When saved, Snackbar shows the message and then after finished showing it goes to previous activity.
        // See https://stackoverflow.com/questions/34578375/how-to-wait-to-snackbar-i-want-to-know-when-it-is-closed
        Snackbar.make(view, "Schedule added successfully!", 1200)
                .addCallback(new Snackbar.Callback() {
                    @Override
                    public void onDismissed(Snackbar snackbar, int event) {
                        super.onDismissed(snackbar, event);
                        startActivity(intent);
                    }
                }).show();
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        // Set time to AM/PM
        // See https://stackoverflow.com/questions/2659954/timepickerdialog-and-am-or-pm/2660148#2660148
        if (mBoolClickedStart == true) {
            TextView tvStart = (TextView) findViewById(R.id.schedule_start);
            String am_pm = "";

            Calendar datetime = Calendar.getInstance();
            datetime.set(Calendar.HOUR_OF_DAY, hourOfDay);
            datetime.set(Calendar.MINUTE, minute);

            if (datetime.get(Calendar.AM_PM) == Calendar.AM)
                am_pm = "AM";
            else if (datetime.get(Calendar.AM_PM) == Calendar.PM)
                am_pm = "PM";
            String strHrsToShow = (datetime.get(Calendar.HOUR) == 0) ? "12" : datetime.get(Calendar.HOUR) + "";
// Minutes are formatted to show an extra 0 at the end. For example instead of "12:0" shows "12:00"
            String formattedTime = strHrsToShow.format("%02d", datetime.get(Calendar.MINUTE));
            tvStart.setText(strHrsToShow + ":" + formattedTime + " " + am_pm);
            mScheduleStart = new ArrayList<String>();
            mScheduleStart.add(strHrsToShow + ":" + formattedTime + " " + am_pm);

            mBoolClickedStart = false;
        }
        if (mBoolClickedEnd == true) {
            TextView tvEnd = (TextView) findViewById(R.id.schedule_end);
            String am_pm = "";

            Calendar datetime = Calendar.getInstance();
            datetime.set(Calendar.HOUR_OF_DAY, hourOfDay);
            datetime.set(Calendar.MINUTE, minute);

            if (datetime.get(Calendar.AM_PM) == Calendar.AM)
                am_pm = "AM";
            else if (datetime.get(Calendar.AM_PM) == Calendar.PM)
                am_pm = "PM";
            String strHrsToShow = (datetime.get(Calendar.HOUR) == 0) ? "12" : datetime.get(Calendar.HOUR) + "";
// Minutes are formatted to show an extra 0 at the end. For example instead of "12:0" shows "12:00"
            String formattedTime = strHrsToShow.format("%02d", datetime.get(Calendar.MINUTE));
            tvEnd.setText(strHrsToShow + ":" + formattedTime + " " + am_pm);
            mScheduleFinish = new ArrayList<String>();
            mScheduleFinish.add(strHrsToShow + ":" + formattedTime + " " + am_pm);

            mBoolClickedEnd = false;
        }
    }
}