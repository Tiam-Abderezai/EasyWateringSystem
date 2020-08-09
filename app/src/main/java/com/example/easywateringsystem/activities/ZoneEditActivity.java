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

public class ZoneEditActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private static final int REQUEST_STORAGE_PERMISSION = 12;
    private static final int REQUEST_IMAGE_CAPTURE = 101;

    private Spinner mZoneNumber;
    private EditText mZoneName;
    private Bundle mSelectedZoneKey;
    private Bundle mSelectedZoneNumber;
    private Bundle mSelectedZoneName;
    private Bundle mSelectedZoneImage;
    private Bundle mSelectedZonePos;
    private Bundle mSystemName;
    private Bundle mSystemKey;
    private TextView mSystemTV;
    private ImageView mIV_ZoneImage;
    private DatabaseReference ref;
    private Snackbar snackbar;
    private RelativeLayout rel_lay_zone_crud;
    private ProgressDialog mProgress;

    private StorageReference mStoreImgRef;
    private DatabaseReference mDBImgRef;
    String imgPath = null;
    private Bitmap mResultsBitmap;
    private CameraExecutor mCamExecutor;

    boolean isTakePhoto = false;
    boolean isSelectPhoto = false;
    private String mTempPhotoPath;
    private Uri mImageUri = null;

//    private Snackbar snackbar;
//    private RelativeLayout rel_lay_zone_crud;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_zone);


        mSystemName = getIntent().getExtras();
        mSystemKey = getIntent().getExtras();
        mSelectedZoneName = getIntent().getExtras();
        mSelectedZoneImage = getIntent().getExtras();
        mSelectedZonePos = getIntent().getExtras();
        mSelectedZoneKey = getIntent().getExtras();

        mIV_ZoneImage = findViewById(R.id.activity_edit_zone_image);
        if (mSystemName != null) {
            String sysName = mSystemName.getString(getString(R.string.key_system_name));
            //The key argument here must match that used in the login activity
            mSystemTV = findViewById(R.id.activity_edit_sys_name);
            mSystemTV.setText(sysName);


        }

        String imageUrl = mSelectedZoneImage.getString(getString(R.string.key_zone_image));

        if (mSelectedZoneImage != null && !imageUrl.equals("")) {
            Picasso.with(this).load(imageUrl).fit().into(mIV_ZoneImage);
        } else {
            Picasso.with(this).load(R.drawable.splash_image).fit().into(mIV_ZoneImage);
        }
        String sysKey = mSystemKey.getString(getString(R.string.key_system_key));
        String zoneName = mSelectedZoneName.getString(getString(R.string.key_zone_name));

        mZoneNumber = findViewById(R.id.activity_edit_zone_number);
        mZoneName = findViewById(R.id.activity_edit_zone_name);

        rel_lay_zone_crud = (RelativeLayout) findViewById(R.id.layout_zone_edit);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.numbers, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mZoneNumber.setAdapter(adapter);

        mCamExecutor = new CameraExecutor();
        mProgress = new ProgressDialog(this);


        mZoneNumber.setOnItemSelectedListener(this);

        int zonePosition = mSelectedZonePos.getInt(getString(R.string.key_zone_position));
        mZoneNumber.setSelection(zonePosition);

        mZoneName.setHint(zoneName);

        mStoreImgRef = FirebaseStorage.getInstance().getReference("uploads");
        mDBImgRef = FirebaseDatabase.getInstance().getReference("uploads");

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        String userKey = currentUser.getUid();



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

            Picasso.with(this).load(mImageUri).into(mIV_ZoneImage);
            Toast.makeText(this, "Photo selected.", Toast.LENGTH_LONG).show();

            isSelectPhoto = false;
        }


    }



    public void saveZoneEdit(View view) throws InterruptedException {
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

                                    FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                                    String userKey = currentUser.getUid();
                                    // System key generated to uniquely identify System
                                    // See https://stackoverflow.com/questions/37094631/get-the-pushed-id-for-specific-value-in-firebase-android
                                    String zoneKey = mSelectedZoneKey.getString(getString(R.string.key_zone_key));
                                    String zoneNumber = mZoneNumber.getSelectedItem().toString();
                                    String zoneName = mZoneName.getText().toString();
                                    int zonePosition = mZoneNumber.getSelectedItemPosition();

                                    String sysKey = mSystemKey.getString(getString(R.string.key_system_key));

                                    HashMap hashMap = new HashMap();

                                    hashMap.put(getString(R.string.hashmap_zoneKey), zoneKey);
                                    hashMap.put(getString(R.string.hashmap_zonePos), zonePosition);
                                    hashMap.put(getString(R.string.hashmap_zoneId), zoneNumber);
                                    hashMap.put(getString(R.string.hashmap_zoneName), zoneName);
                                    hashMap.put(getString(R.string.hashmap_zoneImage), zoneImage.getImage());

                                    FirebaseDatabase.getInstance().getReference()
                                            .child(getString(R.string.firebase_path_Users))
                                            .child(userKey)
                                            .child(getString(R.string.firebase_child_Systems))
                                            .child(sysKey)
                                            .child(getString(R.string.firebase_child_Zones))
                                            .child(zoneKey)
                                            .updateChildren(hashMap);

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
                                            Intent intent = new Intent(ZoneEditActivity.this, HomepageActivity.class);
                                            startActivity(intent);
//                                            finish();
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
        }

        else {
            // Snackbar "no file detected"
//            Toast.makeText(this, "Please select photo", Toast.LENGTH_LONG).show();
    String imageUrl = mSelectedZoneImage.getString(getString(R.string.key_zone_image));

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
            String zoneKey = mSelectedZoneKey.getString(getString(R.string.key_zone_key));
            HashMap hashMap = new HashMap();




            hashMap.put(getString(R.string.hashmap_zoneKey), zoneKey);
            hashMap.put(getString(R.string.hashmap_zonePos), zonePos);
            hashMap.put(getString(R.string.hashmap_zoneId), tvNumber);
            hashMap.put(getString(R.string.hashmap_zoneName), tvName);
            hashMap.put(getString(R.string.hashmap_zoneImage), imageUrl);

            FirebaseDatabase.getInstance().getReference()
                    .child(getString(R.string.firebase_path_Users))
                    .child(userKey)
                    .child(getString(R.string.firebase_child_Systems))
                    .child(sysKey)
                    .child(getString(R.string.firebase_child_Zones))
                    .child(zoneKey)
                    .updateChildren(hashMap);
//                    .setValue(hashMap);
//
//            // used to back out of an activity
//        // See https://stackoverflow.com/questions/15393899/how-to-close-activity-and-go-back-to-previous-activity-in-android
//        final Intent intent = new Intent(this, HomepageActivity.class);

            InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            mgr.hideSoftInputFromWindow(mZoneName.getWindowToken(), 0);

            Snackbar.make(view, "Zone saved successfully!", 1200)
                    .addCallback(new Snackbar.Callback() {
                        @Override
                        public void onDismissed(Snackbar snackbar, int event) {
                            super.onDismissed(snackbar, event);


                            Intent intent = new Intent(ZoneEditActivity.this, HomepageActivity.class);
                            startActivity(intent);
                        }
                    }).show();

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
