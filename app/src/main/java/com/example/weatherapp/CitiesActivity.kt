package com.example.weatherapp

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.weatherapp.databinding.ActivityCitiesBinding
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import org.json.JSONObject
import java.util.*

class CitiesActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCitiesBinding
    private lateinit var firebaseAuth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCitiesBinding.inflate(layoutInflater)
        setContentView(binding.root)
        firebaseAuth = FirebaseAuth.getInstance()
        val drawerLayout = findViewById<DrawerLayout>(R.id.drawerLayout)
        val navigationView = findViewById<NavigationView>(R.id.navigationView)
        val navMenu: Menu = navigationView.menu
        val headerView = navigationView.getHeaderView(0)
        val navUsername = headerView.findViewById<TextView>(R.id.username)
        val header = headerView.findViewById<LinearLayout>(R.id.header)
        val email = firebaseAuth.currentUser?.email.toString()
        val index: Int = email.indexOf('@')
        val modelList = ArrayList<cityModel>()
        val cityList = ArrayList<String>()
        val weatherList = ArrayList<String>()
        val imageList = ArrayList<Int>()
        val fileList = filesDir.listFiles()
        for (f in fileList!!) {
            cityList.add(f.name)
            val country = f.name.substring(f.name.indexOf(",") + 1)
            country.trim()
            val city = f.name.substring(0, f.name.indexOf(","))
            //get the latitude and longitude and create the http URL
            val weatherUrl = "https://api.weatherbit.io/v2.0/current?lang=" + Locale.getDefault().language + "&city=" + city + "&country=" + country + "&key=" + "42972b7195ca4ae69e393c77a00f4284"
            Log.d("query1", weatherUrl)
            //this function will fetch data from URL
            val queue = Volley.newRequestQueue(this)
            val stringReq = StringRequest(
                Request.Method.GET, weatherUrl,
                { response ->
                    //get the JSON object
                    val obj = JSONObject(response)
                    //get the Array from obj of name - "data"
                    val arr = obj.getJSONArray("data")
                    Log.d("obj1", arr.toString())
                    //get the JSON object from the array at index position 0
                    val obj2 = arr.getJSONObject(0)
                    val arr2 = obj2.getJSONObject("weather")
                    weatherList.add(getString(R.string.temperature, obj2.getString("temp")) + " - " + arr2["description"].toString())
                    imageList.add(getImage(arr2["description"].toString()))
                },
                //In case of any error
                {})
            stringReq.retryPolicy = DefaultRetryPolicy(1000,
                0,  // maxNumRetries = 0 means no retry
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
            queue.add(stringReq)
        }
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        val adapter = citiesRecycleViewAdapter(this, modelList)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)
        for (item in weatherList) {
            val i = cityList.indexOf(item)
            modelList.add(cityModel(cityList[i], weatherList[i], imageList[i]))
        }
        if (firebaseAuth.currentUser != null) {
            navMenu.findItem(R.id.optLogin).isVisible = false
            navUsername.text = email.substring(0,index)
        } else {
            navMenu.findItem(R.id.optCities).isVisible = false
            navMenu.findItem(R.id.optLogout).isVisible = false
            navUsername.text = getString(R.string.guest)
        }
        navigationView.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.optHome -> {
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    drawerLayout.closeDrawer(GravityCompat.START)
                }
                R.id.optLogin -> {
                    val intent = Intent(this, LoginActivity::class.java)
                    startActivity(intent)
                    drawerLayout.closeDrawer(GravityCompat.START)
                }
                R.id.optLogout -> {
                    firebaseAuth.signOut()
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    drawerLayout.closeDrawer(GravityCompat.START)
                }
                R.id.optCities -> {
                    drawerLayout.closeDrawer(GravityCompat.START)
                }
            }
            true
        }
        val menu = findViewById<ImageView>(R.id.menu)
        menu.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }
        val rightNow = Calendar.getInstance()
        val hour: Int = rightNow.get(Calendar.HOUR_OF_DAY)
        if (hour in 7..19) {
            val btnCreate = findViewById<FloatingActionButton>(R.id.btnCreate)
            btnCreate.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#74C1FF"))
            header.setBackgroundColor(Color.parseColor("#A7D8FF"))
            drawerLayout.setBackgroundResource(R.drawable.bggradientday)
        }
        binding.btnCreate.setOnClickListener {
            val intent = Intent(this, AddCityActivity::class.java)
            startActivity(intent)
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
}