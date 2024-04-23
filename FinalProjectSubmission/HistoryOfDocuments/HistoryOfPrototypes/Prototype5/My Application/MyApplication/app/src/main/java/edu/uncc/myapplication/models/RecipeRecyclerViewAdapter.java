package edu.uncc.myapplication.models;

import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

import edu.uncc.myapplication.R;

public class RecipeRecyclerViewAdapter extends RecyclerView.Adapter<RecipeRecyclerViewAdapter.RecipeViewHolder>{

    final String TAG = "james";
    ArrayList<Recipe> recipes;
    IRecipeRecycler mListener;
    StorageReference storageReference = FirebaseStorage.getInstance().getReference();

    public RecipeRecyclerViewAdapter(ArrayList<Recipe> data, IRecipeRecycler mListener){
        this.mListener = mListener;
        this.recipes = data;
    }

    @NonNull
    @Override
    public RecipeRecyclerViewAdapter.RecipeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recipelistitem, parent, false);
        RecipeViewHolder recipeViewHolder = new RecipeViewHolder(view, mListener);

        return recipeViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecipeViewHolder holder, int position) {
        Recipe recipe = recipes.get(position);

        Log.d(TAG, "onBindViewHolder: " + recipe.getName() + " " + recipe.getTime());

        String docId = recipe.getDocId();
        holder.nameText.setText(recipe.getName());
        holder.userText.setText("Created By " + recipe.getUser());
        holder.timeText.setText(recipe.getTime());
        holder.recipe = recipe;

        String imagePath = "images/" + docId + ".jpg"; // Adjust this path as needed.
        StorageReference imageRef = storageReference.child(imagePath);

        Glide.with(holder.itemView.getContext())
                .load(R.drawable.transparent) // Placeholder image
                .into(holder.recipeImage);

        imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                // Use Glide or Picasso to load the image
                Glide.with(holder.itemView.getContext())
                        .load(uri)
                        .into(holder.recipeImage);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e(TAG, "Error fetching image: ", e);
                // Handle the case where the image is not found or error
                // Set a default placeholder image
                Glide.with(holder.itemView.getContext())
                        .load(R.drawable.pb_j_hero) // Default placeholder image
                        .into(holder.recipeImage);
            }
        });
    }



    @Override
    public int getItemCount() {
        return this.recipes.size();
    }

public static class RecipeViewHolder extends RecyclerView.ViewHolder{
    TextView nameText;
    TextView userText;
    TextView timeText;

    ImageView recipeImage;
    IRecipeRecycler mListener;
    Recipe recipe;
    public RecipeViewHolder(@NonNull View itemView, IRecipeRecycler mListener) {
        super(itemView);
        this.mListener = mListener;
        nameText = itemView.findViewById(R.id.textViewName);
        userText = itemView.findViewById(R.id.textViewUser);
        timeText = itemView.findViewById(R.id.textViewTime);
        recipeImage = itemView.findViewById(R.id.RecipeimageView);

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.goToRecipeDetails(recipe);
            }
        });

    }
}

    public void filterList(ArrayList<Recipe> filteredList) {
        recipes = filteredList;
        notifyDataSetChanged();
    }


    public interface IRecipeRecycler{
        void goToRecipeDetails(Recipe recipe);
    }

}
