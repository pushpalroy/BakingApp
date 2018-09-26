package com.pushpal.bakingapp.database;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity(tableName = "ingredients")
public class RecipeIngredient {

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "id")
    private String id;
    @ColumnInfo(name = "name")
    private String ingredientName;
    @ColumnInfo(name = "measure")
    private String ingredientMeasure;

    public RecipeIngredient(String id, String ingredientName, String ingredientMeasure) {
        this.id = id;
        this.ingredientName = ingredientName;
        this.ingredientMeasure = ingredientMeasure;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIngredientName() {
        return ingredientName;
    }

    public String getIngredientMeasure() {
        return ingredientMeasure;
    }
}
