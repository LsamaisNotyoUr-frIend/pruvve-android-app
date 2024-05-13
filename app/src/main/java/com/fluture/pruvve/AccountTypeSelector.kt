package com.fluture.pruvve

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.SpannableString
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.text.style.UnderlineSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import com.fluture.pruvve.auth.AuthInterceptor
import com.fluture.pruvve.auth.LoginManager
import com.fluture.pruvve.databinding.ActivityAccounttypeSelectorBinding
import com.fluture.pruvve.retrofittcalls.AccountType
import com.fluture.pruvve.retrofittcalls.AccountTypeResponse
import com.fluture.pruvve.retrofittcalls.UserService
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.io.BufferedReader
import java.io.InputStreamReader

class AccountTypeSelector : AppCompatActivity() {
    private lateinit var binding: ActivityAccounttypeSelectorBinding
    private var selectedTextView: TextView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityAccounttypeSelectorBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        LoginManager.init(this)
        setContentView(binding.root)
        binding.pruvve1.alpha = 0.5f
        binding.button1.setOnClickListener {
            finish()}
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

        var teamCoachPreviousBackground = binding.tvteamcoach.background
        var athletePreviousBackground = binding.tvathlete.background

        val text1 = binding.tvtos.text.toString()
        val mySpan = SpannableString(text1)
        setClickableSpan(mySpan, "terms of service")
        setClickableSpan(mySpan, "additional terms")
        setClickableSpan(mySpan, "privacy policy")
        binding.tvtos.apply {
            text = mySpan
            movementMethod = LinkMovementMethod.getInstance()
        }
        binding.button.isEnabled = false

        binding.tvathlete.setOnClickListener {
            if (selectedTextView != binding.tvathlete) {
                athletePreviousBackground = binding.tvathlete.background
                binding.tvathlete.setBackgroundResource(R.drawable.clicked_denominator)
                binding.tvteamcoach.background = teamCoachPreviousBackground
                selectedTextView = binding.tvathlete
                binding.button.isEnabled = true
                binding.button.setBackgroundResource(R.drawable.primary_button)
            }
        }

        binding.tvteamcoach.setOnClickListener {
            if (selectedTextView != binding.tvteamcoach) {
                teamCoachPreviousBackground = binding.tvteamcoach.background
                binding.tvteamcoach.setBackgroundResource(R.drawable.clicked_denominator)
                binding.tvathlete.background = athletePreviousBackground
                selectedTextView = binding.tvteamcoach
                binding.button.isEnabled = true
                binding.button.setBackgroundResource(R.drawable.primary_button)
            }
        }
        binding.button.setOnClickListener {
            binding.button.setBackgroundResource(R.drawable.disabled_button)
            binding.button.isEnabled = false
            val username = intent.getStringExtra("Extra_username").toString()

            val accountType = if (selectedTextView == binding.tvteamcoach) "COACH" else "ATHLETE"
            val newAccountType = AccountType(
                accountType
            )
            Log.d("RetrofitAccount", accountType)
            service.userAccountType(newAccountType).enqueue(object : Callback<AccountTypeResponse> {
                override fun onResponse(call: Call<AccountTypeResponse>, response: Response<AccountTypeResponse>) {
                    if (response.isSuccessful) {
                        Log.d("RetrofitAccount", newAccountType.toString())
                        Log.d("RetrofitAccount", response.body()?.message.toString())
                        val intent: Intent
                        if (selectedTextView == binding.tvteamcoach) {
                            intent = Intent(this@AccountTypeSelector, CoachScoutAccountcreator::class.java)
                        }
                        else{
                            intent = Intent(this@AccountTypeSelector, AthleteAccountCreator::class.java)
                        }
                        intent.putExtra("Extra_username", username)
                        startActivity(intent)
                    } else {
                        Log.e("RetrofitError", "Error changing user account type:${response.errorBody()?.string()!!}")
                        Toast.makeText(this@AccountTypeSelector, "Error changing account type", Toast.LENGTH_SHORT).show()
                        binding.button.setBackgroundResource(R.drawable.primary_button)
                        binding.button.isEnabled = true
                    }
                }
                override fun onFailure(call: Call<AccountTypeResponse>, t: Throwable) {
                    Log.e("RetrofitError", "Error reaching server: ${t.message.toString()}")
                    Toast.makeText(this@AccountTypeSelector, "Error reaching the server", Toast.LENGTH_SHORT).show()
                    binding.button.setBackgroundResource(R.drawable.primary_button)
                    binding.button.isEnabled = true
                }
            })
        }
    }
    private fun setClickableSpan(spannableString: SpannableString, targetWord: String) {
        val clickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                showTermsAndConditions(targetWord)
            }
        }
        val startIndex = spannableString.indexOf(targetWord)
        val endIndex = startIndex + targetWord.length
        spannableString.apply {
            setSpan(clickableSpan, startIndex, endIndex, SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE)
            setSpan(ForegroundColorSpan(Color.WHITE), startIndex, endIndex, SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE)
            setSpan(UnderlineSpan(), startIndex, endIndex, SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE)
        }
    }
    private fun showTermsAndConditions(term: String){
        val parentViewGroup: ViewGroup? = null
        val dialogView = LayoutInflater.from(this).inflate(R.layout.terms_and_conditions, parentViewGroup, false)
        val termsAndConditionsTextView: TextView = dialogView.findViewById(R.id.termsandconditions)

        val inputStream = when (term) {
            "terms of service" -> resources.openRawResource(R.raw.placeholdertermsfile)
            "privacy policy" -> resources.openRawResource(R.raw.placeholdertermsfile)
            "additional terms" -> resources.openRawResource(R.raw.placeholdertermsfile)
            else -> null
        }
        inputStream?.use { input ->
            val reader = BufferedReader(InputStreamReader(input))
            val terms = reader.readText()
            termsAndConditionsTextView.text = terms
        }

        val dialog = Dialog(this)
        dialog.setContentView(dialogView)
        dialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        dialog.show()

        dialogView.setOnClickListener {
            dialog.dismiss()
        }
    }
    private fun accountTypeSetter( accountType: String){
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
    }
}