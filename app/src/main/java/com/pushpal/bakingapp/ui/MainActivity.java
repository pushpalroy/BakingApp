package com.pushpal.bakingapp.ui;

import android.content.Intent;
import android.graphics.Color;
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
import com.pushpal.bakingapp.model.Recipe;
import com.pushpal.bakingapp.networking.RecipeDownloader;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements RecipeClickListener, RecipeDownloader.DelayerCallback {

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
        RecipeDownloader.downloadRecipes(MainActivity.this, mIdlingResource);
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

    @Override
    public void onRecipesDownloaded(List<Recipe> mRecipesList) {
        this.mRecipesList = mRecipesList;
        setupRecyclerView();
    }
}