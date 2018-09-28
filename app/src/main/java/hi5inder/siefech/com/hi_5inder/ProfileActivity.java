package hi5inder.siefech.com.hi_5inder;

import android.content.DialogInterface;
import android.content.Intent;
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

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener{
    AlertDialog.Builder builder;
    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageRef = storage.getReference();
    StorageReference pathReference = storageRef.child("images/");
    private FirebaseAuth firebaseAuth;
    private Glide GlideApp;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

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

    }
}
