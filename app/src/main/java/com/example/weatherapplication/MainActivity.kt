package com.example.weatherapplication

import android.content.Context
import android.content.Intent
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.Toast

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if(!isLocationEnabled()){
            Toast.makeText(this,
                "Your Location Provider is Turned Off. Please Turn ONN",
                Toast.LENGTH_LONG).show()
            // The user will be redirect towards the settings, to enable the location settings
            val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            startActivity(intent)
        }
        else {
            Toast.makeText(this,
                "Your Location Provider is Turned ONN",
                Toast.LENGTH_SHORT).show()
        }
    }

    fun isLocationEnabled() : Boolean {
        // This provides access to the system location services
        val locationManager : LocationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled( LocationManager.GPS_PROVIDER )
                || locationManager.isProviderEnabled( LocationManager.NETWORK_PROVIDER )
    }


    // this is the OnClick function for the Test Demo Button
    fun testDemoClick(view: View) {
        val intent = Intent(this, DemoActivity::class.java)
        startActivity(intent)
    }
}