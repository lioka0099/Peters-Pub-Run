package com.example.collisions_game.activities

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import com.example.collisions_game.databinding.ActivityMenuBinding
import com.example.collisions_game.utilities.GameMode
import com.example.collisions_game.utilities.SignalManager
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

class MenuActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMenuBinding
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private companion object {
        private const val REQUEST_LOCATION_PERMISSION = 100
    }

    private var delay: Long = 1000
    private var mode: GameMode = GameMode.SENSORS
    private var userName: String = ""

    // Store location here once obtained
    private var userLatitude: Double = 0.0
    private var userLongitude: Double = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMenuBinding.inflate(layoutInflater)
        setContentView(binding.root)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        checkLocationPermission()

        initViews()
    }

    private fun checkLocationPermission() {
        if (
            checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
            checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
        ) {
            // Permissions already granted
            getUserLocation()
        } else {
            // Request permissions
            requestPermissions(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                REQUEST_LOCATION_PERMISSION
            )
        }
    }

    //Handles the user's response to the permission request
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_LOCATION_PERMISSION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getUserLocation()
            } else {
                SignalManager.getInstance().toast("Location permission denied.")
            }
        }
    }

    private fun getUserLocation() {
        if (
            checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
            checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location ->
                    location?.let {
                        userLatitude = it.latitude
                        userLongitude = it.longitude
                    } ?: SignalManager.getInstance().toast("Location is null, please enable GPS.")
                }
                .addOnFailureListener { e ->
                    SignalManager.getInstance().toast("Failed to get location: ${e.message}")
                }
        }
    }

    private fun initViews() {
        binding.menuMODESWITCH.setOnCheckedChangeListener { _, isChecked ->
            handleModeSwitch(isChecked)
        }
        binding.menuSPEEDSWITCH.setOnCheckedChangeListener { _, isChecked ->
            handleSpeedSwitch(isChecked)
        }
        binding.menuSTARTBTN.setOnClickListener {
            if (userName.isEmpty()) {
                SignalManager.getInstance().toast("Please enter your name")
            } else {
                val bundle = Bundle().apply {
                    putLong("speed", delay)
                    putSerializable("gameMode", mode)
                    putString("userName", userName)
                    putDouble("latitude", userLatitude)
                    putDouble("longitude", userLongitude)
                }
                val intent = Intent(this, MainActivity::class.java)
                intent.putExtras(bundle)
                startActivity(intent)
            }
        }

        binding.menuSCOREBTN.setOnClickListener {
            val intent = Intent(this, ScoreTableActivity::class.java)
            startActivity(intent)
        }

        binding.menuBTNSubmit.setOnClickListener {
            userName = binding.menuEDITTEXTName.text.toString()
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(binding.menuEDITTEXTName.windowToken, 0)
        }
    }

    private fun handleModeSwitch(checked: Boolean) {
        if (checked) {
            binding.menuMODESWITCH.text = "Buttons"
            binding.menuSPEEDTEXT.visibility = View.VISIBLE
            binding.menuSPEEDSWITCH.visibility = View.VISIBLE
            mode = GameMode.BUTTONS
        } else {
            binding.menuMODESWITCH.text = "Sensors"
            binding.menuSPEEDTEXT.visibility = View.GONE
            binding.menuSPEEDSWITCH.visibility = View.GONE
            mode = GameMode.SENSORS
        }
    }

    private fun handleSpeedSwitch(checked: Boolean) {
        if (mode == GameMode.BUTTONS) {
            if (checked) {
                binding.menuSPEEDSWITCH.text = "Fast"
                delay = 500
            } else {
                binding.menuSPEEDSWITCH.text = "Normal"
                delay = 1000
            }
        }
    }
}
