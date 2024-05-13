package com.fluture.pruvve

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
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

class SplashScreen : AppCompatActivity() {
    private lateinit var binding: ActivitySplashScreenBinding
    private var accountType: String = ""
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

        service.getUserCredentials().enqueue(object : Callback<GetUserResponse> {
            override fun onResponse(call: Call<GetUserResponse>, response: Response<GetUserResponse>) {
                if (response.isSuccessful){
                    accountType = response.body()?.data?.accountType.toString()
                    Log.d("RetrofitAccount", response.body()?.data?.accountType.toString())
                    Log.d("RetrofitAccount", "your account date of birth is ${response.body()?.data?.dateOfBirth.toString()}")
                    Log.d("RetrofitAccount", "your account email is ${response.body()?.data?.email.toString()}")
                }else{
                    Log.e("RetrofitError", "Couldn't get user credentials ${response.errorBody().toString()}")
                }
            }
            override fun onFailure(call: Call<GetUserResponse>, t: Throwable) {
                Log.e("RetrofitFailure", "Error reaching server ${t.message.toString()}")
            }
        })
        Log.d("RetrofitAccount", "your account type is: $accountType")
        Glide.with(this)
            .load(R.drawable.soccer)
            .into(binding.imvSplashPic)
        android.os.Handler().postDelayed({
            if (accountType == "COACH"){
                startActivity(Intent(this, CoachHomePage::class.java))
                finish()
            }else{
                startActivity(Intent(this, HomePage::class.java))
                finish()
            }
        }, SPLASH_SCREEN_DURATION)
    }
    companion object {
        private const val SPLASH_SCREEN_DURATION = 3000L
    }
}