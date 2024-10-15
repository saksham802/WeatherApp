package com.sak.weatherapp;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface AppApi {
    @GET("weather")
    Call<WeatherApp> getWeatherData(
            @Query("q") String city,
            @Query("appid") String appid,
            @Query("units") String units
    );
}
