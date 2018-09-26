package com.pushpal.bakingapp.database;

import android.arch.persistence.db.SupportSQLiteOpenHelper;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.DatabaseConfiguration;
import android.arch.persistence.room.InvalidationTracker;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

@Database(entities = {RecipeIngredient.class}, version = 1, exportSchema = false)
public abstract class IngredientDatabase extends RoomDatabase {

    private static final String TAG = IngredientDatabase.class.getSimpleName();
    private static final Object LOCK = new Object();
    private static final String DB_NAME = "ingredientsDatabase";
    private static volatile IngredientDatabase sInstance;

    // Returning database instance using Singleton pattern
    public static synchronized IngredientDatabase getInstance(Context context) {
        if (sInstance == null) {
            synchronized (LOCK) {
                Log.d(TAG, "Creating new database instance");
                sInstance = createDatabase(context);
            }
        }
        return sInstance;
    }

    private static IngredientDatabase createDatabase(final Context context) {
        return Room.databaseBuilder(
                context,
                IngredientDatabase.class,
                DB_NAME).build();
    }

    public abstract IngredientDao ingredientDao();

    @NonNull
    @Override
    protected SupportSQLiteOpenHelper createOpenHelper(DatabaseConfiguration config) {
        return null;
    }

    @NonNull
    @Override
    protected InvalidationTracker createInvalidationTracker() {
        return null;
    }

    @Override
    public void clearAllTables() {
    }
}