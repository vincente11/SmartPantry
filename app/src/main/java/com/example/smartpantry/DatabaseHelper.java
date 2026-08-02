package com.example.smartpantry;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "SmartPantry.db";
    private static final int DATABASE_VERSION = 1;

    // Pantry Table
    public static final String TABLE_PANTRY = "pantry";
    public static final String COLUMN_PANTRY_ID = "_id";
    public static final String COLUMN_PANTRY_NAME = "name";
    public static final String COLUMN_PANTRY_QTY = "quantity";
    public static final String COLUMN_PANTRY_UNIT = "unit";
    public static final String COLUMN_PANTRY_EXPIRY = "expiry_date";

    // Recipes Table
    public static final String TABLE_RECIPES = "recipes";
    public static final String COLUMN_RECIPE_ID = "_id";
    public static final String COLUMN_RECIPE_NAME = "name";
    public static final String COLUMN_RECIPE_STEPS = "steps";

    // Recipe Ingredients Table
    public static final String TABLE_RECIPE_INGREDIENTS = "recipe_ingredients";
    public static final String COLUMN_RI_ID = "_id";
    public static final String COLUMN_RI_RECIPE_ID = "recipe_id";
    public static final String COLUMN_RI_NAME = "ingredient_name";
    public static final String COLUMN_RI_QTY = "quantity";
    public static final String COLUMN_RI_UNIT = "unit";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_PANTRY = "CREATE TABLE " + TABLE_PANTRY + " (" +
                COLUMN_PANTRY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_PANTRY_NAME + " TEXT, " +
                COLUMN_PANTRY_QTY + " REAL, " +
                COLUMN_PANTRY_UNIT + " TEXT, " +
                COLUMN_PANTRY_EXPIRY + " TEXT)";

        String CREATE_RECIPES = "CREATE TABLE " + TABLE_RECIPES + " (" +
                COLUMN_RECIPE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_RECIPE_NAME + " TEXT, " +
                COLUMN_RECIPE_STEPS + " TEXT)";

        String CREATE_RI = "CREATE TABLE " + TABLE_RECIPE_INGREDIENTS + " (" +
                COLUMN_RI_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_RI_RECIPE_ID + " INTEGER, " +
                COLUMN_RI_NAME + " TEXT, " +
                COLUMN_RI_QTY + " REAL, " +
                COLUMN_RI_UNIT + " TEXT, " +
                "FOREIGN KEY(" + COLUMN_RI_RECIPE_ID + ") REFERENCES " + TABLE_RECIPES + "(" + COLUMN_RECIPE_ID + "))";

        db.execSQL(CREATE_PANTRY);
        db.execSQL(CREATE_RECIPES);
        db.execSQL(CREATE_RI);

        seedRecipes(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PANTRY);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RECIPES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RECIPE_INGREDIENTS);
        onCreate(db);
    }

    // CRUD - Pantry Operations
    public long addPantryItem(PantryItem item) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_PANTRY_NAME, item.getName());
        cv.put(COLUMN_PANTRY_QTY, item.getQuantity());
        cv.put(COLUMN_PANTRY_UNIT, item.getUnit());
        cv.put(COLUMN_PANTRY_EXPIRY, item.getExpiryDate());
        return db.insert(TABLE_PANTRY, null, cv);
    }

    public List<PantryItem> getAllPantryItems() {
        List<PantryItem> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_PANTRY, null);
        if (cursor.moveToFirst()) {
            do {
                list.add(new PantryItem(
                        cursor.getLong(0),
                        cursor.getString(1),
                        cursor.getDouble(2),
                        cursor.getString(3),
                        cursor.getString(4)
                ));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }

    public int updatePantryItem(PantryItem item) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_PANTRY_NAME, item.getName());
        cv.put(COLUMN_PANTRY_QTY, item.getQuantity());
        cv.put(COLUMN_PANTRY_UNIT, item.getUnit());
        cv.put(COLUMN_PANTRY_EXPIRY, item.getExpiryDate());
        return db.update(TABLE_PANTRY, cv, COLUMN_PANTRY_ID + "=?", new String[]{String.valueOf(item.getId())});
    }

    public void deletePantryItem(long id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_PANTRY, COLUMN_PANTRY_ID + "=?", new String[]{String.valueOf(id)});
    }

    public double getPantryQuantityByName(String name) {
        SQLiteDatabase db = this.getReadableDatabase();
        double quantity = 0;
        Cursor cursor = db.rawQuery("SELECT quantity FROM pantry WHERE LOWER(name) = LOWER(?)", new String[]{name});
        if (cursor.moveToFirst()) {
            quantity = cursor.getDouble(0);
        }
        cursor.close();
        return quantity;
    }

    // Seed 15 Recipes
    private void seedRecipes(SQLiteDatabase db) {
        addRecipe(db, "Scrambled Eggs", "1. Beat eggs with milk.\n2. Melt butter in pan.\n3. Cook on low heat.",
                new String[][]{{"egg", "2", "pcs"}, {"milk", "50", "ml"}, {"butter", "10", "g"}});
        addRecipe(db, "Pancakes", "1. Mix flour, egg, and milk.\n2. Pour batter into oiled pan.\n3. Flip when golden.",
                new String[][]{{"flour", "200", "g"}, {"egg", "1", "pcs"}, {"milk", "250", "ml"}, {"butter", "20", "g"}});
        addRecipe(db, "Grilled Cheese Sandwich", "1. Butter bread slices.\n2. Place cheese between bread.\n3. Toast in skillet.",
                new String[][]{{"bread", "2", "slices"}, {"cheese", "2", "slices"}, {"butter", "10", "g"}});
        addRecipe(db, "Tomato Soup", "1. Sauté onions and tomatoes in butter.\n2. Blend and simmer with salt.",
                new String[][]{{"tomato", "3", "pcs"}, {"onion", "1", "pcs"}, {"butter", "15", "g"}, {"salt", "5", "g"}});
        addRecipe(db, "Garlic Rice", "1. Fry minced garlic in butter.\n2. Add cooked rice and salt.\n3. Toss well.",
                new String[][]{{"rice", "200", "g"}, {"garlic", "2", "cloves"}, {"butter", "15", "g"}, {"salt", "2", "g"}});
        addRecipe(db, "Omelet", "1. Whisk eggs with salt.\n2. Pour into pan, add cheese, and fold.",
                new String[][]{{"egg", "3", "pcs"}, {"cheese", "50", "g"}, {"butter", "10", "g"}, {"salt", "2", "g"}});
        addRecipe(db, "Boiled Pasta with Butter", "1. Boil pasta in salted water.\n2. Drain and toss with butter and cheese.",
                new String[][]{{"pasta", "200", "g"}, {"butter", "20", "g"}, {"cheese", "30", "g"}, {"salt", "5", "g"}});
        addRecipe(db, "French Toast", "1. Dip bread in beaten egg and milk.\n2. Fry in butter until brown.",
                new String[][]{{"bread", "2", "slices"}, {"egg", "1", "pcs"}, {"milk", "50", "ml"}, {"butter", "10", "g"}});
        addRecipe(db, "Mashed Potatoes", "1. Boil potatoes until soft.\n2. Mash with butter, milk, and salt.",
                new String[][]{{"potato", "3", "pcs"}, {"butter", "25", "g"}, {"milk", "50", "ml"}, {"salt", "5", "g"}});
        addRecipe(db, "Simple Chicken Salad", "1. Shred cooked chicken.\n2. Toss with chopped tomato and lettuce.",
                new String[][]{{"chicken", "200", "g"}, {"tomato", "1", "pcs"}, {"lettuce", "50", "g"}});
        addRecipe(db, "Fried Eggs", "1. Heat butter in pan.\n2. Crack eggs into pan and cook.",
                new String[][]{{"egg", "2", "pcs"}, {"butter", "10", "g"}, {"salt", "1", "g"}});
        addRecipe(db, "Garlic Bread", "1. Spread butter and garlic on bread slices.\n2. Toast until crispy.",
                new String[][]{{"bread", "4", "slices"}, {"garlic", "2", "cloves"}, {"butter", "20", "g"}});
        addRecipe(db, "Simple Rice Porridge", "1. Boil rice with milk and salt until soft.",
                new String[][]{{"rice", "100", "g"}, {"milk", "200", "ml"}, {"salt", "2", "g"}});
        addRecipe(db, "Cheesy Potatoes", "1. Bake potatoes, top with cheese and butter, melt.",
                new String[][]{{"potato", "2", "pcs"}, {"cheese", "50", "g"}, {"butter", "10", "g"}});
        addRecipe(db, "Sauteed Mushrooms", "1. Sauté mushrooms in butter with minced garlic.",
                new String[][]{{"mushroom", "150", "g"}, {"butter", "15", "g"}, {"garlic", "1", "cloves"}});
    }

    private void addRecipe(SQLiteDatabase db, String name, String steps, String[][] ingredients) {
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_RECIPE_NAME, name);
        cv.put(COLUMN_RECIPE_STEPS, steps);
        long recipeId = db.insert(TABLE_RECIPES, null, cv);

        for (String[] ing : ingredients) {
            ContentValues ingCv = new ContentValues();
            ingCv.put(COLUMN_RI_RECIPE_ID, recipeId);
            ingCv.put(COLUMN_RI_NAME, ing[0]);
            ingCv.put(COLUMN_RI_QTY, Double.parseDouble(ing[1]));
            ingCv.put(COLUMN_RI_UNIT, ing[2]);
            db.insert(TABLE_RECIPE_INGREDIENTS, null, ingCv);
        }
    }

    public List<Recipe> getAllRecipes() {
        List<Recipe> recipes = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_RECIPES, null);

        if (cursor.moveToFirst()) {
            do {
                long recipeId = cursor.getLong(0);
                String name = cursor.getString(1);
                String steps = cursor.getString(2);
                List<RecipeIngredient> ingredients = getIngredientsForRecipe(recipeId);
                recipes.add(new Recipe(recipeId, name, steps, ingredients));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return recipes;
    }

    private List<RecipeIngredient> getIngredientsForRecipe(long recipeId) {
        List<RecipeIngredient> ingredients = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_RECIPE_INGREDIENTS + " WHERE " + COLUMN_RI_RECIPE_ID + "=?",
                new String[]{String.valueOf(recipeId)});

        if (cursor.moveToFirst()) {
            do {
                ingredients.add(new RecipeIngredient(
                        cursor.getString(2),
                        cursor.getDouble(3),
                        cursor.getString(4)
                ));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return ingredients;
    }
}