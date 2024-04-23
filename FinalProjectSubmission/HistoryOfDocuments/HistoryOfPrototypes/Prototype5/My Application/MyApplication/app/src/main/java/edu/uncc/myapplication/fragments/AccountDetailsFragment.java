package edu.uncc.myapplication.fragments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

import edu.uncc.myapplication.R;
import edu.uncc.myapplication.databinding.FragmentAccountDetailsBinding;
import edu.uncc.myapplication.models.Recipe;
import edu.uncc.myapplication.models.RecipeRecyclerViewAdapter;

public class AccountDetailsFragment extends Fragment implements RecipeRecyclerViewAdapter.IRecipeRecycler {

    final String TAG = "james";
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseStorage storage = FirebaseStorage.getInstance();
    private ArrayList<Recipe> mRecipes = new ArrayList<Recipe>();

    RecyclerView recyclerView;
    LinearLayoutManager layoutManager;
    RecipeRecyclerViewAdapter adapter;
    private static String userID;

    private static String userName;

    private static String uid = null;
    Uri imageURI;

    public AccountDetailsFragment() {
        // Required empty public constructor
    }


    public static AccountDetailsFragment newInstance(String param1, String param2) {
        AccountDetailsFragment fragment = new AccountDetailsFragment();
        Bundle args = new Bundle();
        if (param1 != null){
        userID = param1.toString();
        userName = param2.toString();
        fragment.setArguments(args);}
        else
        {
            userID = null;
        }
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    FragmentAccountDetailsBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentAccountDetailsBinding.inflate(inflater,container,false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if(userID == null) {
            binding.textViewAccountDetailUser.setText(mAuth.getCurrentUser().getDisplayName());
            userID = mAuth.getCurrentUser().getUid().toString();
            binding.buttonLogout.setVisibility(View.VISIBLE);
        }
        else {
            binding.textViewAccountDetailUser.setText(userName);
            binding.buttonLogout.setVisibility(View.GONE);
        }
        binding.imageAccountDetailImage.setVisibility(View.GONE);
        setImageFromFirebase();


        getRecipeCollection(userID);
        Log.d(TAG, "onRecipeViewCreated: " + mRecipes);

        recyclerView = binding.RecyclerMyPosts;
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        adapter = new RecipeRecyclerViewAdapter(mRecipes,this);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        binding.imageAccountDetailImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(userID.equals(mAuth.getCurrentUser().getUid())){selectImage();}
            }
        });

        binding.buttonLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                accountDetailListener.logout();
            }
        });

        binding.buttonRecipeFeed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                accountDetailListener.backToRecipe();
            }
        });
    }

    private void selectImage(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,100);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 100 && data != null){
            imageURI = data.getData();
            uploadImage();
        }
    }

    private void uploadImage() {
        if (imageURI != null) {
            String userId = mAuth.getCurrentUser().getUid();

            // Reference to the storage location
            StorageReference storageReference = FirebaseStorage.getInstance().getReference();

            // Path to the user's image
            String imagePath = "images/" + userId + "/userImage.jpg";
            final StorageReference fileRef = storageReference.child(imagePath);

            // Check if image already exists
            fileRef.getDownloadUrl().addOnSuccessListener(uri -> {
                // If image exists, delete it first
                fileRef.delete().addOnSuccessListener(aVoid -> {
                    // Upload new image
                    uploadNewImage(fileRef);
                }).addOnFailureListener(exception -> {
                    Log.e(TAG, "Failed to delete existing image: " + exception.getMessage());
                });
            }).addOnFailureListener(exception -> {
                // If image doesn't exist, directly upload new image
                uploadNewImage(fileRef);
            });
        } else {
            Log.e(TAG, "No image selected");
        }
    }

    private void uploadNewImage(StorageReference fileRef) {
        // Upload new image
        fileRef.putFile(imageURI).addOnSuccessListener(taskSnapshot -> {
            // Image upload successful
            fileRef.getDownloadUrl().addOnSuccessListener(uri -> {
                String imageUrl = uri.toString();
                Log.d(TAG, "Upload successful, Image URL: " + imageUrl);
                setImageFromFirebase();
            });
        }).addOnFailureListener(e -> {
            Log.e(TAG, "Upload failed: " + e.toString());
        });
    }

    private void setImageFromFirebase() {
        String userId = mAuth.getCurrentUser().getUid();
        StorageReference storageReference = FirebaseStorage.getInstance().getReference();
        String imagePath = "images/" + userId + "/userImage.jpg"; // Adjust this path as needed.
        StorageReference imageRef = storageReference.child(imagePath);

        Glide.with(AccountDetailsFragment.this)
                .load(R.drawable.transparent) // Replace placeholder_image with your actual placeholder image resource
                .into(binding.imageAccountDetailImage);

        imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                // Use Glide or Picasso to load the image
                Glide.with(AccountDetailsFragment.this)
                        .load(uri)
                        .into(binding.imageAccountDetailImage);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e(TAG, "Error fetching image: ", e);
                // Handle the case where the image is not found or error
                Glide.with(AccountDetailsFragment.this)
                        .load(R.drawable.user) // Default user image
                        .into(binding.imageAccountDetailImage);
            }
        });
        binding.imageAccountDetailImage.setVisibility(View.VISIBLE);
    }


    void getRecipeCollection(String userID){
        Log.d(TAG, "onComplete: Query In SuccessFully" + userID);
        db.collection("recipes")
                .whereEqualTo("uid", userID)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()){
                            Log.d(TAG, "onComplete: Query In SuccessFully");
                            mRecipes.clear();
                            for(QueryDocumentSnapshot document: task.getResult()){
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
                        } else {
                            Log.d(TAG, "onComplete: Error");
                            Log.d(TAG, "onComplete: Error" + task.getException().getMessage());
                        }
                    }
                });
    }


    AccountDetailListener accountDetailListener;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        accountDetailListener = (AccountDetailListener) context;
    }

    @Override
    public void goToRecipeDetails(Recipe recipe) {
        accountDetailListener.gotoRecipeDetails(recipe);
    }

    public interface AccountDetailListener{
        void backToRecipe();
        void gotoRecipeDetails(Recipe recipe);
        void logout();
    }

}