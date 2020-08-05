package com.metis.weather.api

import com.metis.weather.di.DaggerApiComponent
import com.metis.weather.model.ForecastModel
import com.metis.weather.model.WeatherModel
import io.reactivex.Single
import javax.inject.Inject

class WeatherService {

    @Inject
    lateinit var api: WeatherApi

    init {
        DaggerApiComponent.create().inject(this)
    }

    fun getWeatherByCity(city: String,units:String, apikey: String): Single<WeatherModel> {
        return api.getWeatherByCity(city, units, apikey)
    }

    fun getForecastHourly(lat: String,lon: String,units:String, apikey: String): Single<ForecastModel> {
        return api.getForecastHourly(lat,lon,units, apikey)
    }

}
