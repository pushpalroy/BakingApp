package com.pushpal.bakingapp.widget;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteConstraintException;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.pushpal.bakingapp.R;
import com.pushpal.bakingapp.database.AppExecutors;
import com.pushpal.bakingapp.database.IngredientDatabase;
import com.pushpal.bakingapp.database.RecipeIngredient;

import java.util.ArrayList;
import java.util.List;

public class RecipeWidgetService extends RemoteViewsService {

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return (new RecipeRemoteViewsFactory(this.getApplicationContext()));
    }
}

class RecipeRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {

    private final String TAG = RecipeRemoteViewsFactory.class.getSimpleName();
    private ArrayList<ListItem> listItemList = new ArrayList<>();
    private Context context;

    RecipeRemoteViewsFactory(Context context) {
        this.context = context;
    }

    @Override
    public void onDataSetChanged() {
        populateListItem();
    }

    private void populateListItem() {
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                try {
                    IngredientDatabase db = IngredientDatabase.getInstance(context);
                    List<RecipeIngredient> ingredients = db.ingredientDao().getAllIngredients();
                    for (RecipeIngredient ingredient : ingredients) {
                        ListItem listItem = new ListItem();
                        listItem.ingredient = ingredient.getIngredientName();
                        listItem.measurement = ingredient.getIngredientMeasure();

                        listItemList.add(listItem);
                    }
                } catch (SQLiteConstraintException e) {
                    Log.e(TAG, e.getMessage());
                }
            }
        });
    }

    @Override
    public RemoteViews getViewAt(int position) {
        final RemoteViews remoteView = new RemoteViews(
                context.getPackageName(), R.layout.list_row);
        ListItem listItem = listItemList.get(position);
        remoteView.setTextViewText(R.id.tv_ingredient, listItem.ingredient);
        remoteView.setTextViewText(R.id.tv_measure, listItem.measurement);

        return remoteView;
    }

    @Override
    public int getCount() {
        return listItemList.size();
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public void onCreate() {

    }

    @Override
    public void onDestroy() {

    }
}