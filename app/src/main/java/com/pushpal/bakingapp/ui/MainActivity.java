package com.pushpal.bakingapp.ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import android.support.test.espresso.IdlingResource;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import com.pushpal.bakingapp.IdlingResource.SimpleIdlingResource;
import com.pushpal.bakingapp.R;
import com.pushpal.bakingapp.adapter.RecipeAdapter;
import com.pushpal.bakingapp.adapter.RecipeClickListener;
import com.pushpal.bakingapp.model.Ingredient;
import com.pushpal.bakingapp.model.Recipe;
import com.pushpal.bakingapp.model.Step;
import com.pushpal.bakingapp.networking.NetworkUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements RecipeClickListener {

    public static final String TAG = MainActivity.class.getSimpleName();
    @BindView(R.id.toolbar)
    public Toolbar toolbar;
    @BindView(R.id.recipe_recycler_view)
    public RecyclerView mRecyclerView;
    List<Recipe> mRecipesList;

    @Nullable
    private SimpleIdlingResource mIdlingResource;

    /**
     * Only called from test, creates and returns a new {@link SimpleIdlingResource}.
     */
    @VisibleForTesting
    @NonNull
    public IdlingResource getIdlingResource() {
        if (mIdlingResource == null) {
            mIdlingResource = new SimpleIdlingResource();
        }
        return mIdlingResource;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        // Get the IdlingResource instance
        getIdlingResource();

        setUpActionBar();
    }

    @Override
    protected void onStart() {
        super.onStart();
        new FetchRecipesTask().execute();
    }

    private void setupRecyclerView() {
        RecipeAdapter mAdapter = new RecipeAdapter(mRecipesList, this);
        RecyclerView.LayoutManager mLayoutManager;
        if (getResources().getBoolean(R.bool.is_phone))
            mLayoutManager = new LinearLayoutManager(this);
        else
            mLayoutManager = new GridLayoutManager(this, 3);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);
    }

    private void setUpActionBar() {
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(Color.WHITE);
    }

    @Override
    public void onRecipeClicked(Recipe recipe) {
        Intent intent = new Intent(MainActivity.this, StepsActivity.class);
        Bundle bundle = new Bundle();
        bundle.putParcelable("Recipe", recipe);
        bundle.putString("RecipeName", recipe.getName());
        bundle.putParcelableArrayList("Ingredients", recipe.getIngredients());
        bundle.putParcelableArrayList("Steps", recipe.getSteps());
        intent.putExtras(bundle);
        startActivity(intent);
    }

    public void onRecipesDownloaded(List<Recipe> mRecipesList) {
        this.mRecipesList = mRecipesList;
        setupRecyclerView();
    }

    @SuppressLint("StaticFieldLeak")
    public class FetchRecipesTask extends AsyncTask<Void, Void, Void> {

        private List<Recipe> recipesList;

        @Override
        protected Void doInBackground(Void... params) {

            URL recipesJsonUrl = NetworkUtils.buildUrl();

            try {
                String jsonResponse = NetworkUtils
                        .getResponseFromHttpUrl(recipesJsonUrl);

                if (null != jsonResponse) {
                    JSONArray jsonArray = new JSONArray(jsonResponse.trim());

                    for (int i = 0; i < jsonArray.length(); i++) {

                        JSONObject jsonObject = jsonArray.getJSONObject(i);

                        Recipe recipe = new Recipe();
                        recipe.setId(jsonObject.getInt("id"));
                        recipe.setName(jsonObject.getString("name"));
                        recipe.setServings(jsonObject.getInt("servings"));
                        recipe.setImage(jsonObject.getString("image"));

                        JSONArray jsonArrayIngredients = jsonObject.getJSONArray("ingredients");
                        ArrayList<Ingredient> ingredients = new ArrayList<>();
                        for (int j = 0; j < jsonArrayIngredients.length(); j++) {
                            JSONObject jsonObjectIngredient = jsonArrayIngredients.getJSONObject(j);
                            ingredients.add(new Ingredient(jsonObjectIngredient.getDouble("quantity"),
                                    jsonObjectIngredient.getString("measure"),
                                    jsonObjectIngredient.getString("ingredient")));
                        }
                        recipe.setIngredients(ingredients);

                        JSONArray jsonArraySteps = jsonObject.getJSONArray("steps");
                        ArrayList<Step> steps = new ArrayList<>();
                        for (int j = 0; j < jsonArraySteps.length(); j++) {
                            JSONObject jsonObjectStep = jsonArraySteps.getJSONObject(j);
                            steps.add(new Step(jsonObjectStep.getInt("id"),
                                    jsonObjectStep.getString("shortDescription"),
                                    jsonObjectStep.getString("description"),
                                    jsonObjectStep.getString("videoURL"),
                                    jsonObjectStep.getString("thumbnailURL")));
                        }
                        recipe.setSteps(steps);

                        recipesList.add(recipe);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (mIdlingResource != null) {
                mIdlingResource.setIdleState(false);
            }

            recipesList = new ArrayList<>();
        }

        @Override
        protected void onPostExecute(Void weatherData) {
            onRecipesDownloaded(recipesList);
            if (mIdlingResource != null) {
                mIdlingResource.setIdleState(true);
            }
        }
    }
}