package com.fluture.pruvve

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.fluture.pruvve.databinding.ActivityPlayerAccountCreatorBinding

class AthleteAccountCreator : AppCompatActivity() {
    private lateinit var binding: ActivityPlayerAccountCreatorBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityPlayerAccountCreatorBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        binding.button1.setOnClickListener {
            finish()}
        binding.button.isEnabled = false

        binding.profpicplacholder.setOnClickListener {
            Intent(Intent.ACTION_GET_CONTENT).also {
                it.type = "image/*"
                startActivityForResult(it, 0)}
        }
        binding.btnaddimage.setOnClickListener {
            Intent(Intent.ACTION_GET_CONTENT).also {
                it.type = "image/*"
                startActivityForResult(it, 0)}
        }
        binding.button.setOnClickListener {
            val firstName = intent.getStringExtra("Extra_firstname")
            val lastName = intent.getStringExtra("Extra_lastname")
            val zipCode = intent.getStringExtra("Extra_zipcode")
            val gender = intent.getStringExtra("Extra_gender")
            val dateOfBirth = intent.getStringExtra("Extra_dateOfBirth")
            val userName = intent.getStringExtra("Extra_username")
            val passWord = intent.getStringExtra("Extra_password")
            val imageAddress = binding.profpicplacholder.tag as? Uri
            Intent(this, AthleteVideoSet::class.java).also {
                it.putExtra("Extra_firstname", firstName)
                it.putExtra("Extra_lastname", lastName)
                it.putExtra("Extra_zipcode", zipCode)
                it.putExtra("Extra_gender", gender)
                it.putExtra("Extra_dateOfBirth", dateOfBirth)
                it.putExtra("Extra_username", userName)
                it.putExtra("Extra_password", passWord)
                it.putExtra("Extra_profilePic", imageAddress.toString())
                startActivity(it)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == Activity.RESULT_OK && requestCode == 0){
            val imgAdress = data?.data
            binding.btnaddimage.text = "Add Another Image"
            binding.profpicplacholder.setImageURI(imgAdress)
            binding.button.isEnabled = true
            binding.button.setBackgroundResource(R.drawable.primary_button)
        }
    }
}