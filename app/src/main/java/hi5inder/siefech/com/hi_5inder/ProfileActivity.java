package hi5inder.siefech.com.hi_5inder;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
<<<<<<< HEAD
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
=======
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
>>>>>>> e146314c819e071b37780de0474357c573eb87a0

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener {
    AlertDialog.Builder builder;
<<<<<<< HEAD
    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageRef = storage.getReference();
    StorageReference pathReference = storageRef.child("images/");
    private FirebaseAuth firebaseAuth;
    private Glide GlideApp;
=======
    private ImageView profilePic;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int PERMISSION_REQUEST_CODE = 200;
    Button pic;
>>>>>>> e146314c819e071b37780de0474357c573eb87a0


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
<<<<<<< HEAD

        //getting firebase auth object
        firebaseAuth = FirebaseAuth.getInstance();

        loadWithGlide();
    }

    public void loadWithGlide() {
        // [START storage_load_with_glide]
        // Reference to an image file in Cloud Storage
        StorageReference pictureRef = pathReference.child(firebaseAuth.getUid() + ".png");

        // ImageView in your Activity
        ImageView imageView = findViewById(R.id.profileImage);

        // Download directly from StorageReference using Glide
        // (See MyAppGlideModule for Loader registration)
        GlideApp.with(this /* context */)
                .load(pictureRef)
                .into(imageView);
        // [END storage_load_with_glide]
=======
        pic = findViewById(R.id.changePic);
        profilePic = findViewById(R.id.profileImage);
        pic.setOnClickListener(this);
>>>>>>> e146314c819e071b37780de0474357c573eb87a0
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
            profilePic.setImageBitmap(imageBitmap);
            profilePic.setRotation(90);
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
}
