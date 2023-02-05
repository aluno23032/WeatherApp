package com.example.weatherapp

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.example.weatherapp.databinding.ActivityAddCityBinding
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVParser
import java.io.BufferedReader
import java.util.*


class AddCityActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddCityBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    private lateinit var headerView: View
    private lateinit var spinnerCountry: Spinner
    private lateinit var spinnerCity: Spinner
    private lateinit var country: String
    private lateinit var city: String
    private var flag = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddCityBinding.inflate(layoutInflater)
        setContentView(binding.root)
        firebaseAuth = FirebaseAuth.getInstance()
        drawerLayout = findViewById(R.id.drawerLayout)
        navigationView = findViewById(R.id.navigationView)
        headerView = navigationView.getHeaderView(0)
        spinnerCountry = findViewById(R.id.country)
        spinnerCity = findViewById(R.id.city)
        val addBtn = findViewById<Button>(R.id.addBtn)
        setupMenu()
        dayColors()
        setCountries()
        addListenerCountry()
        addListenerCity()
        addBtn.setOnClickListener {
            val file = "$city, $country"
            try {
                val fos = openFileOutput(file, Context.MODE_PRIVATE)
                fos.write("".toByteArray())
                val intent = Intent(this, CitiesActivity::class.java)
                startActivity(intent)
            }catch (e: Exception){
                e.printStackTrace()
            }
        }
    }

    private fun addListenerCountry() {
        spinnerCountry.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parentView: AdapterView<*>?,
                selectedItemView: View?,
                position: Int,
                id: Long
            ) {
                if (flag == 1) {
                    country = spinnerCountry.selectedItem.toString()
                    setCities(country)
                } else {
                    flag = 1
                }
            }

            override fun onNothingSelected(parentView: AdapterView<*>?) {
            }
        }
    }

    private fun addListenerCity() {
        spinnerCity.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parentView: AdapterView<*>?,
                selectedItemView: View?,
                position: Int,
                id: Long
            ) {
                city = spinnerCity.selectedItem.toString()
            }

            override fun onNothingSelected(parentView: AdapterView<*>?) {
                // your code here
            }
        }
    }

    private fun setCities(country: String){
        Log.d("teste", country)
        val arrayCity = ArrayList<String>()
        val cityReader = BufferedReader(assets.open("cities.csv").reader())
        val cityParser =
            CSVParser.parse(cityReader, CSVFormat.EXCEL.withFirstRecordAsHeader().withQuote(null))
        cityParser.forEach {
            if (it[4].equals(country) && !it[1].equals("") && !it[1].contains("Ã")) {
                arrayCity.add(it[1])
            }
        }
        arrayCity.sort()
        val adapterCity = ArrayAdapter(this, android.R.layout.simple_spinner_item, arrayCity)
        adapterCity.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerCity.adapter = adapterCity
    }

    private fun setCountries() {
        val arrayCountry = ArrayList<String>()
        val countryReader = BufferedReader(assets.open("countries.csv").reader())
        val countryParser =
            CSVParser.parse(
                countryReader,
                CSVFormat.EXCEL.withFirstRecordAsHeader().withQuote(null)
            )
        countryParser.forEach {
            if (!it[1].equals("") && !it[1].contains("Ã")) {
                arrayCountry.add(it[1])
            }
        }
        val adapterCountry = ArrayAdapter(this, android.R.layout.simple_spinner_item, arrayCountry)
        adapterCountry.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerCountry.adapter = adapterCountry
    }

    private fun setupMenu() {
        val navMenu: Menu = navigationView.menu
        val navUsername = headerView.findViewById<TextView>(R.id.username)
        val email = firebaseAuth.currentUser?.email.toString()
        val index: Int = email.indexOf('@')
        if (firebaseAuth.currentUser != null) {
            navMenu.findItem(R.id.optLogin).isVisible = false
            navUsername.text = email.substring(0, index)
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
                    val intent = Intent(this, CitiesActivity::class.java)
                    startActivity(intent)
                    drawerLayout.closeDrawer(GravityCompat.START)
                }
                R.id.optCreators -> {
                    val intent = Intent(this, CreatorsActivity::class.java)
                    startActivity(intent)
                    drawerLayout.closeDrawer(GravityCompat.START)
                }
            }
            true
        }
        val menu = findViewById<ImageView>(R.id.menu)
        menu.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }
    }

    private fun dayColors() {
        val rightNow = Calendar.getInstance()
        val hour: Int = rightNow.get(Calendar.HOUR_OF_DAY)
        val header = headerView.findViewById<LinearLayout>(R.id.header)
        if (hour in 7..19) {
            header.setBackgroundColor(Color.parseColor("#A7D8FF"))
            drawerLayout.setBackgroundResource(R.drawable.bggradientday)
        }
    }
}