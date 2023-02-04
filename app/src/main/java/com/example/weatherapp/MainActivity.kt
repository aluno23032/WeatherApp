package com.example.weatherapp

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TableLayout
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.weatherapp.databinding.ActivityMainBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import org.json.JSONObject
import java.util.*


class MainActivity : AppCompatActivity() {
    private var weatherUrl = ""
    private var weatherUrl2 = ""
    private var weatherUrl3 = ""
    private var apikey = "6ed6199c40174da593a7091e683ace8d"
    private lateinit var tableLayout: TableLayout
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    private lateinit var rightNow: Calendar
    private lateinit var city: TextView
    private lateinit var temperature: TextView
    private lateinit var weather: TextView
    private lateinit var peaks: TextView
    private lateinit var day1: TextView
    private lateinit var day2: TextView
    private lateinit var day3: TextView
    private lateinit var day4: TextView
    private lateinit var day5: TextView
    private lateinit var day6: TextView
    private lateinit var day7: TextView
    private lateinit var imgday1: ImageView
    private lateinit var imgday2: ImageView
    private lateinit var imgday3: ImageView
    private lateinit var imgday4: ImageView
    private lateinit var imgday5: ImageView
    private lateinit var imgday6: ImageView
    private lateinit var imgday7: ImageView
    private lateinit var peaksday1: TextView
    private lateinit var peaksday2: TextView
    private lateinit var peaksday3: TextView
    private lateinit var peaksday4: TextView
    private lateinit var peaksday5: TextView
    private lateinit var peaksday6: TextView
    private lateinit var peaksday7: TextView
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var binding: ActivityMainBinding

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        firebaseAuth = FirebaseAuth.getInstance()
        tableLayout = findViewById(R.id.tableLayout)
        tableLayout.visibility = View.INVISIBLE
        drawerLayout = findViewById(R.id.drawerLayout)
        navigationView = findViewById(R.id.navigationView)
        val navMenu: Menu = navigationView.menu
        val headerView = navigationView.getHeaderView(0)
        val navUsername = headerView.findViewById<TextView>(R.id.username)
        val header = headerView.findViewById<LinearLayout>(R.id.header)
        val email = firebaseAuth.currentUser?.email.toString()
        val index: Int = email.indexOf('@')
        if (firebaseAuth.currentUser != null) {
            navMenu.findItem(R.id.optRegister).isVisible = false
            navMenu.findItem(R.id.optLogin).isVisible = false
            navUsername.text = email.substring(0,index)
        } else {
            navMenu.findItem(R.id.optCities).isVisible = false
            navMenu.findItem(R.id.optLogout).isVisible = false
            navUsername.text = "Guest"
        }
        navigationView.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.optHome -> {
                    drawerLayout.closeDrawer(GravityCompat.START)
                }
                R.id.optRegister -> {
                    val signupIntent = Intent(this, RegisterActivity::class.java)
                    startActivity(signupIntent)
                    drawerLayout.closeDrawer(GravityCompat.START)
                }
                R.id.optLogin -> {
                    val signupIntent = Intent(this, LoginActivity::class.java)
                    startActivity(signupIntent)
                    drawerLayout.closeDrawer(GravityCompat.START)
                }
                R.id.optLogout -> {
                    firebaseAuth.signOut()
                    val signupIntent = Intent(this, MainActivity::class.java)
                    startActivity(signupIntent)
                    drawerLayout.closeDrawer(GravityCompat.START)
                }
            }
            true
        }
        val menu = findViewById<ImageView>(R.id.menu)
        menu.setOnClickListener{
            drawerLayout.openDrawer(GravityCompat.START)
        }
        supportActionBar?.hide()
        rightNow = Calendar.getInstance()
        val hour: Int = rightNow.get(Calendar.HOUR_OF_DAY)
        if (hour !in 6..20) {
            header.setBackgroundColor(Color.parseColor("#A7D8FF"))
            drawerLayout.setBackgroundResource(R.drawable.bggradientday)
            tableLayout.setBackgroundResource(R.drawable.rectangleday)
        }
        weekDays()
        //create an instance of the Fused Location Provider Client
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        obtainLocation()
        tableLayout.visibility = View.VISIBLE
    }

    private fun weekDays() {
        city = findViewById(R.id.city)
        temperature = findViewById(R.id.temperature)
        weather = findViewById(R.id.weather)
        peaks = findViewById(R.id.peaks)
        day1 = findViewById(R.id.day1)
        day2 = findViewById(R.id.day2)
        day3 = findViewById(R.id.day3)
        day4 = findViewById(R.id.day4)
        day5 = findViewById(R.id.day5)
        day6 = findViewById(R.id.day6)
        day7 = findViewById(R.id.day7)
        imgday1 = findViewById(R.id.imgday1)
        imgday2 = findViewById(R.id.imgday2)
        imgday3 = findViewById(R.id.imgday3)
        imgday4 = findViewById(R.id.imgday4)
        imgday5 = findViewById(R.id.imgday5)
        imgday6 = findViewById(R.id.imgday6)
        imgday7 = findViewById(R.id.imgday7)
        peaksday1 = findViewById(R.id.peaksday1)
        peaksday2 = findViewById(R.id.peaksday2)
        peaksday3 = findViewById(R.id.peaksday3)
        peaksday4 = findViewById(R.id.peaksday4)
        peaksday5 = findViewById(R.id.peaksday5)
        peaksday6 = findViewById(R.id.peaksday6)
        peaksday7 = findViewById(R.id.peaksday7)
        var day = rightNow.get(Calendar.DAY_OF_WEEK)
        day2.text = getDay(day)
        rightNow.add(Calendar.DATE, 1)
        day = rightNow.get(Calendar.DAY_OF_WEEK)
        day3.text = getDay(day)
        rightNow.add(Calendar.DATE, 1)
        day = rightNow.get(Calendar.DAY_OF_WEEK)
        day4.text = getDay(day)
        rightNow.add(Calendar.DATE, 1)
        day = rightNow.get(Calendar.DAY_OF_WEEK)
        day5.text = getDay(day)
        rightNow.add(Calendar.DATE, 1)
        day = rightNow.get(Calendar.DAY_OF_WEEK)
        day6.text = getDay(day)
        rightNow.add(Calendar.DATE, 1)
        day = rightNow.get(Calendar.DAY_OF_WEEK)
        day7.text = getDay(day)
    }

    @SuppressLint("SimpleDateFormat")
    @RequiresApi(Build.VERSION_CODES.O)
    private fun obtainLocation(){
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
                weatherUrl = "https://api.weatherbit.io/v2.0/current?lang=" + Locale.getDefault().language + "&lat=" + location?.latitude +"&lon="+ location?.longitude + "&key="+ apikey
                Log.d("query1", weatherUrl)
                weatherUrl2 = "https://api.weatherbit.io/v2.0/forecast/daily?lat=" + location?.latitude +"&lon="+ location?.longitude + "&key="+ apikey + "&lang=" + Locale.getDefault().language + "&days=7"
                Log.d("query2", weatherUrl2)
                //this function will fetch data from URL
                getTemp()
            }
    }

    private fun getDay(day: Int): String{
        return when (day) {
            1 -> (getString(R.string.monday))
            2 -> (getString(R.string.tuesday))
            3 -> (getString(R.string.wednesday))
            4 -> (getString(R.string.thursday))
            5 -> (getString(R.string.friday))
            6 -> (getString(R.string.saturday))
            7 -> (getString(R.string.sunday))
            else -> {
                ("Error.")
            }
        }
    }

    private fun getImage(weather: String): Int{
        return when (weather) {
            "Broken clouds" -> (R.drawable.clouds)
            "Nuvens quebradas" -> (R.drawable.clouds)
            "Scattered clouds" -> (R.drawable.clouds)
            "Nuvens dispersas" -> (R.drawable.clouds)
            "Flurries" -> (R.drawable.snow)
            "Neve fraca" -> (R.drawable.snow)
            "Mix snow/rain" -> (R.drawable.snow)
            "Mistura neve/chuva" -> (R.drawable.snow)
            "Light snow" -> (R.drawable.snow)
            "Céu limpo" -> (R.drawable.sun)
            "Clear Sky" -> (R.drawable.sun)
            "Few clouds" -> (R.drawable.fewclouds)
            "Poucas nuvens" -> (R.drawable.fewclouds)
            "Light shower rain" -> (R.drawable.rain)
            "Light rain" -> (R.drawable.rain)
            "Chuva forte" -> (R.drawable.rain)
            "Heavy rain" -> (R.drawable.rain)
            "Tempestade com chuva forte" -> (R.drawable.storm)
            "Thunderstorm with heavy rain" -> (R.drawable.storm)
            "Tempestade com chuva" -> (R.drawable.storm)
            "Thunderstorm with rain" -> (R.drawable.storm)
            else -> {
                (R.drawable.storm)
            }
        }
    }

    private fun getTemp() {
        // Instantiate the RequestQueue.
        val queue = Volley.newRequestQueue(this)
        val url: String = weatherUrl
        val url2: String = weatherUrl2
        weatherUrl3
        // Request a string response from the provided URL.
        val stringReq = StringRequest(
            Request.Method.GET, url,
            { response ->
                //get the JSON object
                val obj = JSONObject(response)
                //get the Array from obj of name - "data"
                val arr = obj.getJSONArray("data")
                Log.d("obj1", arr.toString())
                //get the JSON object from the array at index position 0
                val obj2 = arr.getJSONObject(0)
                val arr2 = obj2.getJSONObject("weather")
                //set the temperature and the city name using getString() function
                city.text = obj2.getString("city_name")
                temperature.text = getString(R.string.temperature, obj2.getString("temp"))
                weather.text = arr2["description"].toString()
            },
            //In case of any error
            {tableLayout.visibility = View.INVISIBLE})
        stringReq.retryPolicy = DefaultRetryPolicy(1000,
            0,  // maxNumRetries = 0 means no retry
            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
        queue.add(stringReq)
        val stringReq2 = StringRequest(
            Request.Method.GET, url2,
            { response ->
                //get the JSON object
                val obj = JSONObject(response)
                //get the Array from obj of name - "data"
                val arr = obj.getJSONArray("data")
                Log.d("obj2", arr.toString())
                //get the JSON object from the array at index position 0
                val obj2 = arr.getJSONObject(0)
                val arr2 = obj2.getJSONObject("weather")
                val obj3 = arr.getJSONObject(1)
                val arr3 = obj3.getJSONObject("weather")
                val obj4 = arr.getJSONObject(2)
                val arr4 = obj4.getJSONObject("weather")
                val obj5 = arr.getJSONObject(3)
                val arr5 = obj5.getJSONObject("weather")
                val obj6 = arr.getJSONObject(4)
                val arr6 = obj6.getJSONObject("weather")
                val obj7 = arr.getJSONObject(5)
                val arr7 = obj7.getJSONObject("weather")
                val obj8 = arr.getJSONObject(6)
                val arr8 = obj8.getJSONObject("weather")
                //set the temperature and the city name using getString() function
                peaks.text = getString(R.string.peaks, obj2.getString("min_temp"), obj2.getString("max_temp"))
                peaksday1.text = getString(R.string.peaks, obj2.getString("min_temp"), obj2.getString("max_temp"))
                peaksday2.text = getString(R.string.peaks, obj3.getString("min_temp"), obj3.getString("max_temp"))
                peaksday3.text = getString(R.string.peaks, obj4.getString("min_temp"), obj4.getString("max_temp"))
                peaksday4.text = getString(R.string.peaks, obj5.getString("min_temp"), obj5.getString("max_temp"))
                peaksday5.text = getString(R.string.peaks, obj6.getString("min_temp"), obj6.getString("max_temp"))
                peaksday6.text = getString(R.string.peaks, obj7.getString("min_temp"), obj7.getString("max_temp"))
                peaksday7.text = getString(R.string.peaks, obj8.getString("min_temp"), obj8.getString("max_temp"))
                imgday1.setImageResource(getImage(arr2["description"].toString()))
                imgday2.setImageResource(getImage(arr3["description"].toString()))
                imgday3.setImageResource(getImage(arr4["description"].toString()))
                imgday4.setImageResource(getImage(arr5["description"].toString()))
                imgday5.setImageResource(getImage(arr6["description"].toString()))
                imgday6.setImageResource(getImage(arr7["description"].toString()))
                imgday7.setImageResource(getImage(arr8["description"].toString()))
            },
            //In case of any error
            {tableLayout.visibility = View.INVISIBLE})
        queue.add(stringReq2)
    }
}