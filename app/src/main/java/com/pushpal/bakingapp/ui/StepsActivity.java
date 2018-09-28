package com.pushpal.bakingapp.ui;

import android.app.FragmentManager;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteConstraintException;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.pushpal.bakingapp.R;
import com.pushpal.bakingapp.adapter.StepClickListener;
import com.pushpal.bakingapp.database.AppExecutors;
import com.pushpal.bakingapp.database.IngredientDatabase;
import com.pushpal.bakingapp.database.RecipeIngredient;
import com.pushpal.bakingapp.model.Ingredient;
import com.pushpal.bakingapp.model.Recipe;
import com.pushpal.bakingapp.model.Step;
import com.pushpal.bakingapp.widget.RecipeWidgetProvider;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.pushpal.bakingapp.utilities.Constants.MyPREFERENCES;

public class StepsActivity extends AppCompatActivity implements StepClickListener {

    public static final String TAG = StepsActivity.class.getSimpleName();
    public static String RecipeName = "Recipe";
    @BindView(R.id.toolbar)
    public Toolbar toolbar;
    private List<String> mStepsList;
    private ArrayList<Ingredient> ingredients = null;
    private ArrayList<Step> steps = null;
    private boolean isTwoPane = true;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = getIntent().getExtras();
        mStepsList = new ArrayList<>();

        if (bundle != null) {
            Recipe recipe = bundle.getParcelable("Recipe");
            ingredients = bundle.getParcelableArrayList("Ingredients");
            steps = bundle.getParcelableArrayList("Steps");
            RecipeName = bundle.getString("RecipeName");

            if (recipe != null) {
                if (ingredients != null && ingredients.size() != 0) {
                    mStepsList.add("Ingredients");
                }
                if (steps != null && steps.size() != 0) {
                    for (Step step : steps) {
                        mStepsList.add(step.getShortDescription());
                    }
                }
            }
        }

        setContentView(R.layout.activity_steps);
        ButterKnife.bind(this);
        setUpActionBar();

        if (null == findViewById(R.id.recipe_container))
            isTwoPane = false;
        else
            setStepInFragment(null, 0);
    }

    @Override
    public void onStepClicked(int position, ArrayList<Ingredient> ingredients, ArrayList<Step> steps) {
        if (!isTwoPane) {
            Intent recipeIntent = new Intent(StepsActivity.this, RecipeActivity.class);
            Bundle bundle = new Bundle();
            bundle.putParcelableArrayList("Ingredients", ingredients);
            bundle.putParcelableArrayList("Step", steps);
            bundle.putInt("Position", (position - 1));
            recipeIntent.putExtras(bundle);
            startActivity(recipeIntent);
        } else {
            setStepInFragment(steps, position);
        }
    }

    private void setStepInFragment(ArrayList<Step> steps, int position) {
        RecipeFragment recipeFragment = new RecipeFragment();
        recipeFragment.setSteps(steps);
        recipeFragment.setPosition(position - 1);
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.recipe_container, recipeFragment)
                .commit();
    }

    private void setUpActionBar() {
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setTitle(RecipeName);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.ingredient, menu);
        /* Return true so that the menu is displayed in the Toolbar */
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_add_to_widget) {
            AppExecutors.getInstance().diskIO().execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        IngredientDatabase db = IngredientDatabase.getInstance(StepsActivity.this);
                        int id = 0;
                        db.ingredientDao().nukeTable();
                        for (Ingredient ingredient : ingredients) {
                            db.ingredientDao().insertIngredient(new RecipeIngredient(String.valueOf(id),
                                    ingredient.getIngredientName(),
                                    ingredient.getQuantity() + ingredient.getMeasure()));
                            id++;
                        }

                        SharedPreferences sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedpreferences.edit();
                        editor.putString("Recipe", RecipeName);
                        editor.apply();

                        sendUpdateWidgetBroadcast();

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(StepsActivity.this, "Recipe ingredients added to widget!", Toast.LENGTH_LONG).show();
                            }
                        });
                    } catch (SQLiteConstraintException e) {
                        Log.e(TAG, e.getMessage());
                    }
                }
            });
        }
        return true;
    }

    private void sendUpdateWidgetBroadcast() {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(StepsActivity.this);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(this, RecipeWidgetProvider.class));
        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.list_view_ingredients);
        RecipeWidgetProvider.updateWidget(StepsActivity.this, appWidgetManager, appWidgetIds);
    }

    public List<String> getStepsList() {
        return mStepsList;
    }

    public ArrayList<Ingredient> getIngredients() {
        return ingredients;
    }

    public ArrayList<Step> getSteps() {
        return steps;
    }
}