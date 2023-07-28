package com.uas_project.uas_alfares_2107411020.activity

import android.content.Context
import android.content.Intent
import android.location.Location
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.uas_project.uas_alfares_2107411020.R
import com.uas_project.uas_alfares_2107411020.adapter.LocationAdapter
import com.uas_project.uas_alfares_2107411020.api.RetrofitClient
import com.uas_project.uas_alfares_2107411020.model.DetailLoc
import com.uas_project.uas_alfares_2107411020.model.Locations
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import kotlin.collections.ArrayList

class HomePageActivity : AppCompatActivity() {
    private lateinit var username: TextView
    private lateinit var currLoc: TextView
    private lateinit var btnLogout: Button
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var listView : ListView
    private var data = ArrayList<DetailLoc>()
    private val TAG: String = "CHECK_RESPONSE"
    private lateinit var geocoder: Geocoder

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_page)
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        val sharedPref = getSharedPreferences("USER_ACCOUNT", MODE_PRIVATE)
        val user_acc = sharedPref.getString("USERNAME", null)
        geocoder = Geocoder(this, Locale.getDefault())
        username = findViewById(R.id.usernameHome)
        username.text = user_acc

        btnLogout = findViewById(R.id.btnLogout)
        listView = findViewById(R.id.LvLoc)
        currLoc = findViewById(R.id.currLoc)

        btnLogout.setOnClickListener {
            val preferences = getSharedPreferences("USER_ACCOUNT", MODE_PRIVATE)
            preferences.edit().clear().commit()
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }

        RetrofitClient.instance.getLocations().enqueue(object: Callback<Locations>{
            override fun onResponse(call: Call<Locations>, response: Response<Locations>) {
                val listResponse = response.body()?.data
                if (listResponse != null) {
                    data = listResponse
                    getCurrentLocation()
                }
            }

            override fun onFailure(call: Call<Locations>, t: Throwable) {
                Log.i(TAG, "Error = ${t.message}")
            }

        })




    }

    // Tampilkan list view data kos
    private fun showListView(lat: Double, lot:Double){
        val adapter  = LocationAdapter(this, data)
        if (adapter != null) {
            adapter.setKosInfo(lat, lot)
        }
        listView.adapter = adapter
        listView.visibility = View.VISIBLE
        listView.onItemClickListener =
            AdapterView.OnItemClickListener { parent, view, position, id ->
                val locSelected = data.get(position)
                val intentUri = Uri.parse("https://www.google.com/maps/dir/?api=1&destination=${locSelected?.latitude},${locSelected?.longitude}")
                val intent = Intent(Intent.ACTION_VIEW, intentUri)
                intent.setPackage("com.google.android.apps.maps")
                startActivity(intent)
            }

    }

    private fun getCurrentLocation(){
        if(checkPermission())
        {
            if (isLocationEnabled())
            {

                // Final latitude and longitude
                fusedLocationProviderClient.lastLocation.addOnSuccessListener{ location: Location? ->
                    location?.let {
                        Toast.makeText(this, "Get Success", Toast.LENGTH_SHORT).show()
                        currLoc.text = location.latitude.toString() + " " + location.longitude.toString()
                        var address = geocoder.getFromLocation(location.latitude, location.longitude, 1)
                        if (address != null) {
                            if (address.isEmpty()){
                                currLoc.text = "Location Not Found"
                            }else{
                                currLoc.text = "${address?.get(0)?.getAddressLine(0)}"
                            }
                        }
                        showListView(location.latitude, location.longitude)
                    } ?: run {
                        Toast.makeText(
                            this,
                            "Tidak dapat mengambil lokasi",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
            else{
                // Setting open here
                Toast.makeText(this, "Turn on location", Toast.LENGTH_SHORT).show()
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(intent)
            }
        }
        else
        {
            // request permission
            requestPermission()
        }
    }

    private fun isLocationEnabled(): Boolean
    {
        val locationManager: LocationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)||locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }

    private fun requestPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(android.Manifest.permission.ACCESS_COARSE_LOCATION,
                android.Manifest.permission.ACCESS_FINE_LOCATION),
            PERMISSION_REQUEST_ACCESS_LOCATION
        )
    }


    companion object{
        private const val PERMISSION_REQUEST_ACCESS_LOCATION = 1001
    }

    private fun checkPermission(): Boolean
    {
        if(ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_COARSE_LOCATION)
            == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED)
        {
            return true
        }
        return false
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == PERMISSION_REQUEST_ACCESS_LOCATION)
        {
            if(grantResults.isNotEmpty() && grantResults[0]== PackageManager.PERMISSION_GRANTED)
            {
                Toast.makeText(applicationContext, "Granted", Toast.LENGTH_SHORT).show()
                getCurrentLocation()
            }
            else
            {
                Toast.makeText(applicationContext, "Denied", Toast.LENGTH_SHORT).show()
            }
        }
    }
}