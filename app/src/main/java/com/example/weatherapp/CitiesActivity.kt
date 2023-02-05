package com.example.weatherapp

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherapp.databinding.ActivityCitiesBinding
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import java.util.*
import kotlin.collections.ArrayList

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
        cityList.add("Test1")
        cityList.add("Test2")
        weatherList.add("15º - Broken Clouds")
        weatherList.add("18º - Sunny")
        imageList.add(R.drawable.clouds)
        imageList.add(R.drawable.sun)
        cityList.add("Test1")
        cityList.add("Test2")
        weatherList.add("15º - Broken Clouds")
        weatherList.add("18º - Sunny")
        imageList.add(R.drawable.clouds)
        imageList.add(R.drawable.sun)
        cityList.add("Test1")
        cityList.add("Test2")
        weatherList.add("15º - Broken Clouds")
        weatherList.add("18º - Sunny")
        imageList.add(R.drawable.clouds)
        imageList.add(R.drawable.sun)
        cityList.add("Test1")
        cityList.add("Test2")
        weatherList.add("15º - Broken Clouds")
        weatherList.add("18º - Sunny")
        imageList.add(R.drawable.clouds)
        imageList.add(R.drawable.sun)
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        val adapter = citiesRecycleViewAdapter(this, modelList)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)
        for (item in cityList) {
            val i = cityList.indexOf(item)
            modelList.add(cityModel(cityList[i], weatherList[i], imageList[i]))
        }
        if (firebaseAuth.currentUser != null) {
            navMenu.findItem(R.id.optRegister).isVisible = false
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
                R.id.optRegister -> {
                    val intent = Intent(this, RegisterActivity::class.java)
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
        if (hour in 6..20) {
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
}