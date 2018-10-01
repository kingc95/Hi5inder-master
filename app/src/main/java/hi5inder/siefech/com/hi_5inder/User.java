package hi5inder.siefech.com.hi_5inder;

import com.google.firebase.firestore.IgnoreExtraProperties;

@IgnoreExtraProperties
public class User {

    public String username;
    public String radius;
    public String status;

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String username, String radius, String status) {
        this.username = username;
        this.radius = radius;
        this.status = status;
    }
}
