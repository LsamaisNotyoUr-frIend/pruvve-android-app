package com.fluture.pruvve

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.fluture.pruvve.adapters.GetUserResponse
import com.fluture.pruvve.auth.AuthInterceptor
import com.fluture.pruvve.auth.LoginManager
import com.fluture.pruvve.databinding.ActivitySplashScreenBinding
import com.fluture.pruvve.retrofittcalls.UserService
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

@SuppressLint("CustomSplashScreen")
class SplashScreen : AppCompatActivity() {
    private lateinit var binding: ActivitySplashScreenBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        LoginManager.init(this)
        setContentView(binding.root)
        val token = LoginManager.getToken()
        Log.d("RetrofitToken", token.toString())

        val httpClient = OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor(token.toString()))
            .build()
        val service = Retrofit.Builder()
            .baseUrl("https://pruvve-backend-9a89de78d2a1.herokuapp.com/api/")
            .client(httpClient)
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
            .create(UserService::class.java)

        Glide.with(this)
            .load(R.drawable.soccer)
            .into(binding.imvSplashPic)

        getUserCredentials(service)
    }

    override fun onResume() {
        super.onResume()
        LoginManager.init(this)
        val token = LoginManager.getToken()
        Log.d("RetrofitToken", token.toString())

        val httpClient = OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor(token.toString()))
            .build()
        val service = Retrofit.Builder()
            .baseUrl("https://pruvve-backend-9a89de78d2a1.herokuapp.com/api/")
            .client(httpClient)
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
            .create(UserService::class.java)

        getUserCredentials(service)
    }

    private fun getUserCredentials(service: UserService){
        service.getUserCredentials().enqueue(object : Callback<GetUserResponse> {
            override fun onResponse(call: Call<GetUserResponse>, response: Response<GetUserResponse>) {
                if (response.isSuccessful) {
                    val accountType = response.body()?.data?.accountType.toString()
                    Log.d("RetrofitAccount", "Your account type is: $accountType")
                    if (accountType == "COACH") {
                        Intent(this@SplashScreen, CoachHomePage::class.java).also {
                            startActivity(it)
                        }
                    } else {
                        Intent(this@SplashScreen, HomePage::class.java).also {
                            startActivity(it)
                        }
                    }
                } else {
                    Log.e("RetrofitError", "Couldn't get user credentials ${response.errorBody().toString()}")
                }
            }

            override fun onFailure(call: Call<GetUserResponse>, t: Throwable) {
                Log.e("RetrofitFailure", "Error reaching server ${t.message.toString()}")
            }
        })
    }
}
