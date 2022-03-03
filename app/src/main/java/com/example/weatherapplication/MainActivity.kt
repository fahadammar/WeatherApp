package com.example.weatherapplication

import android.Manifest
import android.annotation.SuppressLint
import android.app.Dialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.API_Models.WeatherResponse
import com.example.Network.WeatherServiceInterface
import com.example.utility.Constants
import com.google.android.gms.location.*
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import org.w3c.dom.Text
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {

    // A fused location client variable which is further used to get the user's current location
    private lateinit var mFusedLocationClient: FusedLocationProviderClient

    private var customProgressDialog : Dialog? = null

    lateinit var tv_main : TextView
    lateinit var tv_main_description : TextView
    lateinit var tv_temp : TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tv_main = findViewById(R.id.tv_main)
        tv_main_description = findViewById(R.id.tv_main_description)
        tv_temp = findViewById(R.id.tv_temp)

        // Initialize the Fused location variable
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        // Custom Progress Dialog

        if(!isLocationEnabled()){
            Toast.makeText(this,
                "Your Location Provider is Turned Off. Please Turn ONN",
                Toast.LENGTH_LONG).show()
            // The user will be redirect towards the settings, to enable the location settings
            val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            startActivity(intent)
        }
        else {
            Dexter.withActivity(this)
                .withPermissions(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
                .withListener(object : MultiplePermissionsListener {
                    override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                        if (report!!.areAllPermissionsGranted()) {
                            // TODO (STEP 7: Call the location request function here.)
                            // START
                            requestLocationData()
                            // END
                        }

                        if (report.isAnyPermissionPermanentlyDenied) {
                            Toast.makeText(
                                this@MainActivity,
                                "You have denied location permission. Please allow it is mandatory.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }

                    override fun onPermissionRationaleShouldBeShown(
                        permissions: MutableList<PermissionRequest>?,
                        token: PermissionToken?
                    ) {
                        showRationalDialogForPermissions()
                    }
                })
                .onSameThread()
                .check()

        }
    }

    fun isLocationEnabled() : Boolean {
        // This provides access to the system location services
        val locationManager : LocationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled( LocationManager.GPS_PROVIDER ) || locationManager.isProviderEnabled( LocationManager.NETWORK_PROVIDER )
    }

    private fun getLocationWeatherDetails(latittude : Double, longitutde : Double)
    {
        // if we have the internet connection then we get the retrofit object and do the rest of the stuff
        if(Constants.isNetworkAvailable(this))
        {
            val service : WeatherServiceInterface = Constants.returnRetroObject()!!.create<WeatherServiceInterface>(WeatherServiceInterface::class.java)

            val listCall : Call<WeatherResponse> = service.getWeather(latittude, longitutde, Constants.METRIC_UNIT, Constants.APP_KEY)

            showCustomProgressDialog()

            listCall.enqueue(object : Callback<WeatherResponse>{
                override fun onResponse(
                    call: Call<WeatherResponse>,
                    response: Response<WeatherResponse>
                ) {
                    if(response.isSuccessful){
                        hideProgressDialog()

                        val weatherList : WeatherResponse? = response.body()
                        Log.i("responseResult", "$weatherList")

                        setupUI(weatherList!!);
                    }
                    else
                    {
                        when(response.code()){
                           400 -> {
                               Log.e("Error 400", "Bad Connection")
                           }
                           404 -> {
                               Log.e("Error 404", "Content Not Found!!")
                           }
                           else -> {
                                Log.e("Error", "Generic Error")
                            }
                        }
                    }
                }

                override fun onFailure(call: Call<WeatherResponse>, t: Throwable) {
                    hideProgressDialog()
                    Log.e("Error", t!!.message.toString())
                }

            })
        }
        else {
            Toast.makeText(this, "No the internet is connected", Toast.LENGTH_LONG).show()
        }
    }

    /**
     * A function used to show the alert dialog when the permissions are denied
     * and need to allow it from settings app info.
     */
    private fun showRationalDialogForPermissions() {
        AlertDialog.Builder(this)
            .setMessage("It Looks like you have turned off permissions required for this feature. It can be enabled under Application Settings")
            .setPositiveButton(
                "GO TO SETTINGS"
            ) { _, _ ->
                try {
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    val uri = Uri.fromParts("package", packageName, null)
                    intent.data = uri
                    startActivity(intent)
                } catch (e: ActivityNotFoundException) {
                    e.printStackTrace()
                }
            }
            .setNegativeButton("Cancel") { dialog,
                                           _ ->
                dialog.dismiss()
            }.show()
    }

    /**
     * A function to request the current location. Using the fused location provider client.
     */
    @SuppressLint("MissingPermission")
    private fun requestLocationData() {
        val mLocationRequest = LocationRequest()
        mLocationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY

        mFusedLocationClient.requestLocationUpdates(
            mLocationRequest, mLocationCallback,
            Looper.myLooper()
        )
    }


    // Show Custom Progress Dialog
    private fun showCustomProgressDialog(){
        customProgressDialog = Dialog(this)
        /**
         * Set the screen content from a layout resource. The resource will be inflated, add all top level
         * views to the screen
         * */
        customProgressDialog!!.setContentView(R.layout.custom_dialog_progress)

        customProgressDialog!!.show()
    }
    // Hide Custom Progress Dialog
    private fun hideProgressDialog() {
        if(customProgressDialog != null){
            customProgressDialog!!.dismiss()
        }
    }

    /**
     * A location callback object of fused location provider client where we will get the current location details.
     */
    private val mLocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            val mLastLocation: Location = locationResult.lastLocation
            val latitude = mLastLocation.latitude
            Log.i("Current Latitude", "$latitude")

            val longitude = mLastLocation.longitude
            Log.i("Current Longitude", "$longitude")
            getLocationWeatherDetails(latitude, longitude)
        }
    }

    private fun setupUI(weatherList : WeatherResponse){
        for(i in weatherList.weather.indices){
            Log.i("weatherName", weatherList.weather.toString())

            tv_main.text = weatherList.weather[i].main
            tv_main_description.text = weatherList.weather[i].description
            tv_temp.text = weatherList.main.temp.toString() + getUnit(application.resources.configuration.locales.toString())
        }
    }

    private fun getUnit(value : String) : String? {
        var value = "℃"
        if("US" == value || "LR" == value || "MM" == value){
            value = "℉"
        }
        return value;
    }
}