package com.example.weatherapp

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.example.weatherapp.databinding.ActivityRegisterBinding
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import java.util.*

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    private lateinit var firebaseAuth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
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
            navUsername.text = "Guest"
        }
        navigationView.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.optHome -> {
                    val signupIntent = Intent(this, MainActivity::class.java)
                    startActivity(signupIntent)
                    drawerLayout.closeDrawer(GravityCompat.START)
                }
                R.id.optRegister -> {
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
        menu.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }
        val rightNow = Calendar.getInstance()
        val hour: Int = rightNow.get(Calendar.HOUR_OF_DAY)
        if (hour in 6..20) {
            header.setBackgroundColor(Color.parseColor("#A7D8FF"))
            drawerLayout.setBackgroundResource(R.drawable.bggradientday)
        }
        binding.signupButton.setOnClickListener {
            val email = binding.signupEmail.text.toString()
            val password = binding.signupPassword.text.toString()
            val confirmPassword = binding.signupConfirm.text.toString()
            if (email.isNotEmpty() && password.isNotEmpty() && confirmPassword.isNotEmpty()) {
                if (password == confirmPassword) {
                    firebaseAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener {
                            if (it.isSuccessful) {
                                val intent = Intent(this, LoginActivity::class.java)
                                startActivity(intent)
                            } else {
                                Toast.makeText(this, it.exception.toString(), Toast.LENGTH_SHORT)
                                    .show()
                            }
                        }
                } else {
                    Toast.makeText(this, "Password does not matched", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Fields cannot be empty", Toast.LENGTH_SHORT).show()
            }
        }
        binding.loginRedirectText.setOnClickListener {
            val loginIntent = Intent(this, LoginActivity::class.java)
            startActivity(loginIntent)
        }
    }
}