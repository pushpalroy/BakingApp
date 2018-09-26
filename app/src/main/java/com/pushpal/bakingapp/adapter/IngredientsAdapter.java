package com.pushpal.bakingapp.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.pushpal.bakingapp.R;
import com.pushpal.bakingapp.model.Ingredient;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class IngredientsAdapter extends RecyclerView.Adapter<IngredientsAdapter.StepViewHolder> {

    private List<Ingredient> ingredients;

    public IngredientsAdapter(List<Ingredient> ingredients) {
        this.ingredients = ingredients;
    }

    @NonNull
    @Override
    public StepViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new StepViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_ingredient,
                        parent,
                        false));
    }

    @Override
    public void onBindViewHolder(@NonNull StepViewHolder holder, final int position) {
        Ingredient ingredient = ingredients.get(position);
        holder.ingredientName.setText(ingredient.getIngredientName());
        String quantity = ingredient.getQuantity() + " " + ingredient.getMeasure();
        holder.ingredientQuantity.setText(quantity);
    }

    @Override
    public int getItemCount() {
        return ingredients.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    static class StepViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tv_ingredient_name)
        TextView ingredientName;

        @BindView(R.id.tv_ingredient_quantity)
        TextView ingredientQuantity;

        StepViewHolder(View itemView) {
            super(itemView);

            // ButterKnife Binding
            ButterKnife.bind(this, itemView);
        }
    }
}
