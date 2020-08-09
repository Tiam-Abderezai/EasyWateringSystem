package com.example.easywateringsystem.activities;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.example.easywateringsystem.R;
import com.example.easywateringsystem.models.Zone;
import com.example.easywateringsystem.utils.BitmapUtils;
import com.example.easywateringsystem.utils.CameraExecutor;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import static com.example.easywateringsystem.utils.BitmapUtils.FILE_PROVIDER_AUTHORITY;

public class ZoneCRUDActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private ImageView mZoneImage;
    private static final int REQUEST_STORAGE_PERMISSION = 12;
    private static final int REQUEST_IMAGE_CAPTURE = 101;
    private StorageReference mStorage;
    private ProgressDialog mProgress;
    private Spinner mZoneNumber;
    private EditText mZoneName;
    private Bundle mZonePosition;
    private Bundle mSystemName;
    private Bundle mSystemKey;

    private TextView mSystemTV;
    private StorageReference mStoreImgRef;
    private DatabaseReference mDBImgRef;
    private DatabaseReference ref;
    String imgPath = null;
    private Bitmap mResultsBitmap;
    private CameraExecutor mCamExecutor;

    boolean isTakePhoto = false;
    boolean isSelectPhoto = false;
    private String mTempPhotoPath;

    private Snackbar snackbar;
    private RelativeLayout rel_lay_zone_crud;

    private Uri mImageUri = null;
    String zoneNumber;
    String zoneName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crud_zone);

        mSystemName = getIntent().getExtras();
        mSystemKey = getIntent().getExtras();
        mZonePosition = getIntent().getExtras();
        if (mSystemName != null) {
            String sysName = mSystemName.getString(getString(R.string.key_system_name));
            //The key argument here must match that used in the login activity
            mSystemTV = findViewById(R.id.activity_zone_crud_sys_name);
            mSystemTV.setText(sysName);
        }

