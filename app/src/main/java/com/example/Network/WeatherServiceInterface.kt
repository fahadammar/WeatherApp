package com.example.Network

// MODEL
import com.example.API_Models.WeatherResponse

// RETROFIT
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherServiceInterface {
    @GET("2.5/weather")
    fun getWeather(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("units") units: String?,
        @Query("appid") appid: String?
    ) : Call<WeatherResponse>
}

/**
 * Query parameters are a defined set of parameters attached to the end of a url.
 * They are extensions of the URL that are used to help define specific content or actions
 * based on the data being passed.
 * */