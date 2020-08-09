package com.example.easywateringsystem.activities;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.example.easywateringsystem.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.material.snackbar.Snackbar;

import cdflynn.android.library.checkview.CheckView;

public class LinkNewSysActivity extends AppCompatActivity {
    // Member variables
    private ProgressBar progressBar;
    private CheckView mCheckView;
    // Create a Handler instance on the main thread
    private Handler handler = new Handler();
    private Snackbar snackbar;
    private RelativeLayout rel_lay_link;

    // Log to verify correct Google Play Services version
    private static final String TAG = "LinkNewSysActivity";
    // Error number displayed in log if the correct Google Play Services
    // version doesn't exist
    private static final int ERROR_DIALOG_REQUEST = 9001;

    private Bundle mUserCreds;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_link_system);
        progressBar = (ProgressBar) findViewById(R.id.progress_circular);
        mCheckView = (CheckView) findViewById(R.id.check);
        rel_lay_link = (RelativeLayout) findViewById(R.id.layout_link_new_sys);
mUserCreds = getIntent().getExtras();

    }

    // When clicked, links the new system to the app
    // (pretends to connect via bluetooth)
    public void linkSystem(View view) throws InterruptedException {
        progressBar.setVisibility(View.VISIBLE);
        // Creates a new thread after making progressBar visible
        // so that it can freeze the UI for 2 seconds while the bar
        // spins, and then makes the bar invisible again
        new Thread(new Runnable() {
            public void run() {
                try {
                    Thread.sleep(2000);
                } catch (Exception e) {
                }
                // Now we use the Handler to post back to the main thread
                handler.post(new Runnable() {
                    public void run() {
                        // Set the View's visibility back on the main UI Thread
                        progressBar.setVisibility(View.INVISIBLE);
                        // Display check-mark that communicates to user that task
                        // is complete
                        mCheckView.check();
                        // Display to user that the new system is linked to the app
                        snackbar = Snackbar.make(rel_lay_link, "New system is linked! Enter system address.", snackbar.LENGTH_INDEFINITE);
                        snackbar.show();
                    }
                });
            }
        }).start();

    }

    // When clicked, allows user to use Google maps
    // to enter location address for their newly installed system
    public void enterAddress(View view) {
        if(isServicesOK()) {
            mUserCreds = getIntent().getExtras();
            String value = mUserCreds.getString(getString(R.string.key_user_key));
           final Intent intent = new Intent(this, MapActivity.class);
    // Sends logged user's creds to map activity so it can add sys address under it
            intent.putExtra(getString(R.string.key_user_key), value);
            startActivity(intent);
        }
    }



    public boolean isServicesOK() {
        Log.d(TAG, "isServicesOK: Checking Google Play Services version");

        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(LinkNewSysActivity.this);
        if (available == ConnectionResult.SUCCESS) {
            Log.d(TAG, "isServicesOK: Google Play Services is working");
            return true;
        } else if (GoogleApiAvailability.getInstance().isUserResolvableError(available)) {
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(LinkNewSysActivity.this, available, ERROR_DIALOG_REQUEST);
            dialog.show();
        } else {
            //SNACKBAR "You can't make map requests."
        }
        return false;

    }

}
