package com.example.weatherapp

import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class CreatorsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_creators)
        val text1 = findViewById<TextView>(R.id.linkEduardo)
        val text2 = findViewById<TextView>(R.id.linkAndre)
        text1.movementMethod = LinkMovementMethod.getInstance()
        text2.movementMethod = LinkMovementMethod.getInstance()
    }
}