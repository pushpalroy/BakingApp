package com.pushpal.bakingapp.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.pushpal.bakingapp.R;
import com.pushpal.bakingapp.model.Ingredient;
import com.pushpal.bakingapp.model.Step;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class StepsAdapter extends RecyclerView.Adapter<StepsAdapter.StepViewHolder> {

    private List<String> stepsList;
    private StepClickListener stepClickListener;
    private ArrayList<Ingredient> ingredients;
    private ArrayList<Step> steps;

    public StepsAdapter(List<String> stepsList, ArrayList<Ingredient> ingredients,
                        ArrayList<Step> steps, StepClickListener stepClickListener) {
        this.stepsList = stepsList;
        this.ingredients = ingredients;
        this.steps = steps;
        this.stepClickListener = stepClickListener;
    }

    @NonNull
    @Override
    public StepViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new StepViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_step,
                        parent,
                        false));
    }

    @Override
    public void onBindViewHolder(@NonNull StepViewHolder holder, final int position) {
        final String step = stepsList.get(position);
        holder.stepName.setText(step);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stepClickListener.onStepClicked(position, ingredients, steps);
            }
        });
    }

    @Override
    public int getItemCount() {
        return stepsList.size();
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

        @BindView(R.id.tv_step)
        TextView stepName;

        StepViewHolder(View itemView) {
            super(itemView);

            // ButterKnife Binding
            ButterKnife.bind(this, itemView);
        }
    }
}
