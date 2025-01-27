package com.mruh.flavourfullfinds;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    private AutoCompleteTextView foodNameInput;
    private TextView recipeTitle;
    private TextView recipeInstructions;
    private ImageView recipeImage;
    private Button searchButton;

    private RequestQueue requestQueue;

    private static final String BASE_URL = "https://www.themealdb.com/api/json/v1/1/";
    private static final String SEARCH_BY_NAME_URL = BASE_URL + "search.php?s=";

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize UI elements
        foodNameInput = findViewById(R.id.foodNameInput);
        searchButton = findViewById(R.id.searchButton);
        recipeTitle = findViewById(R.id.recipeTitle);
        recipeInstructions = findViewById(R.id.recipeInstructions);
        recipeImage = findViewById(R.id.recipeImage);

        // Initialize Volley RequestQueue
        requestQueue = Volley.newRequestQueue(this);

        // Set up the search button click listener
        searchButton.setOnClickListener(v -> {
            String query = foodNameInput.getText().toString().trim();
            if (!query.isEmpty()) {
                searchMealByName(query);
            } else {
                recipeTitle.setText("Please enter a food name.");
                recipeInstructions.setText("");
                recipeImage.setImageResource(android.R.color.transparent);
            }
        });
    }

    private void searchMealByName(String mealName) {
        String url = SEARCH_BY_NAME_URL + mealName;

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                new Response.Listener<JSONObject>() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray meals = response.optJSONArray("meals");
                            if (meals != null && meals.length() > 0) {
                                JSONObject meal = meals.getJSONObject(0);

                                // Extract details
                                String title = meal.optString("strMeal", "No title available");
                                String instructions = meal.optString("strInstructions", "No instructions available");
                                String imageUrl = meal.optString("strMealThumb", "");

                                // Update UI
                                recipeTitle.setText(title);
                                recipeInstructions.setText(instructions);

                                // Load image using Glide
                                if (!imageUrl.isEmpty()) {
                                    Glide.with(MainActivity.this)
                                            .load(imageUrl)
                                            .into(recipeImage);
                                } else {
                                    recipeImage.setImageResource(android.R.color.transparent);
                                }
                            } else {
                                // No meals found
                                recipeTitle.setText("No recipes found.");
                                recipeInstructions.setText("");
                                recipeImage.setImageResource(android.R.color.transparent);
                            }
                        } catch (JSONException e) {
                            Log.e("JSONError", "Error parsing JSON", e);
                            recipeTitle.setText("Error parsing data.");
                        }
                    }
                },
                error -> {
                    Log.e("VolleyError", "Error fetching data", error);
                    recipeTitle.setText("Failed to fetch recipe.");
                    recipeInstructions.setText("");
                    recipeImage.setImageResource(android.R.color.transparent);
                }
        );

        // Add the request to the queue
        requestQueue.add(request);
    }
}
