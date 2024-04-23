package edu.uncc.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;

import edu.uncc.myapplication.fragments.AccountDetailsFragment;
import edu.uncc.myapplication.fragments.AddRecipeFragment;
import edu.uncc.myapplication.fragments.EditRecipeFragment;
import edu.uncc.myapplication.fragments.LoginFragment;
import edu.uncc.myapplication.fragments.RecipeDetailsFragment;
import edu.uncc.myapplication.fragments.RecipeFragment;
import edu.uncc.myapplication.fragments.SignUpFragment;
import edu.uncc.myapplication.models.Recipe;

public class MainActivity extends AppCompatActivity implements AccountDetailsFragment.AccountDetailListener, SignUpFragment.SignUpListener, LoginFragment.LoginListener ,RecipeFragment.RecipeListener, AddRecipeFragment.AddRecipeListener, RecipeDetailsFragment.DetailListener, EditRecipeFragment.EditRecipeListener {

    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() == null){
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.rootView, new LoginFragment())
                    .commit();
        } else {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.rootView, new RecipeFragment(), "RecipeFrag")
                    .commit();
        }
    }

    @Override
    public void createNewAccount() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.rootView, new SignUpFragment())
                .commit();
    }

    @Override
    public void login() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.rootView, new LoginFragment())
                .commit();

    }

    @Override
    public void authCompleted() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.rootView, new RecipeFragment(), "RecipeFrag")
                .commit();
    }

    @Override
    public void gotoAddRecipe() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.rootView, new AddRecipeFragment(), "addRecipeFrag")
                .addToBackStack(null)
                .commit();
    }

    public void gotoEditRecipe(Recipe recipe) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.rootView, EditRecipeFragment.newInstance(recipe, "editRecipeFrag", ""))
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void gotoAccountDetails() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.rootView, AccountDetailsFragment.newInstance(null, null))
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void goToProfile(String uid, String userName) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.rootView, AccountDetailsFragment.newInstance(uid, userName))
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
    public void SubmitRecipe() {
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
    public void logout() {
        FirebaseAuth.getInstance().signOut();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.rootView, new LoginFragment())
                .commit();
    }

}