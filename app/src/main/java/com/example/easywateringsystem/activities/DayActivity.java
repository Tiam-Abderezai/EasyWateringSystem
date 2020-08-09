package com.example.easywateringsystem.activities;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.easywateringsystem.R;
import com.example.easywateringsystem.adapters.ScheduleAdapter;
import com.example.easywateringsystem.models.Schedule;
import com.example.easywateringsystem.utils.DayWidgetProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DayActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private Bundle mDaySelected;
    private TextView mTV_DayTitle;
    private Bundle mSelectedSysKey;
    private Bundle mSelectedSysName;
    private Bundle mSelectedZoneKey;
    private Bundle mSelectedZonePosition;

    private DatabaseReference ref;
    private Spinner mZoneSpinner;
    private SharedPreferences sharedPreferences;
    public static final String PREFERENCES_ID = "ID";
    public static final String PREFERENCES_WIDGET_TITLE = "WIDGET_TITLE";
    public static final String PREFERENCES_WIDGET_CONTENT = "WIDGET_CONTENT";


    ArrayList<Schedule> mSchedules = new ArrayList<Schedule>();
    List<String> mDay;
    List<String> mScheduleStart;
    List<String> mScheduleFinish;
    List<String> mZoneKey;
    List<String> mZoneName;
    //    List<String> mSysKey;
    List<String> mSysName;
    List<String> mNumbers;
    List<String> mNames;
    List<String> mScheduleKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_day);
        mTV_DayTitle = findViewById(R.id.title_day);
        mDaySelected = getIntent().getExtras();
        mSelectedSysKey = getIntent().getExtras();
        mSelectedSysName = getIntent().getExtras();
        mSelectedZoneKey = getIntent().getExtras();
        mSelectedZonePosition = getIntent().getExtras();

        mNumbers = new ArrayList<String>();
        mNames = new ArrayList<String>();

        final String daySelected = mDaySelected.getString(getString(R.string.key_day));
        final String key = mSelectedSysKey.getString(getString(R.string.key_system_key));
        String name = mSelectedSysName.getString(getString(R.string.key_system_name));
        final ArrayList<String> zoneKey = mSelectedZoneKey.getStringArrayList(getString(R.string.key_zone_key));
        mDay = new ArrayList<String>();
        mScheduleStart = new ArrayList<String>();
        mScheduleFinish = new ArrayList<String>();
        mZoneKey = new ArrayList<String>();
        mScheduleKey = new ArrayList<String>();
        mTV_DayTitle.setText(daySelected);


        String systemKey = mSelectedSysKey.getString(getString(R.string.key_system_key));


        mZoneSpinner = findViewById(R.id.zone_drp_dwn_crud);

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        String userKey = currentUser.getUid();
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
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(DayActivity.this, android.R.layout.simple_spinner_item);
                mZoneSpinner.setAdapter(adapter);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                mZoneSpinner.setOnItemSelectedListener(DayActivity.this);
                // mSelectedZonePosition is received from ScheduleActivity when a new schedule is added and
                // the spinner in DayActivity will load/show the zone whose schedule was just added.
                // If none were added, (if mSelectedZonePosition != null) it will skip it so app doesn't crash.
                // Then it will only load the first zone's spinner value.
                if (mSelectedZonePosition != null) {
                    int zonePosition = mSelectedZonePosition.getInt("KEY_ZONE_POSITION");
                    mZoneSpinner.setSelection(zonePosition);
                }
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
        if (zoneKey != null) {
            zoneKey.clear();
        }

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, final int position, long id) {
        mDaySelected = getIntent().getExtras();
        mSelectedSysKey = getIntent().getExtras();
        mSelectedSysName = getIntent().getExtras();
        mSelectedZoneKey = getIntent().getExtras();

        mScheduleStart.clear();
        mScheduleFinish.clear();
        mSchedules.clear();

        final String daySelected = mDaySelected.getString(getString(R.string.key_day));
        final String key = mSelectedSysKey.getString(getString(R.string.key_system_key));
        String name = mSelectedSysName.getString(getString(R.string.key_system_name));
        final ArrayList<String> zoneKey = mSelectedZoneKey.getStringArrayList(getString(R.string.key_zone_key));
        final int zonePos = mZoneSpinner.getSelectedItemPosition();

        mDay = new ArrayList<String>();
        mScheduleStart = new ArrayList<String>();
        mScheduleFinish = new ArrayList<String>();
        mZoneKey = new ArrayList<String>();
        mScheduleKey = new ArrayList<String>();
        mTV_DayTitle.setText(daySelected);


        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        String userKey = currentUser.getUid();
//         See https://stackoverflow.com/questions/37094631/get-the-pushed-id-for-specific-value-in-firebase-android
        ref = FirebaseDatabase.getInstance()
                .getReference(getString(R.string.firebase_path_Users))
                .child(userKey)
                .child(getString(R.string.firebase_child_Systems))
                .child(key)
                .child(getString(R.string.firebase_child_Zones))
                .child(zoneKey.get(position))
                .child(getString(R.string.firebase_child_Schedules));

        mZoneKey.add(zoneKey.get(position));


        // The 5 lines of code below are used to clear the adapter when there are no schedules
        // inside the selected zone. Otherwise instead of empty schedules it would show another
        // Zone's schedule when the empty Zone is selected. These 5 lines of code are very important.
        // Do not remove unless a solution is found. This is the only workaround I could find.

        mSchedules.add(new Schedule("", ""));
        final ScheduleAdapter adapter = new ScheduleAdapter(DayActivity.this, mSchedules);
        ListView scheduList = findViewById(R.id.list);
        scheduList.setAdapter(adapter);
        adapter.clear();


        ref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    // Gets specified value from Firebase DB
                    // See https://stackoverflow.com/questions/46047189/how-to-get-particular-value-using-datasnapshot-in-firebase

                    String keyNumber = ds.getKey();
                    final String day;
                    String start;
                    String finish;
                    String keySchedule;

                    switch (keyNumber) {
                        case "day":
                            day = ds.getValue(String.class);
                            mDay.add(day);
                            break;
                        case "start":
                            start = ds.getValue(String.class);
                            mScheduleStart.add(start);
                            break;
                        case "finish":
                            finish = ds.getValue(String.class);
                            mScheduleFinish.add(finish);
                            break;
                        case "scheduleKey":
                            keySchedule = ds.getValue(String.class);
                            mScheduleKey.add(keySchedule);
                    }
                }
                final int selectedZoneName = position;
                if (mDay.get(mDay.size() - 1).equals(daySelected)) {

                    mSchedules.add(new Schedule(mScheduleStart.get(mScheduleFinish.size() - 1), mScheduleFinish.get(mScheduleFinish.size() - 1)));
                    final ScheduleAdapter adapter = new ScheduleAdapter(DayActivity.this, mSchedules);
                    ListView scheduList = findViewById(R.id.list);
                    scheduList.setAdapter(adapter);

                    scheduList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            if (position == adapter.getItemId(position)) {
                                Bundle bundleZoneKey = new Bundle();
                                mSelectedZoneKey = getIntent().getExtras();

                                Intent intent = new Intent(DayActivity.this, ScheduleCRUDActivity.class);
                                intent.putExtra(getString(R.string.key_sytem_key), key);
                                intent.putExtra(getString(R.string.key_schedule_name), mScheduleKey.get(position));
                                intent.putExtra(getString(R.string.key_day), daySelected);
                                intent.putExtra(getString(R.string.key_zone), mZoneKey.get(0));
                                intent.putExtra(getString(R.string.key_zone_name), mNames.get(selectedZoneName));
                                intent.putExtra(getString(R.string.key_zone_number), mNumbers.get(0));
                                intent.putExtra(getString(R.string.key_zone_position), zonePos);
                                intent.putExtra(getString(R.string.key_schedule_key), mScheduleKey.get(position));
                                final ArrayList<String> zoneKey = mSelectedZoneKey.getStringArrayList(getString(R.string.key_zone_key));

                                intent.putStringArrayListExtra(getString(R.string.key_zone_key), (ArrayList<String>) zoneKey);
                                intent.putExtra(getString(R.string.key_schedule_start), mScheduleStart.get(position));
                                intent.putExtra(getString(R.string.key_schedule_finish), mScheduleFinish.get(position));
                                startActivityForResult(intent, position);
                            }
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


        mScheduleStart.clear();
        mScheduleFinish.clear();
        mSchedules.clear();

        if (zoneKey != null) {
            zoneKey.clear();
        }
    }


    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    public void addNew(View view) {
        Intent intent = new Intent(DayActivity.this, ScheduleActivity.class);
        String key = mSelectedSysKey.getString(getString(R.string.key_system_key));
        String name = mSelectedSysName.getString(getString(R.string.key_system_name));
        mSelectedZoneKey = getIntent().getExtras();
        final ArrayList<String> zoneKey = mSelectedZoneKey.getStringArrayList(getString(R.string.key_zone_key));
        final int zonePos = mZoneSpinner.getSelectedItemPosition();

        intent.putExtra(getString(R.string.key_day), mTV_DayTitle.getText());
        intent.putExtra(getString(R.string.key_system_key), key);
        intent.putExtra(getString(R.string.key_sytem_name), name);
        intent.putExtra(getString(R.string.key_zone_position), zonePos);

        intent.putStringArrayListExtra(getString(R.string.key_zone_key), (ArrayList<String>) zoneKey);


        startActivity(intent);

    }

    // Add widget
    public void addWidget(View view) {
        final String daySelected = mDaySelected.getString(getString(R.string.key_day));
        String zoneName = mZoneSpinner.getSelectedItem().toString();

        sharedPreferences = getSharedPreferences(getString(R.string.shared_pref_widget_day), MODE_PRIVATE);
        StringBuilder result = new StringBuilder();

        for (int i = 0; i <= mScheduleStart.size() - 1; i++) {
            result.append("Start: " + mScheduleStart.get(i) + "\n")
                    .append("Finish: " + mScheduleFinish.get(i) + "\n\n");
        }

        sharedPreferences

                .edit()
                .putInt(PREFERENCES_ID, 1)
                .putString(PREFERENCES_WIDGET_TITLE, "Day: " + daySelected + "\n" + "Zone: " + zoneName)
                .putString(PREFERENCES_WIDGET_CONTENT, result.toString())
                .apply();

//        Context context = getApplicationContext();
        ComponentName provider = new ComponentName(this, DayWidgetProvider.class);
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        int[] ids = appWidgetManager.getAppWidgetIds(provider);
        DayWidgetProvider dayAppWidget = new DayWidgetProvider();
        dayAppWidget.onUpdate(this, appWidgetManager, ids);

//TODO Add Snackbar message saying widget was added
    }


    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, HomepageActivity.class);
        startActivity(intent);
    }


}