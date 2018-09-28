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

    public static void updateWidget(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_recipe);
            Intent intent = new Intent(context, RecipeWidgetService.class);
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
            intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));

            // Fetch Recipe Name from Shared Preferences
            SharedPreferences sharedpreferences = context.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
            String recipe = sharedpreferences.getString("Recipe", "No Recipe Added");
            remoteViews.setTextViewText(R.id.tv_label, recipe);

            remoteViews.setRemoteAdapter(R.id.list_view_ingredients, intent);
            remoteViews.setEmptyView(R.id.list_view_ingredients, R.id.tv_error);

            // Set pending intent for onClick event on widget
            Intent recipeIntent = new Intent(context, MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, recipeIntent, 0);
            remoteViews.setOnClickPendingIntent(R.id.tv_label, pendingIntent);

            // Calling update app widget
            appWidgetManager.updateAppWidget(appWidgetId, remoteViews);
        }
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);
        updateWidget(context, appWidgetManager, appWidgetIds);
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
