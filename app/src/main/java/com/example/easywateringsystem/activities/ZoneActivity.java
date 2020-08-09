package com.example.easywateringsystem.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.easywateringsystem.R;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

public class ZoneActivity extends AppCompatActivity {

    ProgressBar mProgressBar;
    CountDownTimer mCountDownTimer;
    int i = 0;
    private Bundle mSystemKey;
    private Bundle mSystemName;
    private Bundle mSelectedZoneKey;
    private Bundle mSelectedZoneImage;
    private Bundle mSelectedZoneNumber;
    private Bundle mSelectedZoneName;
    private Bundle mSelectedZonePosition;
    private Bundle mSelectedSystemKey;
    private Bundle mSelectedSystemName;

    private DatabaseReference ref = FirebaseDatabase.getInstance().getReference();

    private TextView mTV_ZoneName;
    private TextView mTV_ZoneNumber;
    private ImageView mIV_ZoneImage;
    private Snackbar snackbar;
    private RelativeLayout rel_lay_zone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zone);

        mSystemKey = getIntent().getExtras();
        mSystemName = getIntent().getExtras();
        mSelectedZoneNumber = getIntent().getExtras();
        mSelectedZoneName = getIntent().getExtras();
        mSelectedSystemKey = getIntent().getExtras();
        mSelectedSystemName = getIntent().getExtras();
        mSelectedZoneKey = getIntent().getExtras();
        mSelectedZoneImage = getIntent().getExtras();
        mSelectedZoneNumber = getIntent().getExtras();
        mSelectedZoneName = getIntent().getExtras();
        mSelectedZonePosition = getIntent().getExtras();
        rel_lay_zone = (RelativeLayout) findViewById(R.id.layout_zone);
        mTV_ZoneName = findViewById(R.id.zone_name_tv);
        mTV_ZoneNumber = findViewById(R.id.zone_number_tv);
        mIV_ZoneImage = findViewById(R.id.zone_image);
        mProgressBar = (ProgressBar) findViewById(R.id.zone_timer_pbar);
        mProgressBar.setProgress(i);
        String imageUrl = mSelectedZoneImage.getString(getString(R.string.key_zone_image));
        if (mSelectedZoneNumber != null && mSelectedSystemName != null) {
            String key = mSelectedZoneNumber.getString(getString(R.string.key_zone_id));
            String name = mSelectedZoneName.getString(getString(R.string.key_zone_name));
            mTV_ZoneNumber.setText(key);
            mTV_ZoneName.setText(name);
        }
        if (mSelectedZoneImage != null && !imageUrl.equals("")) {
            Picasso.with(this).load(imageUrl).fit().into(mIV_ZoneImage);
        }
        else {
            Picasso.with(this).load(R.drawable.splash_image).fit().into(mIV_ZoneImage);
        }

    }

    // See https://stackoverflow.com/questions/10241633/android-progressbar-countdown/29654342
    public void runTimer(final View view) {
        mCountDownTimer = new CountDownTimer(5000, 1000) {

            @Override
            public void onTick(long millisUntilFinished) {
                Log.v("Log_tag", "Tick of Progress" + i + millisUntilFinished);
                i++;
                mProgressBar.setProgress((int) i * 100 / (5000 / 1000));
            }

            @Override
            public void onFinish() {
                //Do what you want
                i++;
                Snackbar.make(view, "                             " +
                        "Finished watering the plants!", 1200)
                        .addCallback(new Snackbar.Callback() {
                            @Override
                            public void onDismissed(Snackbar snackbar, int event) {
                                super.onDismissed(snackbar, event);
                                mProgressBar.setProgress(100);
                            }
                        }).show();

            }
        };
        Snackbar.make(view, "                               " +
                "    Watering the plants!", 1200)
                .addCallback(new Snackbar.Callback() {
                    @Override
                    public void onDismissed(Snackbar snackbar, int event) {
                        super.onDismissed(snackbar, event);
                        mCountDownTimer.start();
                    }
                }).show();

    }


    public void deleteZone(View view) {
        mSystemKey = getIntent().getExtras();
        mSystemName = getIntent().getExtras();
        mSelectedZoneKey = getIntent().getExtras();
        mSelectedZoneNumber = getIntent().getExtras();
        mSelectedZoneName = getIntent().getExtras();
        final Intent intent = new Intent(this, HomepageActivity.class);
        String sysKey = mSystemKey.getString(getString(R.string.key_system_key));
        String zoneKey = mSelectedZoneKey.getString(getString(R.string.key_zone_key));

        final String zoneNumber = mSelectedZoneNumber.getString(getString(R.string.key_zone_id));
        final String zoneName = mSelectedZoneName.getString(getString(R.string.key_zone_name));

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        String userKey = currentUser.getUid();
        // See https://stackoverflow.com/questions/37094631/get-the-pushed-id-for-specific-value-in-firebase-android

        ref = FirebaseDatabase.getInstance()
                .getReference(getString(R.string.firebase_path_Users))
                .child(userKey)
                .child(getString(R.string.firebase_child_Systems))
                .child(sysKey)
                .child(getString(R.string.firebase_child_Zones))
                .child(zoneKey);


        ref.removeValue();
        // When saved, Snackbar shows the message and then after finished showing it goes to previous activity.
        // See https://stackoverflow.com/questions/34578375/how-to-wait-to-snackbar-i-want-to-know-when-it-is-closed
        Snackbar.make(view, "Zone deleted!", 1200)
                .addCallback(new Snackbar.Callback() {
                    @Override
                    public void onDismissed(Snackbar snackbar, int event) {
                        super.onDismissed(snackbar, event);

                        startActivity(intent);
                    }
                }).show();

    }

    public void editZone(View view) {
        String zoneId = mSelectedZoneKey.getString(getString(R.string.key_zone_key));
        String zoneNumber = mSelectedZoneNumber.getString(getString(R.string.key_zone_id));
        String zoneName = mSelectedZoneName.getString(getString(R.string.key_zone_name));
        String zoneImage = mSelectedZoneImage.getString(getString(R.string.key_zone_image));
        int zonePosition = mSelectedZonePosition.getInt(getString(R.string.key_zone_position));
        String systemId = mSelectedSystemKey.getString(getString(R.string.key_system_key));
        String systemName = mSelectedSystemName.getString(getString(R.string.key_system_name));


        Intent intent = new Intent(this, ZoneEditActivity.class);
        intent.putExtra(getString(R.string.key_zone_key), zoneId);
        intent.putExtra(getString(R.string.key_zone_id), zoneNumber);
        intent.putExtra(getString(R.string.key_zone_name), zoneName);
        intent.putExtra(getString(R.string.key_zone_position), zonePosition);
        intent.putExtra(getString(R.string.key_zone_image), zoneImage);
        intent.putExtra(getString(R.string.key_system_key), systemId);
        intent.putExtra(getString(R.string.key_system_name), systemName);
        startActivity(intent);
    }
}
