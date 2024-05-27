package com.fluture.pruvve

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bumptech.glide.Glide
import com.fluture.pruvve.databinding.ActivityMainBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import android.Manifest.permission.*
import android.content.pm.PackageManager
import android.view.LayoutInflater
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import android.os.Build
import android.util.Log

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val coroutineScope = CoroutineScope(Dispatchers.Main)
    private val requiredPermissions = mutableListOf(
        CAMERA,
        ACCESS_FINE_LOCATION,
        ACCESS_COARSE_LOCATION,
        READ_EXTERNAL_STORAGE,
        WRITE_EXTERNAL_STORAGE
    ).apply {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) { // Android 14+
            add(READ_MEDIA_IMAGES)
            add(READ_MEDIA_VIDEO)
        }
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val deniedPermissions = permissions.entries.filter { !it.value }.map { it.key }
        if (deniedPermissions.isNotEmpty()) {
            showPermissionExplanationDialog()
        } else {
            Toast.makeText(this@MainActivity, "Thank you for your patronage", Toast.LENGTH_SHORT).show()
            continueActivity()
        }
    }

    private var isActivityActive = false
    private var permissionDialog: AlertDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        isActivityActive = true
        checkPermissions()
        Glide.with(this)
            .load(R.drawable.soccer)
            .into(binding.image24)
    }

    override fun onDestroy() {
        super.onDestroy()
        isActivityActive = false
        coroutineScope.cancel()
        permissionDialog?.dismiss()
    }

    private fun continueActivity() {
        coroutineScope.launch {
            delay(DELAY_MILLIS)
            startActivity(Intent(this@MainActivity, MainSignup::class.java))
        }

        binding.root.setOnClickListener {
            coroutineScope.cancel()
            startActivity(Intent(this@MainActivity, MainSignup::class.java))
        }
    }

    companion object {
        private const val DELAY_MILLIS: Long = 3000
    }

    private fun checkPermissions(): Boolean {
        val permissionsGranted = requiredPermissions.all { permission ->
            ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED
        }
        if (!permissionsGranted) {
            requestPermissions()
        } else {
            continueActivity()
        }

        return permissionsGranted
    }

    private fun requestPermissions() {
        Log.d("MainActivity", "Requesting permissions: $requiredPermissions")
        requestPermissionLauncher.launch(requiredPermissions.toTypedArray())
    }

    private fun showPermissionExplanationDialog() {
        if (!isActivityActive) return

        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_permission_explanation, null)
        permissionDialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .setCancelable(false)
            .create()

        dialogView.findViewById<TextView>(R.id.tvPermissionCancel).setOnClickListener {
            permissionDialog?.dismiss()
            finishAffinity()
        }

        dialogView.findViewById<TextView>(R.id.tvPermissionProceed).setOnClickListener {
            permissionDialog?.dismiss()
            requestPermissions()
        }

        permissionDialog?.show()
    }
}
