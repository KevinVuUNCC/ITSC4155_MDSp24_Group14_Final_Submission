package edu.uncc.myapplication.fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import edu.uncc.myapplication.models.Recipe;
import edu.uncc.myapplication.databinding.FragmentRecipeDetailsBinding;

public class RecipeDetailsFragment extends Fragment {
    private static final String ARG_RECIPE = "RECIPE";

    // TODO: Rename and change types of parameters
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

        binding.textView2.setText(recipe.getName());
        binding.textView3.setText(recipe.getTime());
        binding.textView4.setText(recipe.getDescription());

        binding.button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                detailListener.backToRecipe();
            }
        });

        binding.button4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                detailListener.deleteRecipe(recipe);
            }
        });

    }


    DetailListener detailListener;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        detailListener = (DetailListener) context;
    }

    //TODO: The interface for the TasksFragment
    public interface DetailListener{
        void backToRecipe();
        void deleteRecipe(Recipe recipe);
    }

}