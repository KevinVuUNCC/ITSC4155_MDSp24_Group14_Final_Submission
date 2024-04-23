package edu.uncc.myapplication.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

import edu.uncc.myapplication.R;
import edu.uncc.myapplication.models.Recipe;
import edu.uncc.myapplication.models.RecipeRecyclerViewAdapter;
import edu.uncc.myapplication.databinding.FragmentRecipeBinding;

public class RecipeFragment extends Fragment implements RecipeRecyclerViewAdapter.IRecipeRecycler {

    final String TAG = "james";
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private ArrayList<Recipe> mRecipes = new ArrayList<Recipe>();
    RecyclerView recyclerView;
    LinearLayoutManager layoutManager;
    RecipeRecyclerViewAdapter adapter;

    public RecipeFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    FragmentRecipeBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentRecipeBinding.inflate(inflater,container,false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setImageFromFirebase();
        getRecipeCollection();
        Log.d(TAG, "onRecipeViewCreated: " + mRecipes);

        recyclerView = binding.recyclerView;
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        adapter = new RecipeRecyclerViewAdapter(mRecipes,this);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();


        binding.editTextSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                filterRecipes(s.toString());
            }
        });


        binding.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                recipeListener.gotoAddRecipe();

            }
        });

        binding.ProfileImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recipeListener.gotoAccountDetails();
            }
        });

    }

    private void filterRecipes(String searchText) {
        ArrayList<Recipe> filteredList = new ArrayList<>();

        for (Recipe recipe : mRecipes) {
            if (recipe.getName().toLowerCase().contains(searchText.toLowerCase())) {
                filteredList.add(recipe);
            }
        }

        adapter.filterList(filteredList);
    }


    void getRecipeCollection(){

        db.collection("recipes").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot task, @Nullable FirebaseFirestoreException error) {
                    Log.d(TAG, "onComplete: Query In SuccessFully");
                    mRecipes.clear();
                    for(QueryDocumentSnapshot document: task){
                        Log.d(TAG, "onComplete: " + document.getId());
                        Log.d(TAG, "onComplete: " + document.getData());
                        Recipe add = new Recipe();
                        add.setName(document.getString("name"));
                        add.setTime(document.getString("time"));
                        add.setDescription(document.getString("description"));
                        add.setUid(document.getString("uid"));
                        add.setUser(document.getString("user"));
                        add.setDocId(document.getId());

                        mRecipes.add(add);
                    }
                    adapter.notifyDataSetChanged();

            }
        });
    }

    private void setImageFromFirebase() {
        String userId = mAuth.getCurrentUser().getUid();
        StorageReference storageReference = FirebaseStorage.getInstance().getReference();
        String imagePath = "images/" + userId + "/userImage.jpg"; // Adjust this path as needed.
        StorageReference imageRef = storageReference.child(imagePath);

        Glide.with(RecipeFragment.this)
                .load(R.drawable.transparent) // Replace placeholder_image with your actual placeholder image resource
                .into(binding.ProfileImageView);

        imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                // Use Glide or Picasso to load the image
                Glide.with(RecipeFragment.this)
                        .load(uri)
                        .into(binding.ProfileImageView);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e(TAG, "Error fetching image: ", e);
                // Handle the case where the image is not found or error
                Glide.with(RecipeFragment.this)
                        .load(R.drawable.user) // Default user image
                        .into(binding.ProfileImageView);
            }
        });
    }

    RecipeListener recipeListener;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        recipeListener = (RecipeListener) context;
    }

    @Override
    public void goToRecipeDetails(Recipe recipe) {
        recipeListener.gotoRecipeDetails(recipe);
    }

    //TODO: The interface for the TasksFragment
    public interface RecipeListener{
        void gotoAddRecipe();
        void gotoRecipeDetails(Recipe recipe);

        void gotoAccountDetails();
    }

}