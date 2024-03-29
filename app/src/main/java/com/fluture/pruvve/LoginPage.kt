package com.fluture.pruvve

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.fluture.pruvve.databinding.ActivityLoginPageBinding


class LoginPage : AppCompatActivity() {
    private lateinit var binding: ActivityLoginPageBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityLoginPageBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        LoginManager.init(this)
        setContentView(binding.root)

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
