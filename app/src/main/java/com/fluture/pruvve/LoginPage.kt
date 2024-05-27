package com.fluture.pruvve

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bumptech.glide.Glide
import com.fluture.pruvve.auth.LoginManager
import com.fluture.pruvve.databinding.ActivityLoginPageBinding


class LoginPage : AppCompatActivity() {
    private lateinit var binding: ActivityLoginPageBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityLoginPageBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        LoginManager.init(this)
        setContentView(binding.root)

        val image = binding.image24
        Glide.with(this)
            .load(R.drawable.soccer)
            .into(image)

        binding.signinbutton.setOnClickListener {
            Intent(this@LoginPage, WelcomeBack::class.java).also{
                startActivity(it)
            }
        }

        binding.googleSignInButton.setOnClickListener {
            TODO()
        }
    }
}
