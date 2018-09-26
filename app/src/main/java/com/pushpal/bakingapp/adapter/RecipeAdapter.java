package com.pushpal.bakingapp.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.pushpal.bakingapp.R;
import com.pushpal.bakingapp.model.Recipe;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder> {

    RecipeClickListener recipeClickListener;
    private List<Recipe> recipes;

    public RecipeAdapter(List<Recipe> recipes, RecipeClickListener recipeClickListener) {
        this.recipes = recipes;
        this.recipeClickListener = recipeClickListener;
    }

    @NonNull
    @Override
    public RecipeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new RecipeViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_recipe,
                        parent,
                        false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecipeViewHolder holder, int position) {
        final Recipe recipe = recipes.get(position);
        holder.recipeName.setText(recipe.getName());
        String servings = recipe.getServings() + " Servings";
        holder.recipeServings.setText(servings);
        switch (position) {
            case 0:
                holder.recipeImage.setImageResource(R.drawable.nutella_pie);
                break;
            case 1:
                holder.recipeImage.setImageResource(R.drawable.brownie);
                break;
            case 2:
                holder.recipeImage.setImageResource(R.drawable.yellow_cake);
                break;
            case 3:
                holder.recipeImage.setImageResource(R.drawable.cheesecake);
                break;
            default:
                holder.recipeImage.setImageResource(R.drawable.nutella_pie);
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                recipeClickListener.onRecipeClicked(recipe);
            }
        });
    }

    @Override
    public int getItemCount() {
        return recipes.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    static class RecipeViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tv_recipe_name)
        TextView recipeName;

        @BindView(R.id.tv_recipe_servings)
        TextView recipeServings;

        @BindView(R.id.iv_recipe_image)
        ImageView recipeImage;

        RecipeViewHolder(View itemView) {
            super(itemView);

            // ButterKnife Binding
            ButterKnife.bind(this, itemView);
        }
    }
}
