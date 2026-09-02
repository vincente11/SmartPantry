package com.example.smartpantry;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView rvPantry;
    private PantryAdapter pantryAdapter;
    private DatabaseHelper dbHelper;
    private List<PantryItem> pantryList;
    private FloatingActionButton fabAddItem;
    private Button btnSuggestRecipes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHelper = new DatabaseHelper(this);

        rvPantry = findViewById(R.id.rvPantry);
        fabAddItem = findViewById(R.id.fabAddItem);
        btnSuggestRecipes = findViewById(R.id.btnSuggestRecipes);

        rvPantry.setLayoutManager(new LinearLayoutManager(this));
        pantryList = new ArrayList<>();

        loadPantryItems();

        fabAddItem.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AddEditItemActivity.class);
            startActivity(intent);
        });

        btnSuggestRecipes.setOnClickListener(v -> generateRecipeSuggestions());
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadPantryItems();
    }

    private void loadPantryItems() {
        pantryList = dbHelper.getAllPantryItems();
        pantryAdapter = new PantryAdapter(this, pantryList, dbHelper);
        rvPantry.setAdapter(pantryAdapter);
    }

    private void generateRecipeSuggestions() {
        List<Recipe> availableRecipes = dbHelper.getAllRecipes();
        List<Recipe> matchingRecipes = new ArrayList<>();

        for (Recipe recipe : availableRecipes) {
            boolean canMake = true;
            for (RecipeIngredient req : recipe.getIngredients()) {
                double currentQty = dbHelper.getPantryQuantityByName(req.getIngredientName());
                if (currentQty < req.getRequiredQuantity()) {
                    canMake = false;
                    break;
                }
            }
            if (canMake) {
                matchingRecipes.add(recipe);
            }
        }

        if (matchingRecipes.isEmpty()) {
            Toast.makeText(this, "No full recipe matches found based on current pantry stock.", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "Found " + matchingRecipes.size() + " recipe(s) you can make!", Toast.LENGTH_SHORT).show();
        }
    }
}