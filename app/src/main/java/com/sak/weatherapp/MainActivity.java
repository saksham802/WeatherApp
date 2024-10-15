package com.sak.weatherapp;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.airbnb.lottie.LottieAnimationView;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    String api_key = "224d63f85721c1784db246e5c8b8b6a4";
    String url = "https://api.openweathermap.org/data/2.5/";
    String tempurl, city = "", wind;
    double temp, min_temp, max_temp;
    int humidity, sealevel, weather_id, timezone;
    TextView T_temp, T_tempmin, T_tempmax, T_humidity, T_wind, T_sea, Loc, weather;
    ArrayAdapter<String> adapter;
    ArrayList<String> list;
    ListView listView;
    SearchView searchView;
    LottieAnimationView iconWeather;
    ZoneOffset zoneOffset;
    ImageView back,setting;
    FusedLocationProviderClient fusedLocationProviderClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        T_temp = findViewById(R.id.textView2);
        T_humidity = findViewById(R.id.textView8);
        T_wind = findViewById(R.id.textView6);

        T_sea = findViewById(R.id.textView10);
        Loc = findViewById(R.id.textView);
        weather = findViewById(R.id.textView3);
        iconWeather = findViewById(R.id.lottieAnimationView);
        setting=findViewById(R.id.imageView6);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        getLastLocation();

        Dialog loc = new Dialog(MainActivity.this);
        Loc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loc.setContentView(R.layout.location);
                searchView = loc.findViewById(R.id.searchView);
                listView = loc.findViewById(R.id.listView);
                back=loc.findViewById(R.id.imageView);
                back.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        loc.dismiss();
                    }
                });

                String[] statesArray = getResources().getStringArray(R.array.states);
                list = new ArrayList<>(Arrays.asList(statesArray));

                adapter = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_list_item_1, list);
                listView.setAdapter(adapter);

                searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                    @Override
                    public boolean onQueryTextSubmit(String s) {
                        String temp_string = s.toUpperCase();
                        if (list.contains(temp_string)) {
                            adapter.getFilter().filter(temp_string);
                        } else {
                            Toast.makeText(MainActivity.this, "Not Found", Toast.LENGTH_SHORT).show();
                        }
                        return false;
                    }

                    @Override
                    public boolean onQueryTextChange(String s) {
                        return false;
                    }
                });

                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        city = list.get(i).toLowerCase();
                        String cap = city.substring(0, 1).toUpperCase() + city.substring(1);

                        Loc.setText(cap);
                        loc.dismiss();
                        fetchWeatherData();
                    }
                });

                loc.getWindow().getAttributes().windowAnimations = R.style.animation;
                loc.setCancelable(true);
                loc.show();
            }
        });
        setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this,Setting.class);
                startActivity(i);
                finish();
            }
        });
    }

    private void fetchWeatherData() {
        tempurl = url + "weather?q=" + city + "&appid=" + api_key;
        StringRequest request = new StringRequest(Request.Method.GET, tempurl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonResponse = new JSONObject(response);

                    JSONArray jsonArray = jsonResponse.getJSONArray("weather");
                    JSONObject jsonObject = jsonArray.getJSONObject(0);
                    weather_id = jsonObject.getInt("id");
                    timezone = jsonResponse.getInt("timezone");

                    JSONObject mainObject = jsonResponse.getJSONObject("main");
                    min_temp = mainObject.getDouble("temp_min") - 273.15;
                    max_temp = mainObject.getDouble("temp_max") - 273.15;
                    temp = mainObject.getDouble("temp") - 273.15;
                    humidity = mainObject.getInt("humidity");
                    sealevel = mainObject.getInt("sea_level");

                    JSONObject windObject = jsonResponse.getJSONObject("wind");
                    wind = windObject.getString("speed");



                    iconChanger(weather_id, timezone);
                    T_temp.setText(String.format("%d°", (int) temp));
                    T_humidity.setText(String.valueOf(humidity) + "%");
                    T_wind.setText(wind + "m/s");

                    T_sea.setText(String.format("%d hPa", sealevel));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity.this, "Error", Toast.LENGTH_SHORT).show();
            }
        });

        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(request);
    }

    private void getLastLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if (location != null) {
                        try {
                            Geocoder geocoder = new Geocoder(MainActivity.this, Locale.getDefault());
                            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                            city = addresses.get(0).getLocality();
                            Loc.setText(city);
                            fetchWeatherData();  // Fetch weather data after getting location
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
        } else {
            askPermission();
        }
    }

    private void askPermission() {
        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 100);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 100) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLastLocation();
            } else {
                Toast.makeText(this, "Please provide Permission", Toast.LENGTH_SHORT).show();
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void iconChanger(int temp_id, int timezone) {
        int hour;
        zoneOffset = ZoneOffset.ofTotalSeconds(timezone);
        ZonedDateTime zonedDateTime = ZonedDateTime.ofInstant(Instant.now(), zoneOffset);
        hour = zonedDateTime.getHour();

        if (temp_id == 800) {
            weather.setText("Clear");
            if(hour>=19||hour<4){
                iconWeather.setAnimation(R.raw.clear_night);
            }
            else{
                iconWeather.setAnimation(R.raw.sunny);
            }
        } else if (temp_id > 800 && temp_id < 805) {
            weather.setText("Cloudy");
            if(hour>=19||hour<4){
                iconWeather.setAnimation(R.raw.night_cloudy);
            }
            else{
                iconWeather.setAnimation(R.raw.cloudyy);
            }

        } else if (temp_id >= 200 && temp_id < 233) {
            weather.setText("Thunderstorm");
            if(hour>=19||hour<4){

            }
            else{

            iconWeather.setAnimation(R.raw.thunderstorm);}
        } else if (temp_id >= 300 && temp_id < 322) {
            weather.setText("Drizzle");
            if(hour>=19||hour<4){
                iconWeather.setAnimation(R.raw.night_rain);
            }
            else{
                iconWeather.setAnimation(R.raw.raining);
            }

        } else if (temp_id >= 500 && temp_id < 532) {
            weather.setText("Rain");
            if(hour>=19||hour<4){
                iconWeather.setAnimation(R.raw.night_rain);
            }
            else{
                iconWeather.setAnimation(R.raw.raining);
            }

        } else if (temp_id >= 600 && temp_id < 623) {
            weather.setText("Snow");
            if(hour>=19||hour<4){
                iconWeather.setAnimation(R.raw.night_snow);
            }
            else{
                iconWeather.setAnimation(R.raw.snow);
            }

        }

        iconWeather.playAnimation();
    }
}
