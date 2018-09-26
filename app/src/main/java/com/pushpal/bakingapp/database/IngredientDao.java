package com.pushpal.bakingapp.database;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

@Dao
public interface IngredientDao {
    @Query("SELECT * FROM ingredients")
    List<RecipeIngredient> getAllIngredients();

    @Query("SELECT * FROM ingredients WHERE id = :id")
    RecipeIngredient getIngredient(int id);

    @Query("DELETE FROM ingredients")
    void nukeTable();

    @Insert
    void insertIngredient(RecipeIngredient ingredient);

    @Update
    void updateIngredient(RecipeIngredient ingredient);

    @Delete
    void deleteIngredient(RecipeIngredient ingredient);
}
