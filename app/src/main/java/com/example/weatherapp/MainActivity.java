package com.example.weatherapp;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {
    private EditText locationInput;
    private TextView temperatureText, humidityText, windSpeedText, weatherConditionText;
    private ImageView weatherImage;
    private Button searchButton, unitSwitchButton;

    private boolean isCelsius = true;

    private static final String API_KEY = "388af0e50d07403fdfcd8771638b1e76";
    private static final String API_URL = "https://api.openweathermap.org/data/2.5/weather?q=%s&appid=%s&units=%s";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // Handle window insets for edge-to-edge displays
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize UI components
        locationInput = findViewById(R.id.locationInput);
        temperatureText = findViewById(R.id.temperatureText);
        humidityText = findViewById(R.id.humidityText);
        windSpeedText = findViewById(R.id.windSpeedText);
        weatherConditionText = findViewById(R.id.weatherConditionText);
        weatherImage = findViewById(R.id.weatherImage);
        searchButton = findViewById(R.id.searchButton);
        unitSwitchButton = findViewById(R.id.unitSwitchButton);

        // Set up button listeners
        searchButton.setOnClickListener(v -> {
            String location = locationInput.getText().toString().trim();
            if (!location.isEmpty()) {
                fetchWeatherData(location);
            } else {
                Toast.makeText(MainActivity.this, "Please enter a location", Toast.LENGTH_SHORT).show();
            }
        });

        unitSwitchButton.setOnClickListener(v -> {
            isCelsius = !isCelsius;
            unitSwitchButton.setText(isCelsius ? "Switch to Fahrenheit" : "Switch to Celsius");
        });
    }

    private void fetchWeatherData(String location) {
        String unit = isCelsius ? "metric" : "imperial";
        String url = String.format(API_URL, location, API_KEY, unit);

        RequestQueue queue = Volley.newRequestQueue(this);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONObject main = response.getJSONObject("main");
                            JSONObject wind = response.getJSONObject("wind");
                            String weatherDescription = response.getJSONArray("weather").getJSONObject(0).getString("description");

                            double temperature = main.getDouble("temp");
                            int humidity = main.getInt("humidity");
                            double windSpeed = wind.getDouble("speed");

                            // Update UI with weather data
                            temperatureText.setText(String.format("%.1f °%s", temperature, isCelsius ? "C" : "F"));
                            humidityText.setText("Humidity: " + humidity + "%");
                            windSpeedText.setText("Wind Speed: " + windSpeed + " m/s");
                            weatherConditionText.setText(weatherDescription);

                            // Log the weather description for debugging
                            Log.d("WeatherDescription", weatherDescription);

                            // Update the weather image based on the description
                            updateWeatherImage(weatherDescription);

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(MainActivity.this, "Error parsing weather data", Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(MainActivity.this, "Error fetching weather data", Toast.LENGTH_SHORT).show();
                    }
                });

        queue.add(jsonObjectRequest);
    }

    private void updateWeatherImage(String description) {
        if (description.contains("clear")) {
            weatherImage.setImageResource(R.drawable.snoopy); // sunny
        } else if (description.contains("cloud")) {
            weatherImage.setImageResource(R.drawable.cloudy); // cloudy
        } else if (description.contains("rain")) {
            weatherImage.setImageResource(R.drawable.rainy); // rainy
        } else {
            weatherImage.setImageResource(R.drawable.mainp); // default image
        }
    }
}