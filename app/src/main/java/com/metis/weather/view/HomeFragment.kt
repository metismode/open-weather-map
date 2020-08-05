package com.metis.weather.view

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.metis.weather.R
import com.metis.weather.databinding.FragmentHomeBinding
import com.metis.weather.model.WeatherModel
import com.metis.weather.util.Utils
import com.metis.weather.util.hideKeyboard
import com.metis.weather.util.loadImage
import com.metis.weather.viewmodel.MainViewModel
import kotlinx.android.synthetic.main.list_forecast_item.view.*

class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private lateinit var viewModel: MainViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_home, container, false
        )
        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        setupUI()
        setupViewModel()

        return binding.root
    }

    private fun setupUI() {

        binding.sectionShow.visibility = View.GONE
        binding.status.visibility = View.GONE
        binding.loading.visibility = View.GONE
        binding.switchUnits.setOnClickListener {
            viewModel.isCelsius.value = !viewModel.isCelsius.value!!
        }
    }

    private fun setupViewModel() {

        viewModel.isCelsius.value = true
        viewModel.weather.observe(this, Observer<WeatherModel> {
            binding.status.visibility = View.GONE
            binding.loading.visibility = View.GONE
            it.let { it ->
                binding.sectionShow.visibility = View.VISIBLE
                viewModel.temperature.value =
                    it.main.temp.toString() +
                            if (viewModel.isCelsius.value!!) resources.getString(R.string.units_celsius) else {
                                resources.getString(R.string.units_fahrenheit)
                            }
                viewModel.humidity.value =
                    it.main.humidity.toString() + resources.getString(R.string.units_humidity)

                viewModel.date.value = Utils.dateFormat(it.dt)
                viewModel.city.value = it.name
                viewModel.main.value = it.weather[0].main
                viewModel.description.value = it.weather[0].description

                binding.image.loadImage( Utils.imageUrl.replace("[name]",viewModel.weather.value!!.weather[0].icon) )

                binding.forecasts.setOnClickListener {
                    val bundle = bundleOf(
                        "city" to viewModel.weather.value!!.name,
                        "lat" to viewModel.weather.value!!.coord.lat.toString(),
                        "lon" to viewModel.weather.value!!.coord.lon.toString(),
                        "unit" to viewModel.isCelsius.value
                    )
                    it.findNavController()
                        .navigate(R.id.action_homeFragment_to_forecastFragment, bundle)
                }

                hideKeyboard()
            }
        })

        viewModel.isLoading.observe(this, Observer<Boolean> {
            binding.loading.visibility = if (it) View.VISIBLE else View.GONE
            binding.status.visibility = View.GONE
            binding.sectionShow.visibility = if (!it) View.VISIBLE else View.GONE
        })

        viewModel.isError.observe(this, Observer<Any> {
            binding.status.visibility = View.VISIBLE
            binding.sectionShow.visibility = View.GONE
            viewModel.setStatus(resources.getString(R.string.error))
        })

        viewModel.isEmpty.observe(this, Observer<Boolean> {
            if(it){
                binding.status.visibility = View.VISIBLE
                binding.sectionShow.visibility = View.GONE
                viewModel.setStatus(resources.getString(R.string.empty))
            }
        })

        viewModel.isCelsius.observe(this, Observer<Boolean> {
            if (it) {
                binding.switchUnits.text = resources.getString(R.string.fahrenheit)
            } else {
                binding.switchUnits.text = resources.getString(R.string.celsius)
            }
        })
    }

}