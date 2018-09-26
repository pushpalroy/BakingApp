package com.pushpal.bakingapp.networking;

import com.pushpal.bakingapp.model.Recipe;
import com.pushpal.bakingapp.utilities.Constants;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface RESTClientInterface {
    @GET(Constants.BAKING_URL)
    Call<List<Recipe>> getRecipes();
}