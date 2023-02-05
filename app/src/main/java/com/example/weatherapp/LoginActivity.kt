package com.example.weatherapp

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.widget.*
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.example.weatherapp.databinding.ActivityLoginBinding
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import java.util.*

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var firebaseAuth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
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
            val loginButton = findViewById<Button>(R.id.login_button)
            val signUp = findViewById<TextView>(R.id.signupRedirectText)
            signUp.setTextColor(Color.parseColor("#A7D8FF"))
            loginButton.setBackgroundColor(Color.parseColor("#74C1FF"))
            header.setBackgroundColor(Color.parseColor("#A7D8FF"))
            drawerLayout.setBackgroundResource(R.drawable.bggradientday)
        }
        binding.loginButton.setOnClickListener {
            val emailLogin = binding.loginEmail.text.toString()
            val password = binding.loginPassword.text.toString()
            if (emailLogin.isNotEmpty() && password.isNotEmpty()) {
                firebaseAuth.signInWithEmailAndPassword(emailLogin, password).addOnCompleteListener {
                    if (it.isSuccessful) {
                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                    } else {
                        Toast.makeText(this, it.exception.toString(), Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                Toast.makeText(this, getString(R.string.fields), Toast.LENGTH_SHORT).show()
            }
        }
        binding.signupRedirectText.setOnClickListener {
            val signupIntent = Intent(this, RegisterActivity::class.java)
            startActivity(signupIntent)
        }
    }
}