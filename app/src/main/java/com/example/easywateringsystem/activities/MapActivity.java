package com.example.easywateringsystem.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.easywateringsystem.R;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.libraries.places.api.model.AutocompleteSessionToken;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.mancj.materialsearchbar.MaterialSearchBar;
import com.skyfishjy.library.RippleBackground;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private PlacesClient placesClient;
    private List<AutocompletePrediction> predictionList;

    private Location mLastKnownLocation;
    private LocationCallback locationCallback;

    private MaterialSearchBar materialSearchBar;
    private View mapView;
    private Button mAddLocationBt;
    private RippleBackground rippleBg;

    private Bundle mUserCreds;
    private static final int REQUEST_LOCATION_PERMISSION = 1;

    private final float DEFAULT_ZOOM = 15;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);


        // Checks if user allows permission to access
        // and grants whether clicking yes or no
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
        } else {
            ActivityCompat.requestPermissions(this, new String[]
                            {Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_LOCATION_PERMISSION);
        }


        mAddLocationBt = findViewById(R.id.btn_location_add);
        rippleBg = findViewById(R.id.ripple_bg);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        mapView = mapFragment.getView();

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(MapActivity.this);
//        Places.initialize(MapActivity.this, getString(R.string.google_api_key));
        placesClient = Places.createClient(this);
        final AutocompleteSessionToken token = AutocompleteSessionToken.newInstance();

        mAddLocationBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final LatLng currentMarkerLocation = mMap.getCameraPosition().target;
                rippleBg.startRippleAnimation();

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Geocoder geocoder = new Geocoder(MapActivity.this, Locale.getDefault());
//                        String sysKey = UUID.randomUUID().toString();

                        List<Address> addresses = null;
                        try {
                            addresses = geocoder.getFromLocation(currentMarkerLocation.latitude, currentMarkerLocation.longitude, 1);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        // Address variables commented out for future use if needed
//                        String address = addresses.get(0).getAddressLine(0);
//                        String city = addresses.get(0).getLocality();
//                        String state = addresses.get(0).getAdminArea();
//                        String zip = addresses.get(0).getPostalCode();
//                        String country = addresses.get(0).getCountryName();
                        String fullAddress = addresses.get(0).toString();

                        // Regex to extract the full address from fullAddress
                        // without any other characters that aren't needed
                        // See https://stackoverflow.com/questions/12595019/how-to-get-a-string-between-two-characters
                        Pattern p = Pattern.compile("\"([^\"]*)\"");
                        Matcher m = p.matcher(fullAddress);

                        while (m.find()) {
                            // Gets current user's id that's currently logged in
                            // See https://stackoverflow.com/questions/37566911/how-to-get-user-uid-from-firebase-on-android
                            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                            String userKey = currentUser.getUid();
                            // System key generated to uniquely identify System
                            // See https://stackoverflow.com/questions/37094631/get-the-pushed-id-for-specific-value-in-firebase-android
                            String sysKey = FirebaseDatabase.getInstance().getReference()
                                    .child(getString(R.string.firebase_path_Users))
                                    .child(userKey)
                                    .child(getString(R.string.firebase_child_Systems))
                                    .push()
                                    .getKey();

//                            Map<String, String> sysMap = new HashMap<>();
                            String sysAddress = m.group(1);
                            HashMap hashMap = new HashMap();
                            hashMap.put(getString(R.string.hashmap_sysKey), sysKey);
                            hashMap.put(getString(R.string.hashmap_sysAddress), sysAddress);

                            int random = (int) (Math.random() * 9000000 + 1);

                            FirebaseDatabase.getInstance().getReference()
                                    .child(getString(R.string.firebase_path_Users))
                                    .child(userKey)
                                    .child(getString(R.string.firebase_child_Systems))
                                    .child(sysKey)
                                    .setValue(hashMap);


                            // Retrieves user data from firebase realtime DB
                            // See https://www.youtube.com/watch?v=LpWhAz3e1sI
//                            FirebaseDatabase.getInstance().getReference().child("Users").
//                                    mUserCreds = getIntent().getExtras();

                            // Adds System data (address) under User to the database
                            // See https://www.youtube.com/watch?v=XhdR2ffYGoE
//                            FirebaseDatabase.getInstance().getReference().child("").child("").setValue("");

                        }
                        Intent intent = new Intent(MapActivity.this, HomepageActivity.class);
//                        intent.putExtra("EXTRA_SYSTEM_KEY", sysKey);
                        startActivity(intent);
                        rippleBg.stopRippleAnimation();
                        finish();
                    }
                }, 1250);

            }
        });
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);

        if (mapView != null && mapView.findViewById(Integer.parseInt("1")) != null) {
            View locationButton = ((View) mapView.findViewById(Integer.parseInt("1")).getParent()).findViewById(Integer.parseInt("2"));
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) locationButton.getLayoutParams();
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
            layoutParams.setMargins(0, 0, 40, 180);
        }

        //check if gps is enabled or not and then request user to enable it
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);

        SettingsClient settingsClient = LocationServices.getSettingsClient(MapActivity.this);
        Task<LocationSettingsResponse> task = settingsClient.checkLocationSettings(builder.build());

        task.addOnSuccessListener(MapActivity.this, new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                getDeviceLocation();
            }
        });

        task.addOnFailureListener(MapActivity.this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if (e instanceof ResolvableApiException) {
                    ResolvableApiException resolvable = (ResolvableApiException) e;
                    try {
                        resolvable.startResolutionForResult(MapActivity.this, 51);
                    } catch (IntentSender.SendIntentException e1) {
                        e1.printStackTrace();
                    }
                }
            }
        });

        mMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
            @Override
            public boolean onMyLocationButtonClick() {
                if (materialSearchBar.isSuggestionsVisible())
                    materialSearchBar.clearSuggestions();
                if (materialSearchBar.isSearchEnabled())
                    materialSearchBar.disableSearch();
                return false;
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 51) {
            if (resultCode == RESULT_OK) {
                getDeviceLocation();
            }
        }
    }

    @SuppressLint("MissingPermission")
    private void getDeviceLocation() {
        mFusedLocationProviderClient.getLastLocation()
                .addOnCompleteListener(new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful()) {
                            mLastKnownLocation = task.getResult();
                            if (mLastKnownLocation != null) {
                                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude()), DEFAULT_ZOOM));
                            } else {
                                final LocationRequest locationRequest = LocationRequest.create();
                                locationRequest.setInterval(10000);
                                locationRequest.setFastestInterval(5000);
                                locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                                locationCallback = new LocationCallback() {
                                    @Override
                                    public void onLocationResult(LocationResult locationResult) {
                                        super.onLocationResult(locationResult);
                                        if (locationResult == null) {
                                            return;
                                        }
                                        mLastKnownLocation = locationResult.getLastLocation();
                                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude()), DEFAULT_ZOOM));
                                        mFusedLocationProviderClient.removeLocationUpdates(locationCallback);
                                    }
                                };
                                mFusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, null);

                            }
                        } else {
                            Toast.makeText(MapActivity.this, "unable to get last location", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
