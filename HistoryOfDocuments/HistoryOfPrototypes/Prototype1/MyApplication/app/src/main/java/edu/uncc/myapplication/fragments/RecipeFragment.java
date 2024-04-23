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

import java.util.ArrayList;

import edu.uncc.myapplication.models.Recipe;
import edu.uncc.myapplication.models.RecipeRecyclerViewAdapter;
import edu.uncc.myapplication.databinding.FragmentRecipeBinding;

public class RecipeFragment extends Fragment implements RecipeRecyclerViewAdapter.IRecipeRecycler {

    final String TAG = "james";
    private static final String ARG_RECIPES = "RECIPES";
    private ArrayList<Recipe> mRecipes;

    RecyclerView recyclerView;
    LinearLayoutManager layoutManager;
    RecipeRecyclerViewAdapter adapter;

    public RecipeFragment() {
        // Required empty public constructor
    }

    public static RecipeFragment newInstance(ArrayList<Recipe> data) {
        RecipeFragment fragment = new RecipeFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_RECIPES, data);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mRecipes = (ArrayList<Recipe>) getArguments().getSerializable(ARG_RECIPES);
        }
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

        Log.d(TAG, "onViewCreated: " + mRecipes);

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
    }

}