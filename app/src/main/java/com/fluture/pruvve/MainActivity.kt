package com.fluture.pruvve

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import com.bumptech.glide.Glide
import com.fluture.pruvve.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val handler = Handler()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val toChangeColor = colorLetters(binding.pruvve.text.toString(), 3, 4)
        binding.pruvve.text = toChangeColor

        val image = binding.image24
        Glide.with(this)
            .load(R.drawable.soccer)
            .into(image)

        handler.postDelayed({
            startActivity(Intent(this, MainSignup::class.java))
        }, DELAY_MILLIS)

        binding.root.setOnClickListener {
            handler.removeCallbacksAndMessages(null)
            startActivity(Intent(this, MainSignup::class.java))
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

    companion object {
        private const val DELAY_MILLIS: Long = 10000
    }
}









