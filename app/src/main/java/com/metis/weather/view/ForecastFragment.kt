package com.metis.weather.view

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.metis.weather.R
import com.metis.weather.adapter.ForecastAdapter
import com.metis.weather.databinding.FragmentForecastBinding
import com.metis.weather.model.ForecastModel
import com.metis.weather.viewmodel.ForecastViewModel

class ForecastFragment : Fragment() {

    private lateinit var binding: FragmentForecastBinding
    private lateinit var viewModel: ForecastViewModel
    private lateinit var adapter: ForecastAdapter

    override fun onResume() {
        super.onResume()
        viewModel.searchWeather()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_forecast, container, false)
        viewModel = ViewModelProvider(this).get(ForecastViewModel::class.java)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        getBundle()
        setupUI()
        setupViewModel()

        return binding.root
    }

    private fun getBundle() {

        viewModel.city.value = arguments!!.getString("city").toString()
        viewModel.lat.value = arguments!!.getString("lat").toString()
        viewModel.lon.value = arguments!!.getString("lon").toString()
        viewModel.units.value = arguments!!.getBoolean("unit")

    }

    private fun setupUI() {
        adapter = ForecastAdapter(
            viewModel.forecast.value?.hourly ?: emptyList(),
            viewModel.city.value!!,
            viewModel.units.value!!
        )
        binding.recyclerViewProduct.layoutManager = LinearLayoutManager(activity as Context)
        binding.recyclerViewProduct.adapter = adapter
    }

    private fun setupViewModel() {

        viewModel.forecast.observe(this, Observer<ForecastModel> {
            binding.status.visibility = View.GONE
            binding.loading.visibility = View.GONE
            it.let {
                adapter.update(it.hourly)
            }
        })

        viewModel.isLoading.observe(this, Observer<Boolean> {
            val visibility = if (it) View.VISIBLE else View.GONE
            binding.loading.visibility = visibility
            binding.status.visibility = View.GONE
        })

        viewModel.isError.observe(this, Observer<Any> {
            binding.status.visibility = View.VISIBLE
            viewModel.setStatus(resources.getString(R.string.error))
        })

        viewModel.isEmpty.observe(this, Observer<Boolean> {
            binding.status.visibility = View.VISIBLE
            viewModel.setStatus(resources.getString(R.string.empty))
        })
    }

}