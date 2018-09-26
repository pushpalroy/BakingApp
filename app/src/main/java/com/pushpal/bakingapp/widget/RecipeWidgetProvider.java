package com.pushpal.bakingapp.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.widget.RemoteViews;

import com.pushpal.bakingapp.R;
import com.pushpal.bakingapp.ui.MainActivity;

import static com.pushpal.bakingapp.utilities.Constants.MyPREFERENCES;

public class RecipeWidgetProvider extends AppWidgetProvider {

    private RemoteViews updateWidgetListView(Context context, int appWidgetId) {
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_recipe);
        Intent intent = new Intent(context, RecipeWidgetService.class);
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));

        SharedPreferences sharedpreferences = context.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        String recipe = sharedpreferences.getString("Recipe", "No Recipe Added");
        remoteViews.setTextViewText(R.id.tv_label, recipe);

        remoteViews.setRemoteAdapter(R.id.list_view_ingredients, intent);
        remoteViews.setEmptyView(R.id.list_view_ingredients, R.id.tv_error);

        Intent recipeIntent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, recipeIntent, 0);
        remoteViews.setOnClickPendingIntent(R.id.tv_label, pendingIntent);
        return remoteViews;
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);

        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            RemoteViews remoteViews = updateWidgetListView(context, appWidgetId);
            appWidgetManager.updateAppWidget(appWidgetId, remoteViews);
        }
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        super.onDeleted(context, appWidgetIds);
    }

    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);
    }

    @Override
    public void onDisabled(Context context) {
        super.onDisabled(context);
    }
}
