package com.fluture.pruvve

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.fluture.pruvve.databinding.ActivitySignupBinding

class MainSignup:AppCompatActivity() {
    private lateinit var binding: ActivitySignupBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivitySignupBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val titleColorChanger = colorLetters(binding.pruvve.text.toString(), 3, 4)
        binding.pruvve.text = titleColorChanger

        val image = binding.image24
        Glide.with(this)
            .load(R.drawable.soccer)
            .into(image)

        binding.alreadyanaccount.setOnClickListener {
            Toast.makeText(this, "there are no accounts in the database yet", Toast.LENGTH_SHORT).show()
        }

        binding.signinbutton.setOnClickListener {
            Intent(this, ProfileCreation::class.java).also{
                startActivity(it)
            }
        }
    }
    fun colorLetters(text: String, startNum: Int, endNum: Int): SpannableStringBuilder {
        val colorChanger = SpannableStringBuilder(text)
        colorChanger.setSpan(
            ForegroundColorSpan(Color.GREEN),
            startNum,
            endNum,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        return colorChanger
    }
}
