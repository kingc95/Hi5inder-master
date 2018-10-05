package hi5inder.siefech.com.hi_5inder;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.firestore.Source;

import org.imperiumlabs.geofirestore.GeoFirestore;
import org.imperiumlabs.geofirestore.GeoQuery;
import org.imperiumlabs.geofirestore.GeoQueryEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.location.LocationManager.GPS_PROVIDER;

public class SearchActivity extends AppCompatActivity implements LocationListener, GeoQueryEventListener{

    //Firebase DB
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    private RecyclerView rv;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    User user;
    private List<User> persons;
    String curUserName;
    String curUserStatus;


    CollectionReference geoFirestoreRef = FirebaseFirestore.getInstance().collection("geoFire");
    GeoFirestore geoFirestore = new GeoFirestore(geoFirestoreRef);

    private static final int PERMISSIONS_REQUEST = 100;
    private FirebaseAuth firebaseAuth;
    private LocationManager locationManager;
    private static final long MIN_TIME = 10;
    private static final float MIN_DISTANCE = 100;
    private Location location;

    private GeoQuery geoQuery;

    private Double radius;

    private GeoPoint QUERY_CENTER = new GeoPoint(36.963817, -122.018284);

    Glide glide;



    private double latitude;
    private double longitude;

    private int counter = 0;
    Map<String, Object> userMap = new HashMap<>();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        rv = (RecyclerView)findViewById(R.id.contentRecycler);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        rv.setLayoutManager(llm);

        persons = new ArrayList<>();

        //initializing firebase authentication object
        firebaseAuth = FirebaseAuth.getInstance();

        //if the user is not logged in
        //that means current user will return null
        if (firebaseAuth.getCurrentUser() == null) {
            //closing this activity
            finish();
            //starting login activity
            startActivity(new Intent(this, MainActivity.class));
        }

        //getting current user
        final FirebaseUser CurUser = firebaseAuth.getCurrentUser();

        LocationManager lm = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (!lm.isProviderEnabled(GPS_PROVIDER)) {
        }

        //Check whether this app has access to the location permission//

        int permission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);

        //If the location permission has been granted, then start the TrackerService//

        if (permission == PackageManager.PERMISSION_GRANTED) {
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME, MIN_DISTANCE, this);
            Toast.makeText(this, "GPS Enabled", Toast.LENGTH_SHORT).show();
        } else {

            //If the app doesn’t currently have access to the user’s location, then request access//

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST);
        }


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        location = locationManager.getLastKnownLocation(GPS_PROVIDER);
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        final CollectionReference ref = database.collection("geoFire");
        GeoFirestore geoFire = new GeoFirestore(ref);
        geoFire.setLocation(firebaseAuth.getUid(), new GeoPoint(location.getLatitude(), location.getLongitude()), new GeoFirestore.CompletionListener() {
            @Override
            public void onComplete(Exception e) {

            }
        });
        QUERY_CENTER = new GeoPoint(location.getLatitude(), location.getLongitude());
        geoQuery = geoFirestore.queryAtLocation(QUERY_CENTER, 1.6);
        geoQuery.addGeoQueryEventListener(this);


    }
    @Override
    public void onBackPressed() {

        finish();
        startActivity(new Intent(getApplicationContext(), MainActivity.class));
    }


    @Override
    public void onLocationChanged(Location location) {
        latitude = location.getLatitude();
        longitude = location.getLongitude();
        QUERY_CENTER =  new GeoPoint(latitude, longitude);
        // Push your location to FireBase
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        final CollectionReference ref = database.collection("geoFire");
        GeoFirestore geoFire = new GeoFirestore(ref);
        geoFire.setLocation(firebaseAuth.getUid(), new GeoPoint(latitude, longitude), new GeoFirestore.CompletionListener() {
            @Override
            public void onComplete(Exception e) {

            }
        });

        //get current username and status
        DocumentReference userRef = db.collection("users").document(firebaseAuth.getUid());
        Source source = Source.DEFAULT;

        userRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                user = documentSnapshot.toObject(User.class);
                radius = user.radius;
            }
        });
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
    private void initializeAdapter(){
        MyAdapter adapter = new MyAdapter(Glide.with(this), persons);
        rv.setAdapter(adapter);
    }


    @Override
    public void onKeyEntered(String s, GeoPoint geoPoint) {
        System.out.println(String.format("Document %s entered the search area at [%f,%f]", s, location.getLatitude(), location.getLongitude()));
        DocumentReference userRef1 = db.collection("users").document(s);
        final String uidS = s;
        userRef1.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                user = documentSnapshot.toObject(User.class);
                curUserName = user.username;
                curUserStatus = user.status;
                System.out.println(String.format("Username %s entered and status is %s", curUserName, curUserStatus));
                persons.add(new User(curUserName, curUserStatus, uidS));
                userMap.put("tempID", persons.size() -1);

                db.collection("users").document(uidS)
                        .set(userMap, SetOptions.merge())
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {

                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {

                            }
                        });
                initializeAdapter();
            }
        });

        counter += 1;


    }

    @Override
    public void onKeyExited(String s) {
        for (int i = 0; i < persons.size(); i++){
            if (persons.get(i).uid.equals(s)){
                System.out.println(String.format("Uid %s exited and was removed at index %d", s, i));
                persons.remove(i);
            }
        }
        System.out.println(String.format("Uid %s exited", s));
        initializeAdapter();

    }

    @Override
    public void onKeyMoved(String s, GeoPoint geoPoint) {

    }

    @Override
    public void onGeoQueryReady() {

    }

    @Override
    public void onGeoQueryError(Exception e) {

    }
}
