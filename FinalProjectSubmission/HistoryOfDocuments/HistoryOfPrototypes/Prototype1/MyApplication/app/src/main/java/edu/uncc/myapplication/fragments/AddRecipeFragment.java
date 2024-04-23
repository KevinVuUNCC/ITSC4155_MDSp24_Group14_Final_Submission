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

import edu.uncc.myapplication.models.Recipe;
import edu.uncc.myapplication.databinding.FragmentAddRecipeBinding;

public class AddRecipeFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private String mParam2;

    public AddRecipeFragment() {
        // Required empty public constructor
    }

    public static AddRecipeFragment newInstance(String param1, String param2) {
        AddRecipeFragment fragment = new AddRecipeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        super.onViewCreated(view, savedInstanceState);

        binding.button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addRecipeListener.backToRecipe();
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
                    Recipe send = new Recipe();
                    send.setName(binding.editTextTitle.getText().toString());
                    send.setTime(binding.editTextTime.getText().toString());
                    send.setDescription(binding.editTextTextMultiLine.getText().toString());
                    addRecipeListener.SubmitTask(send);
                }

            }
        });
    }

    AddRecipeListener addRecipeListener;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        addRecipeListener = (AddRecipeListener) context;
    }

    //TODO: The interface for the TasksFragment
    public interface AddRecipeListener{
        void backToRecipe();
        void SubmitTask(Recipe recipe);
    }

}