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

    public MasterListFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        final View rootView = inflater.inflate(R.layout.fragment_master_list, container, false);

        RecyclerView stepsRecyclerView = rootView.findViewById(R.id.rv_steps);
        StepsAdapter mAdapter = new StepsAdapter(((StepsActivity) getActivity()).getStepsList(),
                ((StepsActivity) getActivity()).getIngredients(),
                ((StepsActivity) getActivity()).getSteps(),
                (StepsActivity) getActivity());
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        stepsRecyclerView.setLayoutManager(mLayoutManager);
        stepsRecyclerView.setAdapter(mAdapter);

        return rootView;
    }
}