package com.fluture.pruvve.retrofittcalls

import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.fluture.pruvve.databinding.ActivityBlankPictureBinding

class BlankPicture : AppCompatActivity() {
    private lateinit var binding: ActivityBlankPictureBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityBlankPictureBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        val imageBitmap = intent.getParcelableExtra<Bitmap>("imageBitmap")
        val imageUriString = intent.getStringExtra("Extra_PostUri")
        if (imageBitmap != null) {
            binding.imvShowPic.setImageBitmap(imageBitmap)
        } else if (!imageUriString.isNullOrEmpty()) {
            val imageUri = Uri.parse(imageUriString)
           binding.imvShowPic.setImageURI(imageUri)
        }
        binding.btnAddPic.setOnClickListener{
            finish()
        }
    }
}