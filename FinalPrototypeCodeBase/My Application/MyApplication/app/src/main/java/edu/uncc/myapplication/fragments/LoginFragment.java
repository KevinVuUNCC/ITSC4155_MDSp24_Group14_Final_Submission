package edu.uncc.myapplication.fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import edu.uncc.myapplication.R;
import edu.uncc.myapplication.databinding.FragmentLoginBinding;
import edu.uncc.myapplication.models.Recipe;

public class LoginFragment extends Fragment {

    private FirebaseAuth mAuth;
    final String TAG = "james";

    public LoginFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    FragmentLoginBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentLoginBinding.inflate(inflater,container,false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.buttonSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginListener.createNewAccount();
            }
        });

        binding.buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = binding.editTextEmail.getText().toString();
                String password = binding.editTextPassword.getText().toString();
                if(email.isEmpty()){
                    Toast.makeText(getActivity(), "Enter valid email!", Toast.LENGTH_SHORT).show();
                } else if (password.isEmpty()){
                    Toast.makeText(getActivity(), "Enter valid password!", Toast.LENGTH_SHORT).show();
                } else {

                    mAuth = FirebaseAuth.getInstance();
                    mAuth.signInWithEmailAndPassword(email,password)
                            .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()){
                                        Log.d(TAG, "onComplete: Logged In SuccessFully");
                                        loginListener.authCompleted();
                                    } else {
                                        Log.d(TAG, "onComplete: Error");
                                        Log.d(TAG, "onComplete: Error" + task.getException().getMessage());
                                        Toast.makeText(getActivity(), "Email or Password were Invalid", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });


                }
            }
        });

    }

    LoginListener loginListener;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        loginListener = (LoginListener) context;
    }

    public interface LoginListener{
        void createNewAccount();
        void authCompleted();
    }

}