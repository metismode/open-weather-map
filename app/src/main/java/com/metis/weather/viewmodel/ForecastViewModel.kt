package com.metis.weather.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.metis.weather.api.WeatherService
import com.metis.weather.di.DaggerApiComponent
import com.metis.weather.model.ForecastModel
import com.metis.weather.util.Utils
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import retrofit2.HttpException
import javax.inject.Inject


class ForecastViewModel : ViewModel()  {

    @Inject
    lateinit var weatherService: WeatherService

    init {
        DaggerApiComponent.create().inject(this)
    }

    private val disposable = CompositeDisposable()

    val forecast = MutableLiveData<ForecastModel>()
    val isLoading = MutableLiveData<Boolean>()
    val isError = MutableLiveData<Any>()
    val isEmpty = MutableLiveData<Boolean>()
    val status = MutableLiveData<String>()

    val city = MutableLiveData<String>()
    val lat = MutableLiveData<String>()
    val lon = MutableLiveData<String>()
    val isCelsius = MutableLiveData<Boolean>()

    fun searchWeather() {
        val units = if (isCelsius.value!!) "Metric" else "Imperial"
        fetchForecast(lat.value!!,lon.value!!,units, Utils.apiKey)
    }

    private fun fetchForecast(lat: String,lon: String,units:String,apikey:String) {


        isLoading.value = true
        disposable.add(
            weatherService.getForecastHourly(lat,lon,units,apikey)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object :
                    DisposableSingleObserver<ForecastModel>() {
                    override fun onSuccess(value: ForecastModel) {
                        isLoading.value = false
                        forecast.value = value
                    }
                    override fun onError(e: Throwable) {
                        isLoading.value = false
                        if (e is HttpException) {
                            when (e.code()) {
                                404 -> {
                                    isEmpty.value = true
                                }
                                else -> {
                                    isError.value = e.message.toString()
                                }
                            }
                        }else{
                            isError.value = e.message.toString()
                        }

                    }
                })
        )
    }

    fun setStatus(msg:String){
        status.value = msg
    }

    override fun onCleared() {
        super.onCleared()
        disposable.clear()
    }

}