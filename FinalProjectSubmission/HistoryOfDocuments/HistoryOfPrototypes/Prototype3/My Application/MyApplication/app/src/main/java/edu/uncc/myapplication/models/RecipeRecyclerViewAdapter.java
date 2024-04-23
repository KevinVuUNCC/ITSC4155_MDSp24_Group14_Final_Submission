package edu.uncc.myapplication.models;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import edu.uncc.myapplication.R;

public class RecipeRecyclerViewAdapter extends RecyclerView.Adapter<RecipeRecyclerViewAdapter.RecipeViewHolder>{

    final String TAG = "james";
    ArrayList<Recipe> recipes;
    IRecipeRecycler mListener;

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

        Log.d(TAG, "onBindViewHolder: " + recipe.getName() +" "+ recipe.getTime());


        holder.nameText.setText(recipe.getName());
        holder.userText.setText("Created By " + recipe.getUser());
        holder.timeText.setText(recipe.getTime());
        holder.recipe = recipe;
    }

    @Override
    public int getItemCount() {
        return this.recipes.size();
    }

public static class RecipeViewHolder extends RecyclerView.ViewHolder{
    TextView nameText;
    TextView userText;

    TextView timeText;
    IRecipeRecycler mListener;
    Recipe recipe;
    public RecipeViewHolder(@NonNull View itemView, IRecipeRecycler mListener) {
        super(itemView);
        this.mListener = mListener;
        nameText = itemView.findViewById(R.id.textViewName);
        userText = itemView.findViewById(R.id.textViewUser);
        timeText = itemView.findViewById(R.id.textViewTime);

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.goToRecipeDetails(recipe);
            }
        });

    }
}

    public interface IRecipeRecycler{
        void goToRecipeDetails(Recipe recipe);
    }

}
