package com.fluture.pruvve

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.fluture.pruvve.databinding.ActivityLoginPageBinding

class LoginPage : AppCompatActivity() {
    private lateinit var biniding: ActivityLoginPageBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        biniding = ActivityLoginPageBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(biniding.root)
        biniding.signinbutton.setOnClickListener {
            Intent(this, WelcomeBack::class.java).also{
                startActivity(it)
            }
        }
    }
}