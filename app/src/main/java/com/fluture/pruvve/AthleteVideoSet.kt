package com.fluture.pruvve

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import com.fluture.pruvve.databinding.ActivityAthleteVideoSetBinding

class AthleteVideoSet : AppCompatActivity() {
    private lateinit var binding:ActivityAthleteVideoSetBinding
    private var videoUri: Uri? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityAthleteVideoSetBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        binding.button1.setOnClickListener {
            finish()
        }
        binding.button.isEnabled = false

        binding.intvidplacholder.setOnClickListener {
            openVideoChooser()
        }
        binding.btnaddvideo.setOnClickListener {
            openVideoChooser()
        }

        binding.introvid.setOnPreparedListener {
            binding.intvidplacholder.visibility = View.GONE
            it.isLooping = true
            it.setVideoScalingMode(MediaPlayer.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING)
            binding.introvid.start()
        }
        binding.tvskip.setOnClickListener {
            val firstName = intent.getStringExtra("Extra_firstname").toString()
            val lastName = intent.getStringExtra("Extra_lastname").toString()
            val zipCode = intent.getStringExtra("Extra_zipcode").toString()
            val gender = intent.getStringExtra("Extra_gender").toString()
            val dateOfBirth = intent.getStringExtra("Extra_dateOfBirth").toString()
            val userName = intent.getStringExtra("Extra_username").toString()
            val passWord = intent.getStringExtra("Extra_password").toString()
            val imageAddress = intent.getStringExtra("Extra_profilePic").toString()
            Intent(this, AthleteAccountFinalization::class.java).also {
                it.putExtra("Extra_firstname", firstName)
                it.putExtra("Extra_lastname", lastName)
                it.putExtra("Extra_zipcode", zipCode)
                it.putExtra("Extra_gender", gender)
                it.putExtra("Extra_dateOfBirth", dateOfBirth)
                it.putExtra("Extra_username", userName)
                it.putExtra("Extra_password", passWord)
                it.putExtra("Extra_profilePic", imageAddress)
                startActivity(it)
            }
        }
        binding.button.setOnClickListener {
            val firstName = intent.getStringExtra("Extra_firstname").toString()
            val lastName = intent.getStringExtra("Extra_lastname").toString()
            val zipCode = intent.getStringExtra("Extra_zipcode").toString()
            val gender = intent.getStringExtra("Extra_gender").toString()
            val dateOfBirth = intent.getStringExtra("Extra_dateOfBirth").toString()
            val userName = intent.getStringExtra("Extra_username").toString()
            val passWord = intent.getStringExtra("Extra_password").toString()
            val imageAddress = intent.getStringExtra("Extra_profilePic").toString()
            val videoAddress = videoUri.toString()
            Intent(this, AthleteAccountFinalization::class.java).also {
                it.putExtra("Extra_firstname", firstName)
                it.putExtra("Extra_lastname", lastName)
                it.putExtra("Extra_zipcode", zipCode)
                it.putExtra("Extra_gender", gender)
                it.putExtra("Extra_dateOfBirth", dateOfBirth)
                it.putExtra("Extra_username", userName)
                it.putExtra("Extra_password", passWord)
                it.putExtra("Extra_profilePic", imageAddress)
                it.putExtra("Extra_introVideo", videoAddress)
                startActivity(it)
            }
        }
    }
    private fun openVideoChooser() {
        Intent(Intent.ACTION_GET_CONTENT).also {
            it.type = "video/*"
        }
        resultLauncher.launch(intent)
    }
    @SuppressLint("SetTextI18n")
    private val resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data
            data?.data?.let { uri ->
                binding.introvid.setMediaController(null)
                videoUri = uri
                binding.introvid.setVideoURI(uri)
                binding.btnaddvideo.text = "Add Another Video"
                binding.button.isEnabled = true
                binding.button.setBackgroundResource(R.drawable.primary_button)
            }
        }
    }
}