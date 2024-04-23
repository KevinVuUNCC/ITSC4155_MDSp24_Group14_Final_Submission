package edu.uncc.myapplication.fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import edu.uncc.myapplication.models.Recipe;
import edu.uncc.myapplication.databinding.FragmentRecipeDetailsBinding;

public class RecipeDetailsFragment extends Fragment {

    private FirebaseAuth mAuth;
    final String TAG = "james";
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

        mAuth = FirebaseAuth.getInstance();

        if (recipe.getUid().equals(mAuth.getCurrentUser().getUid())) {
            binding.buttonDelete.setVisibility(View.VISIBLE);
        } else {
            binding.buttonDelete.setVisibility(View.GONE);
        }

        binding.textViewDetailTitle.setText(recipe.getName());
        binding.textViewDetailTime.setText(recipe.getTime());
        binding.textViewDetailUser.setText("Recipe Created By: " + recipe.getUser());
        binding.textViewDetailDescription.setText(recipe.getDescription());

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