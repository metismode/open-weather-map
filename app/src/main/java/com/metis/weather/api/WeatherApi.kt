package com.metis.weather.api

import com.metis.weather.model.ForecastModel
import com.metis.weather.model.WeatherModel
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApi {

    @GET("/data/2.5/weather")
    fun getWeatherByCity(@Query("q") q:String,@Query("units") unit:String,@Query("appid") appid:String):
            Single<WeatherModel>

    @GET("/data/2.5/onecall")
    fun getForecastHourly(@Query("lat") lat:String,@Query("lon") lon:String,@Query("units") unit:String,@Query("appid") appid:String):
            Single<ForecastModel>
}

