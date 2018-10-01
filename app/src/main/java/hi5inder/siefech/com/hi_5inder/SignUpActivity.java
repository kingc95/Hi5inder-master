package hi5inder.siefech.com.hi_5inder;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener{

    //initialize views
    private Button signUpButton;
    private EditText emailText;
    private EditText passText;
    private TextView logInText;

    //firebase auth object
    private FirebaseAuth firebaseAuth;

    //progress dialog
    private ProgressDialog progressDialog;

    //Firebase DB
    FirebaseFirestore db = FirebaseFirestore.getInstance();


    String toReturn;
    int randomNum = (int) (100 * Math.random());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        //getting firebase auth object
        firebaseAuth = FirebaseAuth.getInstance();

        //if the objects getcurrentuser method is not null
        //means user is already logged in
        if (firebaseAuth.getCurrentUser() != null) {
            //close this activity
            finish();
            //opening profile activity
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
        }
        //initializing views
        emailText = (EditText) findViewById(R.id.emailEditTextSign);
        passText = (EditText) findViewById(R.id.passwordEditTextSign);
        signUpButton = (Button) findViewById(R.id.signUpButton);
        logInText = (TextView) findViewById(R.id.logInText);

        progressDialog = new ProgressDialog(this);

        //attaching click listener

        signUpButton.setOnClickListener(this);
        logInText.setOnClickListener(this);
    }

    private void registerUser(){

        //getting email and password from edit texts
        String email = emailText.getText().toString().trim();
        String password  = passText.getText().toString().trim();

        //checking if email and passwords are empty
        if(TextUtils.isEmpty(email)){
            Toast.makeText(this,"Please enter email",Toast.LENGTH_LONG).show();
            return;
        }

        if(TextUtils.isEmpty(password)){
            Toast.makeText(this,"Please enter password",Toast.LENGTH_LONG).show();
            return;
        }

        //if the email and password are not empty
        //displaying a progress dialog

        progressDialog.setMessage("Registering Please Wait...");
        progressDialog.show();

        //creating a new user
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        //checking if success
                        if(task.isSuccessful()){
                            Map<String, Object> user = new HashMap<>();
                            try {
                                user.put("username", getUsername());
                                user.put("status", " ");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            db.collection("users").document(firebaseAuth.getUid())
                                    .set(user, SetOptions.merge())
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            finish();
                                            startActivity(new Intent(getApplicationContext(), MainActivity.class));
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                        }
                                    });
                        }else{
                            //display some message here
                            Toast.makeText(SignUpActivity.this,"Registration Error",Toast.LENGTH_LONG).show();
                        }
                        progressDialog.dismiss();
                    }
                });

    }

    @Override
    public void onClick(View v) {

        if(v == signUpButton){
            registerUser();
        }

        if(v == logInText){
            finish();
            //open login activity when user taps on the already registered textview
            startActivity(new Intent(this, LoginActivity.class));
        }
    }
    public void onConfigurationChanged(Configuration newConfig)
    {
        super.onConfigurationChanged(newConfig);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    public String getUsername() throws JSONException {

        JSONObject obj = new JSONObject(loadJSONFromAsset());
        JSONArray userNames = obj.getJSONArray("names");
        JSONArray adj = obj.getJSONArray("adjectives");
        toReturn = (userNames.getString((int) (userNames.length() * Math.random())) + adj.getString((int) (adj.length() * Math.random())) + Integer.toString(randomNum));

        return toReturn;
    }
    public String loadJSONFromAsset() {
        String json = null;
        try {
            InputStream is = getAssets().open("randomUser.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }
}
