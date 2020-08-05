package com.metis.weather.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.metis.weather.R
import com.metis.weather.model.ForecastModel
import com.metis.weather.util.Utils
import com.metis.weather.util.loadImage
import kotlinx.android.synthetic.main.list_forecast_header.view.*
import kotlinx.android.synthetic.main.list_forecast_item.view.*


class ForecastAdapter(
    private var forecasts: List<ForecastModel.Hourly>,
    private var city: String,
    private var isCelsius: Boolean
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val TYPE_HEADER = 0
    private val TYPE_ITEM = 1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        val itemView: View =
            LayoutInflater.from(parent.context).inflate(R.layout.list_forecast_item, parent, false)
        val headerView: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_forecast_header, parent, false)

        return when (viewType) {
            TYPE_ITEM -> {
                MyViewHolder(itemView, parent.context)
            }
            TYPE_HEADER -> {
                HeaderViewHolder(headerView)
            }
            else -> MyViewHolder(itemView, parent.context)
        }

    }

    override fun getItemViewType(position: Int): Int {
        return if (position == 0) {
            TYPE_HEADER
        } else TYPE_ITEM
    }

    override fun getItemCount(): Int {
        return forecasts.size + 1
    }

    fun update(data: List<ForecastModel.Hourly>) {
        forecasts = data
        notifyDataSetChanged()
    }

    fun clear() {
        forecasts = emptyList()
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        if (holder.itemViewType == TYPE_ITEM) {
            if (holder is MyViewHolder) {
                holder.bind(forecasts[position - 1],isCelsius)
            }
        } else if (holder.itemViewType == TYPE_HEADER) {
            if (holder is HeaderViewHolder) {
                holder.bind(city)
            }
        }

    }
}


class MyViewHolder(val view: View, val context: Context) : RecyclerView.ViewHolder(view) {

    fun bind(forecast: ForecastModel.Hourly, isCelsius: Boolean) {

        view.temperature.text = forecast.temp.toString() +
                if (isCelsius) context.resources.getString(R.string.units_celsius) else {
                    context.resources.getString(R.string.units_fahrenheit)
                }
        view.humidity.text = forecast.humidity.toString() + context.resources.getString(R.string.units_humidity)
        view.main.text = forecast.weather[0].main
        view.description.text = forecast.weather[0].description
        view.image.loadImage(Utils.imageUrl.replace("[name]", forecast.weather[0].icon))
        view.date.text = Utils.dateFormat(forecast.dt)

    }
}

class HeaderViewHolder(val view: View) : RecyclerView.ViewHolder(view) {

    fun bind(string: String) {
        view.city.text = string
    }

}

