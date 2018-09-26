package com.pushpal.bakingapp.networking;

import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.pushpal.bakingapp.IdlingResource.SimpleIdlingResource;
import com.pushpal.bakingapp.model.Recipe;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RecipeDownloader {

    private static final int DELAY_MILLIS = 1000;
    private static final String TAG = RecipeDownloader.class.getSimpleName();
    private static List<Recipe> mRecipesList = new ArrayList<>();

    public static void downloadRecipes(final DelayerCallback callback,
                                       @Nullable final SimpleIdlingResource idlingResource) {
        if (idlingResource != null) {
            idlingResource.setIdleState(false);
        }

        RESTClientInterface restClientInterface = RESTClient.getClient().create(RESTClientInterface.class);
        Call<List<Recipe>> call = restClientInterface.getRecipes();

        call.enqueue(new Callback<List<Recipe>>() {
            @Override
            public void onResponse(@NonNull Call<List<Recipe>> call, @NonNull Response<List<Recipe>> response) {
                int statusCode = response.code();

                if (statusCode == 200) {
                    if (response.body() != null) {
                        mRecipesList = response.body();

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if (callback != null) {
                                    callback.onRecipesDownloaded(mRecipesList);
                                    if (idlingResource != null) {
                                        idlingResource.setIdleState(true);
                                    }
                                }
                            }
                        }, DELAY_MILLIS);
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Recipe>> call, @NonNull Throwable t) {
                Log.e(TAG, t.getMessage());
            }
        });
    }

    public interface DelayerCallback {
        void onRecipesDownloaded(List<Recipe> mRecipesList);
    }
}
