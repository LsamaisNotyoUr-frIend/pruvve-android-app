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

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val coroutineScope = CoroutineScope(Dispatchers.Main)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        Glide.with(this)
            .load(R.drawable.soccer)
            .into(binding.image24)

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
}
