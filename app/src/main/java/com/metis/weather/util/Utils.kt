package com.metis.weather.util

import android.content.Context
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import java.text.SimpleDateFormat
import java.util.*


object Utils {

    val imageUrl: String = "http://openweathermap.org/img/wn/[name]@2x.png"
    val apiKey: String = "3dad2fbae06625c8cd4b06b99483d82e"

    fun dateFormat(dt: Int): String {
        val date = dt * 1000.toLong()
        val dateFormat = SimpleDateFormat("EEE, d MMM yyyy HH:mm")
        val newDt = Date(date)

        return dateFormat.format(newDt)
    }
}


fun ImageView.loadImage(uri: String?) {
    val options = RequestOptions()
    Glide.with(this.context)
        .setDefaultRequestOptions(options)
        .load(uri)
        .into(this)
}


fun Fragment.hideKeyboard() {
    val view = activity?.currentFocus
    if (view != null) {
        val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }
    // else {
    activity?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
    // }
}