package com.metis.weather

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.metis.weather.api.WeatherService
import com.metis.weather.model.ForecastModel
import com.metis.weather.util.Utils
import com.metis.weather.viewmodel.ForecastViewModel
import io.reactivex.Scheduler
import io.reactivex.Single
import io.reactivex.android.plugins.RxAndroidPlugins
import io.reactivex.disposables.Disposable
import io.reactivex.internal.schedulers.ExecutorScheduler
import io.reactivex.plugins.RxJavaPlugins
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import java.util.concurrent.Executor
import java.util.concurrent.TimeUnit

class ForecastViewModelTest {

    @get:Rule
    var rule = InstantTaskExecutorRule()

    @Mock
    lateinit var weatherService: WeatherService

    @InjectMocks
    var forecastViewModel = ForecastViewModel()

    private var testSingle: Single<ForecastModel>? = null

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)
    }

    @Test
    fun getForecastSuccess() {

        val forecast = Mockito.mock(ForecastModel::class.java)
        testSingle = Single.just(forecast)
        `when`(weatherService.getForecastHourly("100.00","200.00","Metric",Utils.apiKey)).thenReturn(testSingle)
        forecastViewModel.lat.value = "100.00"
        forecastViewModel.lon.value = "200.00"
        forecastViewModel.isCelsius.value = true
        forecastViewModel.searchWeather()
        Assert.assertNotNull( forecastViewModel.forecast.value)
        Assert.assertEquals(false, forecastViewModel.isLoading.value)
        Assert.assertNull(forecastViewModel.isError.value)
    }

    @Test
    fun getForecastFail() {
        testSingle = Single.error(Throwable())
        `when`(weatherService.getForecastHourly("100.00","200.00","Metric",Utils.apiKey)).thenReturn(testSingle)
        forecastViewModel.lat.value = "100.00"
        forecastViewModel.lon.value = "200.00"
        forecastViewModel.isCelsius.value = true
        forecastViewModel.searchWeather()
        Assert.assertEquals(false, forecastViewModel.isLoading.value)
        Assert.assertNotNull(forecastViewModel.isError.value)
    }

    @Before
    fun setUp() {
        val immediate = object : Scheduler() {
            override fun scheduleDirect(run: Runnable, delay: Long, unit: TimeUnit): Disposable {
                return super.scheduleDirect(run, delay, unit)
            }

            override fun createWorker(): Worker {
                return ExecutorScheduler.ExecutorWorker(Executor { it.run() }, false)
            }
        }

        RxJavaPlugins.setInitIoSchedulerHandler { scheduler -> immediate }
        RxJavaPlugins.setInitComputationSchedulerHandler { scheduler -> immediate }
        RxJavaPlugins.setInitNewThreadSchedulerHandler { scheduler -> immediate }
        RxJavaPlugins.setInitSingleSchedulerHandler { scheduler -> immediate }
        RxAndroidPlugins.setInitMainThreadSchedulerHandler { scheduler -> immediate }
    }

}