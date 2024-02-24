package com.fluture.pruvve

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import com.fluture.pruvve.databinding.ActivityPlayerAccountCreatorBinding

class AthleteAccountCreator : AppCompatActivity() {
    private lateinit var binding: ActivityPlayerAccountCreatorBinding
    private var imageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityPlayerAccountCreatorBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.button1.setOnClickListener {
            finish()
        }

        binding.button.isEnabled = false

        binding.profpicplacholder.setOnClickListener {
            openImageChooser()
        }

        binding.btnaddimage.setOnClickListener {
            openImageChooser()
        }

        binding.button.setOnClickListener {
            val firstName = intent.getStringExtra("Extra_firstname").toString()
            val lastName = intent.getStringExtra("Extra_lastname").toString()
            val zipCode = intent.getStringExtra("Extra_zipcode").toString()
            val gender = intent.getStringExtra("Extra_gender").toString()
            val dateOfBirth = intent.getStringExtra("Extra_dateOfBirth").toString()
            val userName = intent.getStringExtra("Extra_username").toString()
            val passWord = intent.getStringExtra("Extra_password").toString()
            val imageAddress = imageUri.toString()
            Intent(this, CoachAccountFinalization::class.java).also {
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
    }

    private fun openImageChooser() {
        val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
            type = "image/*"
        }
        resultLauncher.launch(intent)
    }

    @SuppressLint("SetTextI18n")
    private val resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data
            data?.data?.let { uri ->
                imageUri = uri
                binding.profpicplacholder.setImageURI(uri)
                binding.btnaddimage.text = "Add Another Image"
                binding.button.isEnabled = true
                binding.button.setBackgroundResource(R.drawable.primary_button)
            }
        }
    }
}