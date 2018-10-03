package hi5inder.siefech.com.hi_5inder;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.RequestManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

/**
 * Provide views to RecyclerView with data from mDataSet.
 */
public class MyAdapter extends RecyclerView.Adapter<MyAdapter.PersonViewHolder> {
    private final RequestManager glide;

    MyAdapter(RequestManager glide) {
        this.glide = glide;
    }
    public static class PersonViewHolder extends RecyclerView.ViewHolder {
    CardView cv;
    TextView personName;
    TextView personAge;
    ImageView personPhoto;


    PersonViewHolder(View itemView) {
        super(itemView);
        cv = (CardView)itemView.findViewById(R.id.cv);
        personName = (TextView)itemView.findViewById(R.id.person_name);
        personAge = (TextView)itemView.findViewById(R.id.person_age);
        personPhoto = (ImageView)itemView.findViewById(R.id.person_photo);
    }
}

    List<User> persons;

    MyAdapter(RequestManager glide, List<User> persons){
        this.glide = glide;
        this.persons = persons;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public PersonViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item, viewGroup, false);
        PersonViewHolder pvh = new PersonViewHolder(v);
        return pvh;
    }

    @Override
    public void onBindViewHolder(PersonViewHolder personViewHolder, int i) {
        personViewHolder.personName.setText(persons.get(i).username);
        personViewHolder.personAge.setText(persons.get(i).status);

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        StorageReference pathReference = storageRef.child("images/");
        FirebaseAuth firebaseAuth;
        //getting firebase auth object
        firebaseAuth = FirebaseAuth.getInstance();


        // [START storage_load_with_glide]
        // Reference to an image file in Cloud Storage
        StorageReference pictureRef = pathReference.child(persons.get(i).uid);


        // Download directly from StorageReference using Glide
        // (See MyAppGlideModule for Loader registration)
        glide.load(pictureRef).into(personViewHolder.personPhoto);
        // [END storage_load_with_glide]

    }

    @Override
    public int getItemCount() {
        return persons.size();
    }
}

