package edu.uncc.myapplication.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.UUID;

import edu.uncc.myapplication.R;
import edu.uncc.myapplication.models.Recipe;
import edu.uncc.myapplication.databinding.FragmentAddRecipeBinding;

public class AddRecipeFragment extends Fragment {

    private FirebaseAuth mAuth;
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";


    private static final String ARG_RECIPE = "RECIPE";
    private String mParam1;
    private String mParam2;

    private Uri imageURI;

    private Recipe recipe;

    StorageReference storageReference = FirebaseStorage.getInstance().getReference();
    public AddRecipeFragment() {
        // Required empty public constructor
    }

    public static AddRecipeFragment newInstance(Recipe data, String param1, String param2) {
        AddRecipeFragment fragment = new AddRecipeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        args.putSerializable(ARG_RECIPE, data);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (ARG_RECIPE != null)
        {
            recipe = (Recipe) getArguments().getSerializable(ARG_RECIPE);
        }
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    FragmentAddRecipeBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentAddRecipeBinding.inflate(inflater,container,false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        if (recipe != null) {GetRecipeDetails(recipe);}
        super.onViewCreated(view, savedInstanceState);

        binding.buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addRecipeListener.backToRecipe();
            }
        });

        binding.imageViewRecipe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });


        binding.button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(binding.editTextTitle.getText().equals(null) || binding.editTextTitle.getText().toString().equals("")){
                    Toast.makeText(getActivity(),"No Title Was Inputted", Toast.LENGTH_SHORT).show();
                }
                else if(binding.editTextTime.getText().equals(null) || binding.editTextTime.getText().toString().equals("")){
                    Toast.makeText(getActivity(),"No Time Was Inputted", Toast.LENGTH_SHORT).show();
                }
                else if(binding.editTextTextMultiLine.getText().equals(null) || binding.editTextTextMultiLine.getText().toString().equals("")){
                    Toast.makeText(getActivity(),"No Description Was Inputted", Toast.LENGTH_SHORT).show();
                }
                else{

                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    mAuth = FirebaseAuth.getInstance();

                    String displayName = mAuth.getCurrentUser().getDisplayName();
                    String uidPut = mAuth.getUid();
                    Timestamp time = Timestamp.now();

                    HashMap<String, Object> recipe = new HashMap<>();
                    recipe.put("user", displayName);
                    recipe.put("name", binding.editTextTitle.getText().toString());
                    recipe.put("description",binding.editTextTextMultiLine.getText().toString());
                    recipe.put("time",binding.editTextTime.getText().toString());
                    recipe.put("uid",uidPut);
                    recipe.put("timeStamp",time);

                    db.collection("recipes").add(recipe)
                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                @Override
                                public void onSuccess(DocumentReference documentReference) {
                                    String documentId = documentReference.getId(); // Get the document ID
                                    uploadImage(documentId); // Call uploadImage with the document ID
                                }
                            });
                }

            }
        });
    }

    private void GetRecipeDetails(Recipe recipe){
        binding.EditRecipeTitle.setText(recipe.getName());
        binding.editTextTitle.setText(recipe.getName());
        binding.editTextTime.setText(recipe.getTime());
        binding.editTextTextMultiLine.setText(recipe.getDescription());

        String docId = recipe.getDocId();
        String imagePath = "images/" + docId + ".jpg"; // Adjust this path as needed.
        StorageReference imageRef = storageReference.child(imagePath);

        Glide.with(binding.imageViewRecipe.getContext())
                .load(R.drawable.transparent) // Placeholder image
                .into(binding.imageViewRecipe);

        imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                // Use Glide or Picasso to load the image
                Glide.with(binding.imageViewRecipe.getContext())
                        .load(uri)
                        .into(binding.imageViewRecipe);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("james", "Error fetching image: ", e);
                // Handle the case where the image is not found or error
                // Set a default placeholder image
                Glide.with(binding.imageViewRecipe.getContext())
                        .load(R.drawable.pb_j_hero) // Default placeholder image
                        .into(binding.imageViewRecipe);
            }
        });
    }

    private void uploadImage(String documentId) {
        if (imageURI != null) {
            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference storageRef = storage.getReference();

            // Create a reference to "images/{documentId}.jpg"
            StorageReference imageRef = storageRef.child("images/" + documentId + ".jpg");

            // Upload the file to Firebase Storage
            imageRef.putFile(imageURI)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            // Image uploaded successfully
                            addRecipeListener.SubmitRecipe();
                            Toast.makeText(getActivity(), "Post Created!", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // Handle unsuccessful uploads
                            addRecipeListener.SubmitRecipe();
                            Toast.makeText(getActivity(), "Post Create but Image failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            addRecipeListener.SubmitRecipe();
        }
    }


    private void selectImage(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, 100);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            imageURI = data.getData(); // Store the selected image URI
            binding.imageViewRecipe.setImageURI(imageURI);
        }
    }




    AddRecipeListener addRecipeListener;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            addRecipeListener = (AddRecipeListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement EditRecipeListener");
        }
    }


    //TODO: The interface for the TasksFragment
    public interface AddRecipeListener{
        void backToRecipe();
        void SubmitRecipe();
    }

}