package com.fluture.pruvve

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.facebook.FacebookSdk
import com.facebook.appevents.AppEventsLogger
import com.fluture.pruvve.databinding.ActivityLoginPageBinding
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

@Suppress("NAME_SHADOWING")
class LoginPage : AppCompatActivity() {
    private lateinit var binding: ActivityLoginPageBinding
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityLoginPageBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        LoginManager.init(this)
        setContentView(binding.root)

        auth = Firebase.auth

        FacebookSdk.sdkInitialize(applicationContext)
        AppEventsLogger.activateApp(application)

//        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
//            .requestIdToken(getString(R.string.webClientId))
//            .requestEmail()
//            .build()
//
//        googleSignInClient = GoogleSignIn.getClient(this@LoginPage, gso)

        val signInRequest = BeginSignInRequest.builder()
            .setGoogleIdTokenRequestOptions(
                BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                    .setSupported(true)
                    // Your server's client ID, not your Android client ID.
                    .setServerClientId(getString(R.string.webClientId))
                    // Only show accounts previously used to sign in.
                    .setFilterByAuthorizedAccounts(true)
                    .build())
            .build()
        binding.signinbutton.setOnClickListener {
            Intent(this@LoginPage, WelcomeBack::class.java).also{
                startActivity(it)
            }
        }

        binding.googleSignInButton.setOnClickListener {
            TODO()
        }
    }

    private fun login(token:String){
        val service = Retrofit.Builder()
            .baseUrl("https://pruvve-backend-9a89de78d2a1.herokuapp.com/api/")
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
            .create(UserService::class.java)
        service.googleLogin(token).enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                if (response.isSuccessful) {
                    val token = response.body()?.data?.token.toString()
                    Log.d("RetrofitToken", token)
                    LoginManager.saveToken(token)
                } else {
                    Log.e("RetrofitError", "User authorization failed: ${response.errorBody()?.toString()}")
                    Toast.makeText(this@LoginPage, "Couldn't authorize user", Toast.LENGTH_SHORT).show()
                }
            }
            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                Log.e("RetrofitError", "Authorization failed: ${t.message.toString()}")
                Toast.makeText(this@LoginPage, "Error during authorization", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
