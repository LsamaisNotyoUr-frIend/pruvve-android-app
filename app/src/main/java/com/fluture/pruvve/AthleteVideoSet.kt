package com.fluture.pruvve

import android.app.Activity
import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.fluture.pruvve.databinding.ActivityAthleteVideoSetBinding

class AthleteVideoSet : AppCompatActivity() {
    private lateinit var binding:ActivityAthleteVideoSetBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityAthleteVideoSetBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        binding.button1.setOnClickListener {
            finish()
        }
        binding.button.isEnabled = false
        binding.intvidplacholder.setOnClickListener {
            Intent(Intent.ACTION_GET_CONTENT).also {
                it.type = "video/*"
                startActivityForResult(it, 1)
            }
        }
        binding.btnaddvideo.setOnClickListener {
            Intent(Intent.ACTION_GET_CONTENT).also {
                it.type = "video/*"
                startActivityForResult(it, 1)}
        }

        binding.introvid.setOnPreparedListener {
            binding.intvidplacholder.visibility = View.GONE
            it.isLooping = true
            it.setVideoScalingMode(MediaPlayer.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING)
            binding.introvid.start()
        }
        binding.tvskip.setOnClickListener {
            Intent(this, AthleteAccountFinalization::class.java).also {
                startActivity(it)
            }
        }
        binding.button.setOnClickListener {
            val firstName = intent.getStringExtra("Extra_firstname")
            val lastName = intent.getStringExtra("Extra_lastname")
            val zipCode = intent.getStringExtra("Extra_zipcode")
            val gender = intent.getStringExtra("Extra_gender")
            val dateOfBirth = intent.getStringExtra("Extra_dateOfBirth")
            val userName = intent.getStringExtra("Extra_username")
            val passWord = intent.getStringExtra("Extra_password")
            val imageAddress = intent.getStringExtra("Extra_profilePic")
            val videoAddress = binding.introvid.tag as? Uri
            Intent(this, AthleteAccountFinalization::class.java).also {
                it.putExtra("Extra_firstname", firstName)
                it.putExtra("Extra_lastname", lastName)
                it.putExtra("Extra_zipcode", zipCode)
                it.putExtra("Extra_gender", gender)
                it.putExtra("Extra_dateOfBirth", dateOfBirth)
                it.putExtra("Extra_username", userName)
                it.putExtra("Extra_password", passWord)
                it.putExtra("Extra_profilePic", imageAddress)
                it.putExtra("Extra_introVideo", videoAddress.toString())
                startActivity(it)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == Activity.RESULT_OK && requestCode == 1){
            val vidAdress = data?.data
            binding.introvid.setMediaController(null)

            binding.btnaddvideo.text = "Add Another Video"
            binding.introvid.setVideoURI(vidAdress)
            binding.button.isEnabled = true
            binding.button.setBackgroundResource(R.drawable.primary_button)
        }
    }
}