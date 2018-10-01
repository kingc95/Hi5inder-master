package hi5inder.siefech.com.hi_5inder;

import com.google.firebase.firestore.IgnoreExtraProperties;

@IgnoreExtraProperties
public class User {

    public String username;
    public Double radius;
    public String status;
    public int tempID;

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String username, Double radius, String status) {
        this.username = username;
        this.radius = radius;
        this.status = status;
    }

    public User(String username, String status) {
        this.username = username;
        this.status = status;
    }

    public User(int tempID) {
        this.tempID = tempID;
    }

    public User(Double radius) {
        this.radius = radius;
    }
}
