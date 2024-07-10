package com.fluture.pruvve

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.fluture.pruvve.adapters.LoginResponse
import com.fluture.pruvve.auth.LoginManager
import com.fluture.pruvve.databinding.ActivityWelcomeBackBinding
import com.fluture.pruvve.retrofittcalls.LoginInfo
import com.fluture.pruvve.retrofittcalls.UserService
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
            binding.btngetbackin.setBackgroundResource(R.drawable.disabled_button)
            binding.btngetbackin.isEnabled = false
            val username = binding.etusername.text.toString()
            val password =  binding.etpasswordfield.text.toString()
            val userLogin = LoginInfo(
                username.trim(),
                password.trim()
            )
            service.getUser(userLogin).enqueue(object : Callback<LoginResponse> {
                override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                    if (response.isSuccessful) {
                        Toast.makeText(this@WelcomeBack, "User gotten successfully", Toast.LENGTH_SHORT).show()
                        val token = response.body()?.data?.token.toString()
                        LoginManager.saveToken(token)
                        val userData = response.body()?.data
                        userData?.let {
                            LoginManager.saveUserInfo(
                                it.user.id,
                                it.user.username,
                                it.user.accountType,
                                it.user.profilePicUrl
                            )
                        }
                        navigateToMainScreen(userData?.user?.accountType)
                        finish()
                    } else {
                        Log.e("RetrofitGetUserError", "Error calling user API, body:${response.errorBody()?.string()!!}")
                        binding.btngetbackin.setBackgroundResource(R.drawable.primary_button)
                        binding.btngetbackin.isEnabled = true
                    }
                }
                override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                    Log.e("RetrofitGetUserFailure", "Error reaching getUser API: ${t.message.toString()}")
                    Toast.makeText(this@WelcomeBack, "user not found", Toast.LENGTH_SHORT).show()
                    binding.btngetbackin.setBackgroundResource(R.drawable.primary_button)
                    binding.btngetbackin.isEnabled = true
                }
            })
        }
    }

    private fun navigateToMainScreen(accountType: String?) {
        val intent = Intent(this@WelcomeBack, SplashScreen::class.java)
        intent.putExtra("accountType", accountType)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        finish()
    }
}