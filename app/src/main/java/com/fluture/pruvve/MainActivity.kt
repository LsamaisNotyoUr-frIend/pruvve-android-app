package com.fluture.pruvve

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import com.bumptech.glide.Glide
import com.fluture.pruvve.databinding.ActivityMainBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val coroutineScope = CoroutineScope(Dispatchers.Main)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val image = binding.image24
        Glide.with(this)
            .load(R.drawable.soccer)
            .into(image)

        coroutineScope.launch {
            delay(DELAY_MILLIS)
            startActivity(Intent(this@MainActivity, MainSignup::class.java))
        }

        binding.root.setOnClickListener {
            coroutineScope.cancel()
            startActivity(Intent(this@MainActivity, MainSignup::class.java))
        }
    }

    private fun colorLetters(text: String, startNum: Int = 3, endNum: Int = 4): SpannableStringBuilder {
        val colorChanger = SpannableStringBuilder(text)
        colorChanger.setSpan(
            ForegroundColorSpan(Color.GREEN),
            startNum,
            endNum,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        return colorChanger
    }

    override fun onDestroy() {
        super.onDestroy()
        coroutineScope.cancel()
    }

    companion object {
        private const val DELAY_MILLIS: Long = 3000
    }
}









