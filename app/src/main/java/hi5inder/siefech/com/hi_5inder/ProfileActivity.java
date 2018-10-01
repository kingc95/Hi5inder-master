package hi5inder.siefech.com.hi_5inder;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.provider.DocumentsContract;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.firestore.Source;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.widget.Button;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener {
    AlertDialog.Builder builder;
    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageRef = storage.getReference();
    StorageReference pathReference = storageRef.child("images/");
    private FirebaseAuth firebaseAuth;
    private Glide GlideApp;
    private ImageView profilePic;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int PERMISSION_REQUEST_CODE = 200;
    Button pic;
    Button submitProfile;
    private ByteArrayOutputStream baosSubmit;
    boolean picUpdate = false;
    private EditText userName;
    private EditText status;
    private User user;

    //Firebase DB
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        //getting firebase auth object
        firebaseAuth = FirebaseAuth.getInstance();

        pic = findViewById(R.id.changePic);
        profilePic = findViewById(R.id.profileImage);
        submitProfile = findViewById(R.id.submitEditProfile);

        userName = findViewById(R.id.userNameText);
        status = findViewById(R.id.statusText);

        pic.setOnClickListener(this);
        submitProfile.setOnClickListener(this);

        loadWithGlide();

        //get current username and status
        DocumentReference userRef = db.collection("users").document(firebaseAuth.getUid());
        Source source = Source.DEFAULT;

        userRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                user = documentSnapshot.toObject(User.class);
                userName.setText(user.username);
                status.setText(user.status);
            }
        });


    }

    public void loadWithGlide() {
        // [START storage_load_with_glide]
        // Reference to an image file in Cloud Storage
        StorageReference pictureRef = pathReference.child(firebaseAuth.getUid());

        // ImageView in your Activity
        ImageView imageView = findViewById(R.id.profileImage);

        // Download directly from StorageReference using Glide
        // (See MyAppGlideModule for Loader registration)
        GlideApp.with(this /* context */)
                .load(pictureRef)
                .into(imageView);
        // [END storage_load_with_glide]

    }

    @Override
    public void onBackPressed() {

        builder = new AlertDialog.Builder(this, R.style.dialog);
        builder.setMessage("You have unsaved profile settings! You must use the submit button to save profile")
                .setTitle("Warning: unsaved profile settings!")
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User clicked OK button
                        finish();
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    }
                })
                .setNegativeButton("Go Back to Profile", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog

                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    @Override
    public void onClick(View v) {
        if (v == pic) {
            if (checkPermission()) {
                Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePicture.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(takePicture, REQUEST_IMAGE_CAPTURE);
                }
            } else {
                requestPermission();
            }
        }
        else if (v == submitProfile){

            Map<String, Object> user = new HashMap<>();
            user.put("status", status.getText().toString());
            user.put("username", userName.getText().toString());

            db.collection("users").document(firebaseAuth.getUid())
                    .set(user, SetOptions.merge())
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(getApplicationContext(), "Profile Saved!", Toast.LENGTH_LONG).show();
                            finish();
                            startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getApplicationContext(), "Profile NOT Saved!", Toast.LENGTH_LONG).show();
                        }
                    });

            if (picUpdate){
                byte[] dataByte = baosSubmit.toByteArray();
                StorageReference picUploadRef = pathReference.child(firebaseAuth.getUid());
                UploadTask uploadTask = picUploadRef.putBytes(dataByte);
                uploadTask.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle unsuccessful uploads
                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                        // ...
                    }
                });
            }
            else{
                finish();
                startActivity(new Intent(this, MainActivity.class));
            }

        }

    }

    private void requestPermission() {

        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.CAMERA},
                PERMISSION_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");

            if (imageBitmap.getWidth() > imageBitmap.getHeight()) {
                Matrix matrix = new Matrix();
                matrix.postRotate(90);
                imageBitmap = Bitmap.createBitmap(imageBitmap , 0, 0, imageBitmap.getWidth(), imageBitmap.getHeight(), matrix, true);
            }

            profilePic.setImageBitmap(imageBitmap);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            imageBitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);

            baosSubmit = baos;
            picUpdate = true;

        }
    }

    private boolean checkPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            return false;
        }
        return true;
    }
    public void onConfigurationChanged(Configuration newConfig)
    {
        super.onConfigurationChanged(newConfig);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }
}
