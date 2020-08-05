package com.metis.weather.viewmodel

import androidx.databinding.Bindable
import androidx.databinding.Observable
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.metis.weather.api.WeatherService
import com.metis.weather.di.DaggerApiComponent
import com.metis.weather.model.WeatherModel
import com.metis.weather.util.Utils
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import retrofit2.HttpException
import javax.inject.Inject


class MainViewModel : ViewModel() , Observable {

    @Inject
    lateinit var weatherService: WeatherService

    init {
        DaggerApiComponent.create().inject(this)
    }

    private val disposable = CompositeDisposable()


    @Bindable
    var cityName = MutableLiveData<String>()

    val weather = MutableLiveData<WeatherModel>()
    val isLoading = MutableLiveData<Boolean>()
    val isError = MutableLiveData<Any>()
    val isEmpty = MutableLiveData<Boolean>()
    val isCelsius = MutableLiveData<Boolean>()

    var date = MutableLiveData<String>()
    var city = MutableLiveData<String>()
    var temperature = MutableLiveData<String>()
    var humidity = MutableLiveData<String>()
    var main = MutableLiveData<String>()
    var description = MutableLiveData<String>()
    var status = MutableLiveData<String>()

    fun searchWeather(){
        if (isCelsius.value == null) {
            isCelsius.value = true
        }
        val units = if (isCelsius.value!!) "Metric" else "Imperial"
        fetchWeather(cityName.value.toString(), units, Utils.apiKey)
    }


    private fun fetchWeather(city:String,units:String,apikey:String) {
        isError.value = null
        isLoading.value = true
        disposable.add(
            weatherService.getWeatherByCity(city,units,apikey)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object :
                    DisposableSingleObserver<WeatherModel>() {
                    override fun onSuccess(value: WeatherModel) {
                        isLoading.value = false

                        isEmpty.value = false
                        weather.value = value
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

    fun setTempUnits() {
        isCelsius.value = !isCelsius.value!!
        searchWeather()
    }

    override fun onCleared() {
        super.onCleared()
        disposable.clear()
    }

    override fun removeOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback?) {

    }

    override fun addOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback?) {

    }
}