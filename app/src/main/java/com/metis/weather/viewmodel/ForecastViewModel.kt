package com.metis.weather.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
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
    val units = MutableLiveData<Boolean>()

    fun searchWeather() {
        val units = if (units.value!!) "Metric" else "Imperial"
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
                        if (value==null) {
                            isEmpty.value = true
                        } else {
                            forecast.value = value
                        }
                    }
                    override fun onError(e: Throwable) {
                        isLoading.value = false
                        val error: HttpException = e as HttpException
                        val code = error.code()
                        when (code) {
                            404 -> {
                                isEmpty.value = true
                            }
                            else -> {
                                isError.value = e.message.toString()
                            }
                        }

                    }
                })
        )
    }

    fun setStatus(msg:String){
        status.value = msg
    }

    fun click(forecast: ForecastModel.Hourly, context: Context) {

    }

    override fun onCleared() {
        super.onCleared()
        disposable.clear()
    }

}