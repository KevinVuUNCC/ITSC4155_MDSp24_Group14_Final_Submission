package edu.uncc.myapplication.fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;

import java.util.ArrayList;
import java.util.HashMap;

import edu.uncc.myapplication.models.Recipe;
import edu.uncc.myapplication.databinding.FragmentRecipeDetailsBinding;
import edu.uncc.myapplication.models.RecipeRecyclerViewAdapter;
import edu.uncc.myapplication.models.Review;
import edu.uncc.myapplication.models.ReviewRecyclerViewAdapter;

public class RecipeDetailsFragment extends Fragment implements ReviewRecyclerViewAdapter.IReviewRecycler {

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    final String TAG = "james";
    ArrayList<Review> mReviews = new ArrayList<>();
    private static final String ARG_RECIPE = "RECIPE";
    RecyclerView recyclerView;
    LinearLayoutManager layoutManager;
    ReviewRecyclerViewAdapter adapter;

    private Recipe recipe;

    public RecipeDetailsFragment() {
        // Required empty public constructor
    }

    public static RecipeDetailsFragment newInstance(Recipe data) {
        RecipeDetailsFragment fragment = new RecipeDetailsFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_RECIPE, data);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            recipe = (Recipe) getArguments().getSerializable(ARG_RECIPE);
        }
    }

    FragmentRecipeDetailsBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentRecipeDetailsBinding.inflate(inflater,container,false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.ReviewRecyclerView.setVisibility(View.GONE);

        if (recipe.getUid().equals(mAuth.getCurrentUser().getUid())) {
            binding.buttonDelete.setVisibility(View.VISIBLE);
        } else {
            binding.buttonDelete.setVisibility(View.GONE);
        }

        binding.textViewDetailTitle.setText(recipe.getName());
        binding.textViewDetailTime.setText(recipe.getTime());
        binding.textViewDetailUser.setText("Recipe Created By: " + recipe.getUser());
        binding.textViewDetailDescription.setText(recipe.getDescription());

        getReviewsCollection();

        Log.d(TAG, "Review Collection: " + mReviews);

        recyclerView = binding.ReviewRecyclerView;
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        adapter = new ReviewRecyclerViewAdapter(mReviews,this);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        binding.SubmitReviewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String reviewText = binding.editReviewText.getText().toString();
                if(reviewText.isEmpty()){
                    Toast.makeText(getActivity(), "Review cannot be empty", Toast.LENGTH_SHORT).show();
                } else {

                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    mAuth = FirebaseAuth.getInstance();
                    String displayName = mAuth.getCurrentUser().getDisplayName();
                    String uidPut = mAuth.getUid();

                    HashMap<String, Object> review = new HashMap<>();
                    review.put("name", displayName);
                    review.put("text",reviewText);
                    review.put("uid",uidPut);
                    review.put("docid",recipe.getDocId());

                    db.collection("reviews").add(review)
                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                @Override
                                public void onSuccess(DocumentReference documentReference) {
                                    Toast.makeText(getActivity(), "Review Created!", Toast.LENGTH_SHORT).show();
                                    binding.editReviewText.setText("");
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(getActivity(), "Failed to Create Review", Toast.LENGTH_SHORT).show();
                                }
                            });


                }


            }
        });

        binding.buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                detailListener.backToRecipe();
            }
        });

        binding.buttonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String docId = recipe.getDocId();

                FirebaseFirestore db = FirebaseFirestore.getInstance();

                db.collection("reviews").whereEqualTo("docid", docId)
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    WriteBatch batch = db.batch();
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        // Add each delete operation to the batch
                                        ((WriteBatch) batch).delete(document.getReference());
                                    }
                                    // Commit the batch operation to delete all reviews
                                    batch.commit().addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Log.d(TAG, "All reviews associated with the recipe deleted successfully");
                                        }
                                    });
                                }
                            }
                        });

                db.collection("recipes").document(docId)
                        .delete()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                detailListener.backToRecipe();
                                Toast.makeText(getActivity(), "Post Deleted!", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });

    }

    void getReviewsCollection(){

        db.collection("reviews").whereEqualTo("docid", recipe.getDocId()).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot task, @Nullable FirebaseFirestoreException error) {
                    Log.d(TAG, "onComplete: Query In SuccessFully");
                    mReviews.clear();
                    for(QueryDocumentSnapshot document: task){
                        Log.d(TAG, "onComplete: " + document.getId());
                        Log.d(TAG, "onComplete: " + document.getData());
                        Review add = new Review();
                        add.setName(document.getString("name"));
                        add.setUid(document.getString("uid"));
                        add.setText(document.getString("text"));
                        add.setRecipeDocId(document.getString("docid"));

                        mReviews.add(add);
                    }
                    adapter.notifyDataSetChanged();
            }
        });

        binding.ReviewRecyclerView.setVisibility(View.VISIBLE);
    }


    DetailListener detailListener;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        detailListener = (DetailListener) context;
    }

    public interface DetailListener{
        void backToRecipe();
    }

}