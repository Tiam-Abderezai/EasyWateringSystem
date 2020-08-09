
package com.example.easywateringsystem.activities;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import com.example.easywateringsystem.R;
import com.example.easywateringsystem.fragments.TimePickerFragment;
import com.example.easywateringsystem.models.Zone;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

public class ScheduleCRUDActivity extends AppCompatActivity implements TimePickerDialog.OnTimeSetListener {
    private Bundle mSelectedDay;
    private Bundle mSelectedSysKey;
    private Bundle mSelectedSysName;
    private Bundle mSelectedZoneKey;
    private Bundle mSelectedZoneArray;
    private Bundle mSelectedZoneNumber;
    private Bundle mSelectedZoneName;
    private Bundle mSelectedScheduKey;
    private Bundle mSelectedScheduStart;
    private Bundle mSelectedScheduFinish;
    private Bundle mSelectedZonePosition;

    private DatabaseReference ref;
    private TextView mTV_ZoneName;
    private TextView mTV_ScheduleDay;
    private TextView mTV_ZoneNumber;
    private TextView mTV_ScheduleStart;
    private TextView mTV_ScheduleEnd;

    private Button mBT_ScheduleStart;
    private Button mBT_ScheduleEnd;
    private boolean mBoolClickedStart;
    private boolean mBoolClickedEnd;

    ArrayList<Zone> mZones = new ArrayList<>();
    List<String> mScheduleId;
    List<String> mScheduleStart;
    List<String> mScheduleFinish;
    List<String> mZoneKey;
    List<String> mZoneName;
    List<String> mSysKey;
    List<String> mSysName;
    List<String> mNumbers;
    List<String> mNames;

