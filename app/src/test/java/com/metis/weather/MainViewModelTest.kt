package com.metis.weather

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.metis.weather.api.WeatherService
import com.metis.weather.model.WeatherModel
import com.metis.weather.util.Utils
import com.metis.weather.viewmodel.MainViewModel
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

class MainViewModelTest {

    @get:Rule
    var rule = InstantTaskExecutorRule()

    @Mock
    lateinit var weatherService: WeatherService

    @InjectMocks
    var mainViewModel = MainViewModel()

    private var testSingle: Single<WeatherModel>? = null

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)
    }

    @Test
    fun getWeatherSuccess() {

        val weather = Mockito.mock(WeatherModel::class.java)
        testSingle = Single.just(weather)
        `when`(weatherService.getWeatherByCity("bangkok","Metric",Utils.apiKey)).thenReturn(testSingle)
        mainViewModel.cityName.value = "bangkok"
        mainViewModel.isCelsius.value = true
        mainViewModel.searchWeather()
        Assert.assertNotNull( mainViewModel.weather.value)
        Assert.assertEquals(false, mainViewModel.isLoading.value)
        Assert.assertNull(mainViewModel.isError.value)
    }

    @Test
    fun getWeatherFail() {
        testSingle = Single.error(Throwable())
        `when`(weatherService.getWeatherByCity("bangkok","Metric",Utils.apiKey)).thenReturn(testSingle)
        mainViewModel.cityName.value = "bangkok"
        mainViewModel.isCelsius.value = true
        mainViewModel.searchWeather()
        Assert.assertEquals(false, mainViewModel.isLoading.value)
        Assert.assertNotNull(mainViewModel.isError.value)
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