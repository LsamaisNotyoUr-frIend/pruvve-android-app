package com.fluture.pruvve

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.fluture.pruvve.databinding.ActivityWelcomeBackBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

class WelcomeBack : AppCompatActivity() {
    private lateinit var binding: ActivityWelcomeBackBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityWelcomeBackBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        val service = Retrofit.Builder()
            .baseUrl("https://pruvve-backend-9a89de78d2a1.herokuapp.com/api/")
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
            .create(UserService::class.java)

        binding.button1.setOnClickListener {
            finish()
        }
        binding.tvnoaccount.setOnClickListener {
            Intent(this, ProfileCreation::class.java).also {
                startActivity(it)
            }
        }
        binding.btngetbackin.setOnClickListener {
            val username = binding.etusername.text.toString()
            val password =  binding.etpasswordfield.text.toString()
            val userLogin = LoginInfo(
                username,
                password
            )
            service.getUser(userLogin).enqueue(object : Callback<User> {
                override fun onResponse(call: Call<User>, response: Response<User>) {
                    if (response.isSuccessful) {
                        Toast.makeText(this@WelcomeBack, "User gotten successfully", Toast.LENGTH_SHORT).show()
                    } else {
                        Log.e("RetrofitError", "RetrofitError:${response.errorBody()?.string()!!}")
                        Toast.makeText(this@WelcomeBack, "couldn't fetch user", Toast.LENGTH_SHORT).show()
                    }
                }
                override fun onFailure(call: Call<User>, t: Throwable) {
                    Log.e("RetrofitFailure", "error: ${t.message.toString()}")
                    Toast.makeText(this@WelcomeBack, "user not found", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }
}