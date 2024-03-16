package com.fluture.pruvve

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.fluture.pruvve.databinding.ActivityHomePageBinding

class HomePage : AppCompatActivity() {
    private lateinit var binding: ActivityHomePageBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityHomePageBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        LoginManager.init(this)
        setContentView(binding.root)

        val homeScreen = HomeScreenFragments()

        supportFragmentManager.beginTransaction().apply {
            replace(R.id.homeScreenFragment, homeScreen)
            commit()
        }
        binding.homePageButton.setOnClickListener {
            supportFragmentManager.beginTransaction().apply {
                replace(R.id.homeScreenFragment, homeScreen)
                commit()
            }
        }
        binding.bookPitchButton.setOnClickListener {
            supportFragmentManager.beginTransaction().apply {
                replace(R.id.homeScreenFragment, BookPitchFragment())
                commit()
            }
        }
    }
}