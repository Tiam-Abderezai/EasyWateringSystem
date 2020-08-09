package com.example.easywateringsystem.activities;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.example.easywateringsystem.R;
import com.example.easywateringsystem.models.User;
import com.example.easywateringsystem.utils.BitmapUtils;
import com.example.easywateringsystem.utils.CameraExecutor;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.example.easywateringsystem.utils.BitmapUtils.FILE_PROVIDER_AUTHORITY;

public class UserActivity extends AppCompatActivity {
    boolean isTakePhoto = false;
    boolean isSelectPhoto = false;
    private static final int REQUEST_STORAGE_PERMISSION = 12;
    private static final int REQUEST_IMAGE_CAPTURE = 101;
    private ProgressDialog mProgress;
    private String mTempPhotoPath;
    private Bitmap mResultsBitmap;
    private CameraExecutor mCamExecutor;
    private Uri mImageUri = null;
    private ImageView mUserImage;
    private StorageReference mStoreImgRef;
    private DatabaseReference mDBImgRef;
    private StorageReference mStorage;
    private List<String> mImageUrlExtra;
    private Bundle mFirstNameBundle;
    private Bundle mLastNameBundle;
    private Bundle mEmailBundle;
    private Bundle mImageBundle;
    String mNameFirst;
    String mNameLast;
    String mEmail;
    String mImage;
    private DatabaseReference ref = FirebaseDatabase.getInstance().getReference();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        mUserImage = findViewById(R.id.profile_picture);
        mStorage = FirebaseStorage.getInstance().getReference();
//        rel_lay_zone_crud = (RelativeLayout) findViewById(R.id.layout_zone_crud);
        mStoreImgRef = FirebaseStorage.getInstance().getReference("uploads");
        mDBImgRef = FirebaseDatabase.getInstance().getReference("uploads");
        mCamExecutor = new CameraExecutor();
        mProgress = new ProgressDialog(this);

        mFirstNameBundle = getIntent().getExtras();
        mLastNameBundle = getIntent().getExtras();
        mEmailBundle = getIntent().getExtras();
        mImageBundle = getIntent().getExtras();

        mNameFirst = mFirstNameBundle.getString(getString(R.string.key_user_first));
        mNameLast = mLastNameBundle.getString(getString(R.string.key_user_last));
        mEmail = mEmailBundle.getString(getString(R.string.key_user_email));
        mImage = mImageBundle.getString(getString(R.string.key_user_image));
        if (mImageBundle != null && mImage != null && !mImage.equals("")) {
            mImage = mImageBundle.getString(getString(R.string.key_user_image));
            mUserImage = findViewById(R.id.profile_picture);
            Picasso.with(UserActivity.this)
                    .load(mImage)
                    .placeholder(R.drawable.profile_pic)
                    .fit()
                    .centerCrop()
                    .into(mUserImage);
        }

        mImageUrlExtra = new ArrayList<String>();

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
                // Add the URI so the camera can store the image
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
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

            Picasso.with(this).load(mImageUri).into(mUserImage);
            Toast.makeText(this, "Photo selected.", Toast.LENGTH_LONG).show();

            isSelectPhoto = false;
        }


    }

    private String getFileExtension(Uri uri) {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    public void savePhoto(View view) throws InterruptedException {
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
                                    User userImage = new User(downloadUri.toString());

                                    FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                                    String userKey = currentUser.getUid();

                                    HashMap hashMap = new HashMap();
                                    hashMap.put(getString(R.string.hashmap_email_2), mEmail);
                                    hashMap.put(getString(R.string.hashmap_nameFirst_2), mNameFirst);
                                    hashMap.put(getString(R.string.hashmap_nameLast_2), mNameLast);
                                    hashMap.put(getString(R.string.hashmap_image_2), userImage.getImage());
                                    // mImageUrl below is used to pass the image url to the HomeActivity
                                    mImageUrlExtra.add(userImage.getImage());

                                    FirebaseDatabase.getInstance().getReference()
                                            .child(getString(R.string.firebase_path_Users))
                                            .child(userKey)
                                            .child(getString(R.string.firebase_child_userData_2))
                                            .updateChildren(hashMap);
                                }
                            });
//                            InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//                            mgr.hideSoftInputFromWindow(mZoneName.getWindowToken(), 0);

                            // When saved, Snackbar shows the message and then after finished showing it goes to previous activity.
                            // See https://stackoverflow.com/questions/34578375/how-to-wait-to-snackbar-i-want-to-know-when-it-is-closed
                            Snackbar.make(view, "Zone added successfully!", 1200)
                                    .addCallback(new Snackbar.Callback() {
                                        @Override
                                        public void onDismissed(Snackbar snackbar, int event) {
                                            super.onDismissed(snackbar, event);

                                            Intent intent = new Intent(UserActivity.this, HomepageActivity.class);
                                            startActivity(intent);
                                        }
                                    }).show();

                        }

                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // Snackbar
//                            InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//                            mgr.hideSoftInputFromWindow(mZoneName.getWindowToken(), 0);

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
            Toast.makeText(this, "Please select photo", Toast.LENGTH_LONG).show();
        }


    }

    public void deletePhoto(View view) {

        HashMap hashMap = new HashMap();

        hashMap.put(getString(R.string.hashmap_email_2), mEmail);
        hashMap.put(getString(R.string.hashmap_nameFirst_2), mNameFirst);
        hashMap.put(getString(R.string.hashmap_nameLast_2), mNameLast);
        hashMap.put(getString(R.string.hashmap_image_2), "");


        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        String userKey = currentUser.getUid();
        // See https://stackoverflow.com/questions/37094631/get-the-pushed-id-for-specific-value-in-firebase-android

        ref = FirebaseDatabase.getInstance()
                .getReference(getString(R.string.firebase_path_Users))
                .child(userKey)
                .child(getString(R.string.firebase_child_userData_3));
                ref.setValue(hashMap);

        // When saved, Snackbar shows the message and then after finished showing it goes to previous activity.
        // See https://stackoverflow.com/questions/34578375/how-to-wait-to-snackbar-i-want-to-know-when-it-is-closed
        Snackbar.make(view, "Profile picture deleted!", 1200)
                .addCallback(new Snackbar.Callback() {
                    @Override
                    public void onDismissed(Snackbar snackbar, int event) {
                        super.onDismissed(snackbar, event);

                    }

                }).show();
        Intent intent = new Intent(UserActivity.this, HomepageActivity.class);
        startActivity(intent);
    }

}
