package com.pushpal.bakingapp.ui;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pushpal.bakingapp.R;
import com.pushpal.bakingapp.adapter.StepsAdapter;

public class MasterListFragment extends Fragment {

    RecyclerView stepsRecyclerView;
    int currentPosition = -1;

    public MasterListFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        final View rootView = inflater.inflate(R.layout.fragment_master_list, container, false);
        stepsRecyclerView = rootView.findViewById(R.id.rv_steps);

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        StepsAdapter mAdapter = new StepsAdapter(((StepsActivity) getActivity()).getStepsList(),
                ((StepsActivity) getActivity()).getIngredients(),
                ((StepsActivity) getActivity()).getSteps(),
                (StepsActivity) getActivity());
        currentPosition = ((StepsActivity) getActivity()).getStepPosition();
        if (currentPosition != -1)
            mAdapter.setStep(currentPosition);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        stepsRecyclerView.setLayoutManager(mLayoutManager);
        stepsRecyclerView.setAdapter(mAdapter);
        if (currentPosition != -1)
            stepsRecyclerView.scrollToPosition(currentPosition);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        // Saving the current step number
        currentPosition = ((StepsActivity) getActivity()).getStepPosition();
        outState.putInt("step_position", currentPosition);
    }

    @Override
    public void onViewStateRestored(Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);

        if (savedInstanceState != null) {
            // Restoring the step number
            if (savedInstanceState.containsKey("step_position")) {
                currentPosition = savedInstanceState.getInt("step_position");
            }
        }
    }
}