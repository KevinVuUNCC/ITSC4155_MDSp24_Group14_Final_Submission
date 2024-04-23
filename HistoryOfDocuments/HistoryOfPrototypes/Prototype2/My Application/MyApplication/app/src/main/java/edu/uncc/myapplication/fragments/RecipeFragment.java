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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import edu.uncc.myapplication.models.Recipe;
import edu.uncc.myapplication.models.RecipeRecyclerViewAdapter;
import edu.uncc.myapplication.databinding.FragmentRecipeBinding;

public class RecipeFragment extends Fragment implements RecipeRecyclerViewAdapter.IRecipeRecycler {

    final String TAG = "james";
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

        getRecipeCollection();
        Log.d(TAG, "onRecipeViewCreated: " + mRecipes);

        recyclerView = binding.recyclerView;
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        adapter = new RecipeRecyclerViewAdapter(mRecipes,this);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

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

    void getRecipeCollection(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("recipes").get()
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