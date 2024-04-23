package edu.uncc.myapplication.fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;

import edu.uncc.myapplication.R;
import edu.uncc.myapplication.databinding.FragmentAccountDetailsBinding;
import edu.uncc.myapplication.models.Recipe;

public class AccountDetailsFragment extends Fragment {

    private FirebaseAuth mAuth;

    public AccountDetailsFragment() {
        // Required empty public constructor
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

        mAuth = FirebaseAuth.getInstance();

        binding.textViewAccountDetailUser.setText(mAuth.getCurrentUser().getDisplayName());

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

    AccountDetailListener accountDetailListener;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        accountDetailListener = (AccountDetailListener) context;
    }
    public interface AccountDetailListener{
        void backToRecipe();
        void logout();
    }

}