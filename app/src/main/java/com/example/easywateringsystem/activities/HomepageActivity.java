package com.example.easywateringsystem.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.viewpager.widget.ViewPager;

import com.example.easywateringsystem.R;
import com.example.easywateringsystem.adapters.ItemPagerAdapter;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class HomepageActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, AdapterView.OnItemSelectedListener {
    public static HomepageActivity OnDataPassedListener;
    private DrawerLayout drawer;
    private Spinner mSystemSpinner;
    private DatabaseReference ref;
    private FirebaseAuth mAuth;

    private TextView mHomepageTitle;

    List<String> mSystemKey;
    List<String> mSystemAddress;
    List<String> mZoneKey;
    List<String> mNameFirst;
    List<String> mNameLast;
    List<String> mEmail;
    List<String> mImage;
    String mgetData;

    ImageView mProfilePic;
    TextView mUserName;
    TextView mUserEmail;

    private Button sysCrudBt;
    private Button sysLogout;
    private Bundle mSysKey = new Bundle();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // See https://codinginflow.com/tutorials/android/text-spinner
        drawer = findViewById(R.id.drawer_layout);

        mHomepageTitle = findViewById(R.id.title_homepage);
        sysCrudBt = (Button) findViewById(R.id.manage_systems);
//        mProfilePic = findViewById(R.id.user_picture);
        sysLogout = findViewById(R.id.logout);
        final NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        mNameLast = new ArrayList<String>();
        mNameFirst = new ArrayList<String>();
        mEmail = new ArrayList<String>();
        mZoneKey = new ArrayList<String>();
        mSystemKey = new ArrayList<String>();
        mSystemAddress = new ArrayList<String>();
        mImage = new ArrayList<String>();
        // See https://stackoverflow.com/questions/34973456/how-to-change-text-of-a-textview-in-navigation-drawer-header
        View headerView = navigationView.getHeaderView(0);
        mUserName = headerView.findViewById(R.id.user_name);
        mUserEmail = headerView.findViewById(R.id.user_email);
        mProfilePic = headerView.findViewById(R.id.user_picture);

        mUserName.setText(R.string.user_name_homepage);
        mUserEmail.setText(R.string.user_email_homepage);

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        String userKey = currentUser.getUid();


        ref = FirebaseDatabase.getInstance()
                .getReference(getString(R.string.firebase_path_Users))
                .child(userKey);
        Query userByOrder = ref.orderByKey();

        userByOrder.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    String key = ds.getKey();
                    String value = ds.getValue().toString();
                    String email;
                    String nameFirst;
                    String nameLast;
                    String image;
                    switch (key) {
                        case "email: ":
                            email = ds.getValue(String.class);
                            mEmail.add(email);
                            break;
                        case "nameFirst: ":
                            nameFirst = ds.getValue(String.class);
                            mNameFirst.add(nameFirst);
                            break;
                        case "nameLast: ":
                            nameLast = ds.getValue(String.class);
                            mNameLast.add(nameLast);
                            break;
                        case "image: ":
                            image = ds.getValue(String.class);
                            mImage.add(image);
                            break;
                    }

                }

                // Checks if if array has values before it populates text views
                // so the values aren't null and so it won't crash the app
                if (mNameFirst.size() != 0) {
                    mUserName.setText(mNameFirst.get(0) + " " + mNameLast.get(0));
                    mUserEmail.setText(mEmail.get(0));
                }
                if (mImage.size() != 0 && !mImage.get(0).equals("")) {
                    Picasso.with(HomepageActivity.this)
                            .load(mImage.get(0))
                            .resize(256, 256)
                            .centerCrop()
                            .into(mProfilePic);
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


        // System key generated to uniquely identify System
        // See https://stackoverflow.com/questions/37094631/get-the-pushed-id-for-specific-value-in-firebase-android

        ref = FirebaseDatabase.getInstance()
                .getReference(getString(R.string.firebase_path_Users))
                .child(userKey)
                .child(getString(R.string.firebase_child_Systems));

        Query sysByOrder = ref.orderByKey();
        sysByOrder.addChildEventListener(new ChildEventListener() {
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
                    // Below is used to fill the spinner with the string array from systemValues
                    // See https://stackoverflow.com/questions/46382741/android-using-string-resource-file-to-fill-a-spinner
                    mSystemSpinner = (Spinner) navigationView.getMenu().findItem(R.id.select_system).getActionView();
                    ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(HomepageActivity.this, android.R.layout.simple_spinner_item);
                    mSystemSpinner.setAdapter(spinnerAdapter);
                    spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    mSystemSpinner.setOnItemSelectedListener(HomepageActivity.this);
                    for (int i = 0; i <= mSystemAddress.size() - 1; i++) {
                        // .substring(5) removes the first 5 characters of the system address string "{var=)"
                        // See https://stackoverflow.com/questions/4503656/java-removing-first-character-of-a-string#:~:text=12%20Answers&text=Use%20the%20substring()%20function,full%20length%20of%20the%20string).&text=Use%20substring()%20and%20give,want%20to%20trim%20from%20front.
                        // .split("}") removes everything after the specified characters
                        // See https://stackoverflow.com/questions/12277461/delete-everything-after-part-of-a-string
                        spinnerAdapter.add(mSystemAddress.get(i).substring(0).split("=")[0].split("[}]")[0].replaceAll("[{]", ""));
                        spinnerAdapter.notifyDataSetChanged();

                    }

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


    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        if (drawer.isDrawerOpen(Gravity.RIGHT)) {
            drawer.closeDrawer(Gravity.RIGHT);
        } else {
            drawer.openDrawer(Gravity.RIGHT);

        }
        return true;
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    public void addNew(View view) {
        Intent intent = new Intent(this, ZoneCRUDActivity.class);
        // systemName string is used to pass selected system's name to the ZoneCRUDActivty
        if (mSystemSpinner != null) {
            // If a system IS selected from the spinner, then the selected system
            // is sent to the ZoneCRUDActivity

            String systemAddress = mSystemSpinner.getSelectedItem().toString();
            int keyPos = mSystemSpinner.getSelectedItemPosition();
            String systemKey = mSystemKey.get(keyPos);
            intent.putExtra(getString(R.string.key_system_key), systemKey);
            intent.putExtra(getString(R.string.key_system_name), systemAddress);


            startActivity(intent);
        } else {
            // If a system is NOT selected from the spinner, then the first system value
            // is sent to the ZoneCRUDActivity
            intent.putExtra(getString(R.string.key_system_key), mSystemKey.toString());
            intent.putExtra(getString(R.string.key_system_name), mSystemAddress.toString());
            startActivity(intent);
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        // When Item is selected from Spinner, changes the Homepage Title to the address of the selected System
        String trimmedAddress = mSystemAddress.get(position).substring(0).split("=")[0].split("[}]")[0].replaceAll("[{]", "");
        mHomepageTitle.setText(mSystemSpinner.getItemAtPosition(position).toString());
        ItemPagerAdapter pagerAdapter = new ItemPagerAdapter(this, getSupportFragmentManager(), mSystemKey.get(position), trimmedAddress, mZoneKey);
        ViewPager viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(pagerAdapter);
        TabLayout tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);


    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    public void crudSystem(View view) {
        Intent intent = new Intent(this, SystemListActivity.class);
        startActivity(intent);
    }

    public void userAccount(View view) {

        String firstName = mNameFirst.get(0);
        String lastName = mNameLast.get(0);
        String email = mEmail.get(0);


        final Intent intent = new Intent(this, UserActivity.class);

        intent.putExtra(getString(R.string.key_user_first), firstName);
        intent.putExtra(getString(R.string.key_user_last), lastName);
        intent.putExtra(getString(R.string.key_user_email), email);
        if (mImage != null) {
            String image = mImage.get(0);
            intent.putExtra(getString(R.string.key_user_image), image);

        }

        startActivity(intent);


    }

    public void logOut(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Confirm");
        builder.setMessage("Are you sure?");

        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                // Do nothing but close the dialog
                mAuth.getInstance().signOut();
                Intent intent = new Intent(HomepageActivity.this, LoginActivity.class);
                startActivity(intent);
                dialog.dismiss();
            }
        });

        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                // Do nothing
                dialog.dismiss();
            }
        });

        AlertDialog alert = builder.create();
        alert.show();


    }
}
