package com.example.weatherapp

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.icu.text.SimpleDateFormat
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import org.json.JSONObject
import java.util.*


class MainActivity : AppCompatActivity() {
    private var weatherUrl = ""
    private var weatherUrl2 = ""
    private var apikey = "6ed6199c40174da593a7091e683ace8d"
    private lateinit var city: TextView
    private lateinit var temperature: TextView
    private lateinit var weather: TextView
    private lateinit var peaks: TextView
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        getSupportActionBar()?.hide();

        val rightNow = Calendar.getInstance()
        val hour: Int =rightNow.get(Calendar.HOUR_OF_DAY)
        val root = findViewById<RelativeLayout>(R.id.root)
        if (hour !in 6..18) {
            root.setBackgroundResource(R.drawable.bggradientnight)
        } else {
            root.setBackgroundResource(R.drawable.bggradientday)
        }
        city = findViewById(R.id.city)
        temperature = findViewById(R.id.temperature)
        weather = findViewById(R.id.weather)
        peaks = findViewById(R.id.peaks)
        //create an instance of the Fused Location Provider Client
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        Log.e("lat", weatherUrl)
        Log.e("lat", weatherUrl2)
        obtainLocation()
    }
    @SuppressLint("SimpleDateFormat")
    @RequiresApi(Build.VERSION_CODES.O)
    private fun obtainLocation(){
        Log.e("lat", "function")
        //get the last location
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1)
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                //get the latitude and longitude and create the http URL
                weatherUrl = "https://api.weatherbit.io/v2.0/current?" + "lang=" + Locale.getDefault().language + "&lat=" + location?.latitude +"&lon="+ location?.longitude + "&key="+ apikey
                Log.e("lat", weatherUrl)
                val sdf = SimpleDateFormat("yyyy-MM-dd")
                val currentDate = sdf.format(Date())
                Log.e("Date", currentDate)
                weatherUrl2 = "https://api.weatherbit.io/v2.0/history/daily?" + "lat=" + location?.latitude +"&lon="+ location?.longitude + "&start_date="+ currentDate + "&end_date="+ currentDate + "&key="+ apikey
                Log.e("lat", weatherUrl2)
                //this function will fetch data from URL
                getTemp()
            }
    }
    private fun getTemp() {
        // Instantiate the RequestQueue.
        val queue = Volley.newRequestQueue(this)
        val url: String = weatherUrl
        val url2: String = weatherUrl2
        Log.e("lat", url)
        // Request a string response from the provided URL.
        val stringReq = StringRequest(
            Request.Method.GET, url,
            { response ->
                Log.e("lat", response.toString())
                //get the JSON object
                val obj = JSONObject(response)
                //get the Array from obj of name - "data"
                val arr = obj.getJSONArray("data")
                Log.e("lat obj1", arr.toString())
                //get the JSON object from the array at index position 0
                val obj2 = arr.getJSONObject(0)
                Log.e("lat obj2", obj2.toString())
                val arr2 = obj2.getJSONObject("weather")
                //set the temperature and the city name using getString() function
                city.text = obj2.getString("city_name")
                temperature.text = getString(R.string.temperature, obj2.getString("temp"))
                weather.text = arr2["description"].toString()
            },
            //In case of any error
            {})
        queue.add(stringReq)
        val stringReq2 = StringRequest(
            Request.Method.GET, url2,
            { response ->
                Log.e("lat", response.toString())
                //get the JSON object
                val obj = JSONObject(response)
                //get the Array from obj of name - "data"
                val arr = obj.getJSONArray("data")
                Log.e("lat obj1", arr.toString())
                //get the JSON object from the array at index position 0
                val obj2 = arr.getJSONObject(0)
                Log.e("lat obj2", obj2.toString())
                //set the temperature and the city name using getString() function
                peaks.text = getString(R.string.peaks, obj2.getString("min_temp"), obj2.getString("max_temp"))
            },
            //In case of any error
            {})
        queue.add(stringReq2)
    }
}