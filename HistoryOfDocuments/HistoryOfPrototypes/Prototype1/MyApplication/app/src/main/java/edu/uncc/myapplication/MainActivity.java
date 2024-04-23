package edu.uncc.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import java.util.ArrayList;

import edu.uncc.myapplication.fragments.AddRecipeFragment;
import edu.uncc.myapplication.fragments.RecipeDetailsFragment;
import edu.uncc.myapplication.fragments.RecipeFragment;
import edu.uncc.myapplication.models.Data;
import edu.uncc.myapplication.models.Recipe;

public class MainActivity extends AppCompatActivity implements RecipeFragment.RecipeListener, AddRecipeFragment.AddRecipeListener, RecipeDetailsFragment.DetailListener {

    private ArrayList<Recipe> mRecipes = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRecipes.addAll(Data.sampleTestRecipes);

        getSupportFragmentManager().beginTransaction()
                .add(R.id.rootView, RecipeFragment.newInstance(mRecipes), "RecipeFrag")
                .commit();
    }

    @Override
    public void gotoAddRecipe() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.rootView, new AddRecipeFragment(), "addRecipeFrag")
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void gotoRecipeDetails(Recipe recipe) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.rootView, RecipeDetailsFragment.newInstance(recipe))
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void SubmitTask(Recipe recipe) {
        mRecipes.add(recipe);
        RecipeFragment recipeFragment = (RecipeFragment) getSupportFragmentManager().findFragmentByTag("RecipeFrag");
        if (recipeFragment != null){
            getSupportFragmentManager().popBackStack();
        }
    }

    @Override
    public void backToRecipe() {
        RecipeFragment recipeFragment = (RecipeFragment) getSupportFragmentManager().findFragmentByTag("RecipeFrag");
        if (recipeFragment != null){
            getSupportFragmentManager().popBackStack();
        }
    }

    @Override
    public void deleteRecipe(Recipe recipe) {
        mRecipes.remove(recipe);
        RecipeFragment recipeFragment = (RecipeFragment) getSupportFragmentManager().findFragmentByTag("RecipeFrag");
        if (recipeFragment != null){
            getSupportFragmentManager().popBackStack();
        }
    }
}