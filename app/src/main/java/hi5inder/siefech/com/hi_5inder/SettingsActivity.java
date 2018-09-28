package hi5inder.siefech.com.hi_5inder;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;

public class SettingsActivity extends AppCompatActivity implements View.OnClickListener{

    private EditText radius;
    private Button applySetting;
    private Switch randomUsername;
    AlertDialog.Builder builder;

    //Firebase DB
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    //firebase auth object
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        //getting firebase auth object
        firebaseAuth = FirebaseAuth.getInstance();

        radius = (EditText) findViewById(R.id.searchRadiusText);
        applySetting = (Button) findViewById(R.id.applySettingsbutton);
        randomUsername = (Switch) findViewById(R.id.randomUsernameSwitch);

        applySetting.setOnClickListener(this);
    }

    @Override
    public void onBackPressed() {

       builder = new AlertDialog.Builder(this, R.style.dialog);
        builder.setMessage("You have unsaved settings! You must use the apply settings button to save settings")
        .setTitle("Warning: unsaved settings!")
                .setPositiveButton("Ignore Settings", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User clicked OK button
                        finish();
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    }
                })
                .setNegativeButton("Go Back to Settings", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog

                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    @Override
    public void onClick(View v) {
        if (v == applySetting){
            Map<String, Object> user = new HashMap<>();
            user.put("radius", radius.getText().toString());
            user.put("randomUser", randomUsername.isChecked());

            db.collection("users").document(firebaseAuth.getUid())
                    .set(user, SetOptions.merge())
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(getApplicationContext(), "Settings Saved!", Toast.LENGTH_LONG).show();
                            finish();
                            startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getApplicationContext(), "Settings NOT Saved!", Toast.LENGTH_LONG).show();
                        }
                    });
        }

    }
    public void onConfigurationChanged(Configuration newConfig)
    {
        super.onConfigurationChanged(newConfig);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }
}
