package com.pushpal.bakingapp;

import android.app.Activity;
import android.app.Instrumentation;
import android.support.test.espresso.IdlingRegistry;
import android.support.test.espresso.IdlingResource;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.pushpal.bakingapp.ui.MainActivity;
import com.pushpal.bakingapp.ui.StepsActivity;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.Intents.intending;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasExtra;
import static android.support.test.espresso.intent.matcher.IntentMatchers.isInternal;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.Matchers.not;

@RunWith(AndroidJUnit4.class)
public class RecipeListActivityIntentTest {

    private static final String RECIPE_NAME_2 = "Brownies";
    @Rule
    public IntentsTestRule<MainActivity> mActivityTestRule = new IntentsTestRule<>(MainActivity.class);
    private IdlingResource mIdlingResource;

    @Before
    public void registerIdlingResource() {
        mIdlingResource = mActivityTestRule.getActivity().getIdlingResource();
        IdlingRegistry.getInstance().register(mIdlingResource);
    }

    @Before
    public void stubAllExternalIntents() {
        intending(not(isInternal())).respondWith(new Instrumentation.ActivityResult(Activity.RESULT_OK, null));
    }

    @Test
    public void clickRecipeRecyclerViewItem() {

        // Clicking on a recycler view item at position 1 (positioning starts from 0)
        onView(withId(R.id.recipe_recycler_view))
                .perform(RecyclerViewActions.actionOnItemAtPosition(1, click()));

        // Checks that the intent goes to the correct Activity
        intended(hasComponent(StepsActivity.class.getName()));

        // Checks that the intent has the correct bundle extras
        intended(allOf(hasExtra("RecipeName", RECIPE_NAME_2)));
    }

    @After
    public void unregisterIdlingResource() {
        if (mIdlingResource != null)
            IdlingRegistry.getInstance().unregister(mIdlingResource);
    }
}