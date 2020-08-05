package com.metis.weather.di

import com.metis.weather.api.WeatherService
import com.metis.weather.viewmodel.ForecastViewModel
import com.metis.weather.viewmodel.MainViewModel
import dagger.Component

@Component(modules = [ApiModule::class])
interface ApiComponent {

    fun inject(service: WeatherService)

    fun inject(viewModel: MainViewModel)

    fun inject(viewModel: ForecastViewModel)
}