    private Snackbar snackbar;
    private RelativeLayout rel_lay_zone_edit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crud_schedule);
        mTV_ScheduleDay = findViewById(R.id.schedule_day_crud);
        mTV_ZoneNumber = findViewById(R.id.zone_number_crud);
        mTV_ScheduleStart = findViewById(R.id.schedule_start_crud);
        mTV_ScheduleEnd = findViewById(R.id.schedule_end_crud);
        mSelectedDay = getIntent().getExtras();
        mSelectedSysKey = getIntent().getExtras();
        mSelectedSysName = getIntent().getExtras();
        mSelectedZoneKey = getIntent().getExtras();
        mSelectedZoneNumber = getIntent().getExtras();
        mSelectedZoneName = getIntent().getExtras();
        mSelectedZonePosition = getIntent().getExtras();
        mSelectedScheduKey = getIntent().getExtras();
        mSelectedScheduStart = getIntent().getExtras();
        mSelectedScheduFinish = getIntent().getExtras();
        if (mSelectedDay != null) {
            String value = mSelectedDay.getString(getString(R.string.key_day));
            String start = mSelectedScheduStart.getString(getString(R.string.key_schedule_start));
            String finish = mSelectedScheduFinish.getString(getString(R.string.key_schedule_finish));
            mTV_ScheduleDay.setText(value);
            mTV_ScheduleStart.setText(start);
            mTV_ScheduleEnd.setText(finish);

        }
        String systemKey = mSelectedSysKey.getString(getString(R.string.key_system_key));
        String systemName = mSelectedSysName.getString(getString(R.string.key_sytem_name));
        String keyZone = mSelectedZoneKey.getString(getString(R.string.key_zone_key));
        String numberZone = mSelectedZoneNumber.getString(getString(R.string.key_zone_number));
        String nameZone = mSelectedZoneName.getString(getString(R.string.key_zone_name));


        mSysKey = new ArrayList<String>();
        mSysName = new ArrayList<String>();

        mSysKey.add(systemKey);
        mSysName.add(systemName);


        mNumbers = new ArrayList<String>();
        mNames = new ArrayList<String>();

        mScheduleId = new ArrayList<String>();

        mTV_ZoneName = findViewById(R.id.zone_name_crud);
        mTV_ZoneName.setText(nameZone);
        mTV_ZoneNumber.setText(numberZone);
        mZoneKey = new ArrayList<String>();
        mZoneName = new ArrayList<String>();

        rel_lay_zone_edit = (RelativeLayout) findViewById(R.id.layout_schedule_crud);


        mBT_ScheduleStart = (Button) findViewById(R.id.timepicker_start_crud);
        mBT_ScheduleStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment timePicker = new TimePickerFragment();
                timePicker.show(getSupportFragmentManager(), "START TIME");
                mBoolClickedStart = true;
            }
        });


        mBT_ScheduleEnd = (Button) findViewById(R.id.timepicker_end_crud);
        mBT_ScheduleEnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment timePicker = new TimePickerFragment();
                timePicker.show(getSupportFragmentManager(), "END TIME");
                mBoolClickedEnd = true;
            }
        });

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
        mSelectedZoneKey = getIntent().getExtras();
        mSelectedScheduKey = getIntent().getExtras();

        String idSchedule = mSelectedScheduKey.getString(getString(R.string.key_schedule_key));
        String keyZone = mSelectedZoneKey.getString(getString(R.string.key_zone));
        int zonePos = mSelectedZonePosition.getInt(getString(R.string.key_zone_position));

        String day = mSelectedDay.getString(getString(R.string.key_day));

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        String userKey = currentUser.getUid();

        // System key generated to uniquely identify System
        // See https://stackoverflow.com/questions/37094631/get-the-pushed-id-for-specific-value-in-firebase-android

        HashMap hashMap = new HashMap();


        hashMap.put(getString(R.string.hashmap_scheduleKey), idSchedule);
        hashMap.put(getString(R.string.hashmap_day), day);
        hashMap.put(getString(R.string.hashmap_start), tvStart);
        hashMap.put(getString(R.string.hashmap_finish), tvFinish);

        FirebaseDatabase.getInstance().getReference()
                .child(getString(R.string.firebase_path_Users))
                .child(userKey)
                .child(getString(R.string.firebase_child_Systems))
                .child(sysKey)
                .child(getString(R.string.firebase_child_Zones))
                .child(keyZone)
                .child(getString(R.string.firebase_child_Schedules))
                .child(idSchedule)
                .setValue(hashMap);

        // used to back out of an activity
        // See https://stackoverflow.com/questions/15393899/how-to-close-activity-and-go-back-to-previous-activity-in-android

        final Intent intent = new Intent(this, DayActivity.class);

        mSelectedZoneArray = getIntent().getExtras();
        ArrayList<String> zoneArray = mSelectedZoneArray.getStringArrayList(getString(R.string.key_zone_key));
        intent.putStringArrayListExtra(getString(R.string.key_zone_key), (ArrayList<String>) zoneArray);
        intent.putExtra(getString(R.string.key_day), day);
        intent.putExtra(getString(R.string.key_system_key), sysKey);
        intent.putExtra(getString(R.string.key_system_name), sysAddress);
        intent.putExtra(getString(R.string.key_zone_position), zonePos);
        // When saved, Snackbar shows the message and then after finished showing it goes to previous activity.
        // See https://stackoverflow.com/questions/34578375/how-to-wait-to-snackbar-i-want-to-know-when-it-is-closed
        Snackbar.make(view, "Schedule saved successfully!", 1200)
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
            TextView tvStart = (TextView) findViewById(R.id.schedule_start_crud);
            String am_pm = "";

            Calendar datetime = Calendar.getInstance();
            datetime.set(Calendar.HOUR_OF_DAY, hourOfDay);
            String minuteFormatted = String.format("%02d", datetime.get(Calendar.MINUTE));
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
            TextView tvEnd = (TextView) findViewById(R.id.schedule_end_crud);
            String am_pm = "";

            Calendar datetime = Calendar.getInstance();
            datetime.set(Calendar.HOUR_OF_DAY, hourOfDay);
            String minuteFormatted = String.format("%02d", datetime.get(Calendar.MINUTE));
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

    public void deleteSchedule(View view) {

        mSelectedDay = getIntent().getExtras();
        mSelectedSysKey = getIntent().getExtras();
        mSelectedSysName = getIntent().getExtras();
        mSelectedZoneKey = getIntent().getExtras();
        mSelectedZoneArray = getIntent().getExtras();
        mSelectedScheduKey = getIntent().getExtras();
        mSelectedScheduStart = getIntent().getExtras();
        mSelectedScheduFinish = getIntent().getExtras();
        mSelectedZonePosition = getIntent().getExtras();

        String day = mSelectedDay.getString(getString(R.string.key_day));
        String idSchedule = mSelectedScheduKey.getString(getString(R.string.key_schedule_id));
        String start = mSelectedScheduStart.getString(getString(R.string.key_schedule_start));
        String finish = mSelectedScheduFinish.getString(getString(R.string.key_schedule_finish));
        String sysKey = mSelectedSysKey.getString(getString(R.string.key_system_key));
        String systemName = mSelectedSysName.getString(getString(R.string.key_system_name));
        String zoneKey = mSelectedZoneKey.getString(getString(R.string.key_zone));
        int zonePos = mSelectedZonePosition.getInt(getString(R.string.key_zone_position));
        String scheduleKey = mSelectedScheduKey.getString(getString(R.string.key_schedule_key));
        ArrayList<String> zoneArray = mSelectedZoneArray.getStringArrayList(getString(R.string.key_zone_key));

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        String userKey = currentUser.getUid();
        // See https://stackoverflow.com/questions/37094631/get-the-pushed-id-for-specific-value-in-firebase-android


        ref = FirebaseDatabase.getInstance()
                .getReference(getString(R.string.firebase_path_Users))
                .child(userKey)
                .child(getString(R.string.firebase_child_Systems))
                .child(sysKey)
                .child(getString(R.string.firebase_child_Zones))
                .child(zoneKey)
                .child(getString(R.string.firebase_child_Schedules))
                .child(scheduleKey);


        ref.removeValue();
        final Intent intent = new Intent(this, DayActivity.class);
        intent.putExtra(getString(R.string.key_day), day);
        intent.putExtra(getString(R.string.key_system_key), sysKey);
        intent.putStringArrayListExtra(getString(R.string.key_zone_key), (ArrayList<String>) zoneArray);
        intent.putExtra(getString(R.string.key_zone_position), zonePos);


        Snackbar.make(view, "Schedule deleted!", 1200)
                .addCallback(new Snackbar.Callback() {
                    @Override
                    public void onDismissed(Snackbar snackbar, int event) {
                        super.onDismissed(snackbar, event);
                        startActivity(intent);
                    }
                }).show();
    }
}