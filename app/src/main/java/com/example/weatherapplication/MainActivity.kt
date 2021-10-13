package com.example.weatherapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
    // this is the OnClick function for the Test Demo Button
    fun testDemoClick(view: View) {
        val intent = Intent(this, DemoActivity::class.java)
        startActivity(intent)
    }
}