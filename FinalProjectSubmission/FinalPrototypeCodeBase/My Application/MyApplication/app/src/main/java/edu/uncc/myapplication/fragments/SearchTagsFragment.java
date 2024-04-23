package edu.uncc.myapplication.fragments;

import android.content.Context;
import android.net.Uri;
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

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import edu.uncc.myapplication.R;
import edu.uncc.myapplication.databinding.FragmentLoginBinding;
import edu.uncc.myapplication.databinding.FragmentRecipeDetailsBinding;
import edu.uncc.myapplication.databinding.FragmentSearchTagsBinding;
import edu.uncc.myapplication.models.Recipe;
import edu.uncc.myapplication.models.RecipeRecyclerViewAdapter;
import edu.uncc.myapplication.models.TagsRecyclerViewAdapter;
import edu.uncc.myapplication.databinding.FragmentRecipeBinding;

public class SearchTagsFragment extends Fragment implements TagsRecyclerViewAdapter.ITagsRecycler {
    final String TAG = "james";
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    private ArrayList<Recipe> mRecipes = new ArrayList<Recipe>();
    private ArrayList<String> mTags = new ArrayList<String>();
    SearchTagsListener searchTagsListener;

    RecyclerView recyclerView;
    LinearLayoutManager layoutManager;

    FragmentSearchTagsBinding binding;

    TagsRecyclerViewAdapter adapter;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentSearchTagsBinding.inflate(inflater,container,false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //setImageFromFirebase();
        getTagsCollection();
        Log.d(TAG, "onTagsViewCreated: " + mTags);

        recyclerView = binding.recyclerView;
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        adapter = new TagsRecyclerViewAdapter(mTags,this);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        binding.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchTagsListener.backToRecipe();
            }
        });
    }



    void getTagsCollection(){
        db.collection("recipes").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()){
                            Log.d(TAG, "onComplete: Query In SuccessFully");
                            mRecipes.clear();
                            mTags.clear();
                            for(QueryDocumentSnapshot document: task.getResult()){
                                Recipe add = new Recipe();

                                Object tagsObj = document.get("tags");
                                if (tagsObj != null)
                                {
                                    List<String> tagl = (ArrayList<String>) tagsObj;
                                    for(int i=0;i<tagl.size();i++)
                                    {
                                        if(!mTags.contains(tagl.get(i)))
                                        {
                                            mTags.add(tagl.get(i));
                                            Log.d(TAG, "onTagsViewCreated: " + tagl.get(i));
                                        }
                                    }
                                    //add.setTags((ArrayList<String>) tagsObj);
                                    mTags.stream().distinct().collect(
                                            Collectors.toList());
                                }
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
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        searchTagsListener = (SearchTagsFragment.SearchTagsListener) context;
    }

    @Override
    public void toRecipeFilter(String tag) {
        searchTagsListener.toRecipeFilter(tag);
    }

    public interface SearchTagsListener{
        void toRecipeFilter(String tag);

        void backToRecipe();

    }
}