//        String sysKey = mSystemKey.getString("KEY_SYSTEM_KEY");
        mZoneImage = findViewById(R.id.zone_crud_image);
        mStorage = FirebaseStorage.getInstance().getReference();
        mProgress = new ProgressDialog(this);
        mZoneNumber = findViewById(R.id.activity_zone_crud_zone_number);
        mZoneName = findViewById(R.id.activity_zone_crud_zone_name);
        rel_lay_zone_crud = (RelativeLayout) findViewById(R.id.layout_zone_crud);
        mStoreImgRef = FirebaseStorage.getInstance().getReference("uploads");
        mDBImgRef = FirebaseDatabase.getInstance().getReference("uploads");
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.numbers, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mZoneNumber.setAdapter(adapter);
//        Picasso.with(this).load("").placeholder(R.mipmap.ic_launcher).into(mZoneImage);


        mZoneNumber.setOnItemSelectedListener(this);
        mCamExecutor = new CameraExecutor();

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        String userKey = currentUser.getUid();

        // System key generated to uniquely identify System
        // See https://stackoverflow.com/questions/37094631/get-the-pushed-id-for-specific-value-in-firebase-android
        String testKey = FirebaseDatabase.getInstance().getReference()
                .child(getString(R.string.firebase_path_Users))
                .child(userKey)
                .child(getString(R.string.firebase_child_Systems))
                .push()
                .getKey();


        ref = FirebaseDatabase.getInstance()
                .getReference(getString(R.string.firebase_path_Users))
                .child(userKey)
                .child(getString(R.string.firebase_child_Systems));


        ref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    String val = ds.getValue().toString();
                    String key = ds.getKey();

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

    public void takePhoto(View view) {
        isTakePhoto = true;
        if (ContextCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            // If you do not have permission, request it
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_STORAGE_PERMISSION);
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_STORAGE_PERMISSION);
        } else {
            // Launch the camera if the permission exists
            launchCamera();

        }
    }

    private void launchCamera() {

        // Create the capture image intent
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the temporary File where the photo should go
            File photoFile = null;
            try {
                photoFile = BitmapUtils.createTempImageFile(this);
            } catch (IOException ex) {
                // Error occurred while creating the File
                ex.printStackTrace();
            }
            // "android:requestLegacyExternalStorage="true"" was added to the Manifest file.
            // See https://stackoverflow.com/questions/8854359/exception-open-failed-eacces-permission-denied-on-android

            // Continue only if the File was successfully created
            if (photoFile != null) {
                // Get the path of the temporary file
                mTempPhotoPath = photoFile.getAbsolutePath();
                // Get the content URI for the image file
                Uri photoURI = FileProvider.getUriForFile(this,
                        FILE_PROVIDER_AUTHORITY,
                        photoFile);
//                takePictureIntent.setType("image/*");
                // Add the URI so the camera can store the image
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
//                takePictureIntent.setType("image/*");
                // Launch the camera activity
//                takePictureIntent.getIntent()
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);


            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        // Called when you request permission to read and write to external storage
        switch (requestCode) {
            case REQUEST_STORAGE_PERMISSION: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // If you get permission, launch the camera
                    launchCamera();
                } else {
                    // If you do not get permission, show a Toast
                    Toast.makeText(this, R.string.permission_denied, Toast.LENGTH_SHORT).show();
                }
                break;
            }
        }
    }


    public void selectPhoto(View view) {
        Intent intent = new Intent();
        isSelectPhoto = true;

        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);

    }

    // In order to receive the image result from the application this method is used.

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK && isTakePhoto) {

            mResultsBitmap = BitmapUtils.resamplePic(this, mTempPhotoPath);
//            mZoneImage.setImageBitmap(mResultsBitmap);

            mCamExecutor.diskIO().execute(() -> {
                // Delete the temporary image file
                BitmapUtils.deleteImageFile(this, mTempPhotoPath);

                // Save the image
                BitmapUtils.saveImage(this, mResultsBitmap);

            });
            Toast.makeText(this, "Photo saved.", Toast.LENGTH_LONG).show();
            Toast.makeText(this, "Select photo.", Toast.LENGTH_LONG).show();
            isTakePhoto = false;

        } else if (data != null && data.getData() != null && isSelectPhoto) {
            mImageUri = data.getData();

            Picasso.with(this).load(mImageUri).into(mZoneImage);
            Toast.makeText(this, "Photo selected.", Toast.LENGTH_LONG).show();

            isSelectPhoto = false;
        }


    }


    public void saveZone(View view) throws InterruptedException {
        if (mImageUri != null) {
            final StorageReference fileReference = mStoreImgRef.child(System.currentTimeMillis()
                    + "." + getFileExtension(mImageUri));

            fileReference.putFile(mImageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    mProgress.setProgress(0);
                                }
                            }, 5000);
                            // Snackbar "Upload successful"
                            fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    Uri downloadUri = uri;
                                    Zone zoneImage = new Zone(downloadUri.toString());
                                    String tvNumber;
                                    String tvName;
                                    int zonePos;

                                    tvNumber = mZoneNumber.getSelectedItem().toString();
                                    tvName = mZoneName.getText().toString();
                                    zonePos = mZoneNumber.getSelectedItemPosition();

                                    FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                                    String userKey = currentUser.getUid();
                                    // System key generated to uniquely identify System
                                    // See https://stackoverflow.com/questions/37094631/get-the-pushed-id-for-specific-value-in-firebase-android

                                    String sysKey = mSystemKey.getString(getString(R.string.key_system_key));
                                    String sysAddress = mSystemName.getString(getString(R.string.key_system_name));

                                    HashMap hashMap = new HashMap();


                                    String zoneKey = FirebaseDatabase.getInstance().getReference()
                                            .child(getString(R.string.firebase_path_Users))
                                            .child(userKey)
                                            .child(getString(R.string.firebase_child_Systems))
                                            .child(sysKey)
                                            .child(getString(R.string.firebase_child_Zones))
                                            .push()
                                            .getKey();


                                    hashMap.put(getString(R.string.hashmap_zoneKey), zoneKey);
                                    hashMap.put(getString(R.string.hashmap_zonePos), zonePos);
                                    hashMap.put(getString(R.string.hashmap_zoneId), tvNumber);
                                    hashMap.put(getString(R.string.hashmap_zoneName), tvName);
                                    hashMap.put(getString(R.string.hashmap_zoneImage), zoneImage.getImage());

                                    FirebaseDatabase.getInstance().getReference()
                                            .child(getString(R.string.firebase_path_Users))
                                            .child(userKey)
                                            .child(getString(R.string.firebase_path_Systems))
                                            .child(sysKey)
                                            .child(getString(R.string.firebase_child_Zones))
                                            .child(zoneKey)
                                            .setValue(hashMap);
                                }
                            });
                            InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                            mgr.hideSoftInputFromWindow(mZoneName.getWindowToken(), 0);

                            // When saved, Snackbar shows the message and then after finished showing it goes to previous activity.
                            // See https://stackoverflow.com/questions/34578375/how-to-wait-to-snackbar-i-want-to-know-when-it-is-closed
                            Snackbar.make(view, "Zone added successfully!", 1200)
                                    .addCallback(new Snackbar.Callback() {
                                        @Override
                                        public void onDismissed(Snackbar snackbar, int event) {
                                            super.onDismissed(snackbar, event);


                                            finish();
                                        }
                                    }).show();

                        }

                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // Snackbar
                            InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                            mgr.hideSoftInputFromWindow(mZoneName.getWindowToken(), 0);

                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                            mProgress.setProgress((int) progress);

                        }
                    });
        } else {
            // Snackbar "no file detected"
//            Toast.makeText(this, "Please select photo", Toast.LENGTH_LONG).show();

            String tvNumber;
            String tvName;
            int zonePos;

            tvNumber = mZoneNumber.getSelectedItem().toString();
            tvName = mZoneName.getText().toString();
            zonePos = mZoneNumber.getSelectedItemPosition();

            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
            String userKey = currentUser.getUid();
            // System key generated to uniquely identify System
            // See https://stackoverflow.com/questions/37094631/get-the-pushed-id-for-specific-value-in-firebase-android

            String sysKey = mSystemKey.getString(getString(R.string.key_system_key));
//            String sysAddress = mSystemName.getString("KEY_SYSTEM_NAME");

            HashMap hashMap = new HashMap();


            String zoneKey = FirebaseDatabase.getInstance().getReference()
                    .child(getString(R.string.firebase_path_Users))
                    .child(userKey)
                    .child(getString(R.string.firebase_child_Systems))
                    .child(sysKey)
                    .child(getString(R.string.firebase_child_Zones))
                    .push()
                    .getKey();


            hashMap.put(getString(R.string.hasmap_zoneKey), zoneKey);
            hashMap.put(getString(R.string.hashmap_zonePos), zonePos);
            hashMap.put(getString(R.string.hashmap_zoneId), tvNumber);
            hashMap.put(getString(R.string.hashmap_zoneName), tvName);
            hashMap.put(getString(R.string.hashmap_zoneImage), "");

            FirebaseDatabase.getInstance().getReference()
                    .child(getString(R.string.firebase_path_Users))
                    .child(userKey)
                    .child(getString(R.string.firebase_child_Systems))
                    .child(sysKey)
                    .child(getString(R.string.firebase_child_Zones))
                    .child(zoneKey)
                    .setValue(hashMap);
//
//            // used to back out of an activity
//        // See https://stackoverflow.com/questions/15393899/how-to-close-activity-and-go-back-to-previous-activity-in-android
//        final Intent intent = new Intent(this, HomepageActivity.class);
//        intent.putExtra("KEY_ZONE_KEY", zoneKey);
//        intent.putExtra("KEY_ZONE_POSITION", zonePosition);
//        intent.putExtra("KEY_ZONE_ID", tvNumber);
        InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        mgr.hideSoftInputFromWindow(mZoneName.getWindowToken(), 0);

        Snackbar.make(view, "Zone saved successfully!", 1200)
                .addCallback(new Snackbar.Callback() {
                    @Override
                    public void onDismissed(Snackbar snackbar, int event) {
                        super.onDismissed(snackbar, event);

//                        startActivity(intent);

                    }
                }).show();


            finish();

        }


    }


    private String getFileExtension(Uri uri) {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//        String text = parent.getItemAtPosition(position).toString();
//        Toast.makeText(parent.getContext(), text, Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }


}
