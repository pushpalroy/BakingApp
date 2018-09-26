package com.pushpal.bakingapp.adapter;

import com.pushpal.bakingapp.model.Ingredient;
import com.pushpal.bakingapp.model.Step;

import java.util.ArrayList;

public interface StepClickListener {
    void onStepClicked(int position, ArrayList<Ingredient> ingredients, ArrayList<Step> steps);
}
