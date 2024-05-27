package com.fluture.pruvve

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.SpannableString
import android.text.TextWatcher
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.text.style.UnderlineSpan
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.fluture.pruvve.auth.AuthInterceptor
import com.fluture.pruvve.auth.LoginManager
import com.fluture.pruvve.databinding.ActivityCoachAccountFinalizationBinding
import com.fluture.pruvve.retrofittcalls.GetTeams
import com.fluture.pruvve.retrofittcalls.RequestObjects
import com.fluture.pruvve.retrofittcalls.UserService
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.io.BufferedReader
import java.io.InputStreamReader

class CoachAccountFinalization : AppCompatActivity() {
    private lateinit var binding: ActivityCoachAccountFinalizationBinding
    private var isUsable=  true
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityCoachAccountFinalizationBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        LoginManager.init(this)
        setContentView(binding.root)
        val text1 = binding.tvtos.text.toString()
        val mySpan = SpannableString(text1)
        setClickableSpan(mySpan, "terms of service")
        setClickableSpan(mySpan, "additional terms")
        setClickableSpan(mySpan, "privacy policy")

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

        val handler = Handler(Looper.getMainLooper())
        var searchRunnable: Runnable? = null

        binding.etteamview.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                //no change is expected
            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                searchRunnable?.let { handler.removeCallbacks(it) }

                searchRunnable = Runnable {
                    val teamName = s.toString()
                    if (teamName.isNotEmpty()) {
                        getTeams(service, teamName)
                    }
                }
                handler.postDelayed(searchRunnable!!, 300)
            }

            override fun afterTextChanged(s: Editable?) {
                //no change is expected
            }
        })

        binding.tvtos.apply {
            text = mySpan
            movementMethod = LinkMovementMethod.getInstance()
        }

        binding.button1.setOnClickListener {
            finish() }
        binding.button.setOnClickListener {
            binding.button1.isEnabled = false
            binding.button1.setBackgroundResource(R.drawable.disabled_button)
            val team = binding.etteamview.text.toString()
            if (isUsable){
                 makeTeams(service, team)
                finishCoachAccount(team, service)
            }else{
                binding.button1.isEnabled = true
                binding.button1.setBackgroundResource(R.drawable.primary_button)
                showDialog()
            }
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
    @SuppressLint("InflateParams")
    private fun showTermsAndConditions(term: String){
        val dialogView = LayoutInflater.from(this).inflate(R.layout.terms_and_conditions, null)
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

    private fun getTeams(service: UserService, team:String){
        val requestObjects = RequestObjects(
            10,
            10
        )
        Log.d("RetrofitTeams","Process started")
        service.getTeams(requestObjects).enqueue(object : Callback<GetTeams>{
            @SuppressLint("SetTextI18n")
            override fun onResponse(call: Call<GetTeams>, response: Response<GetTeams>) {
                if (response.isSuccessful){
                    val list =  response.body()?.data?.list
                    if (list == null){
                        return
                    }else{
                        Log.d("RetrofitTeams","Checking teams in list")
                        for (teamItems in list){
                            if (teamItems.name == team){
                                Log.d("RetrofitTeams","Team name not available")
                                binding.tvTeamNameChecker.text = "Team name already taken"
                                binding.button1.isEnabled = true
                                binding.button1.setBackgroundResource(R.drawable.primary_button)
                                isUsable = false
                            }else{
                                binding.tvTeamNameChecker.text = "Team name available"
                                isUsable = true
                            }
                        }
                    }
                }
            }
            override fun onFailure(call: Call<GetTeams>, t: Throwable) {
                Log.e("RetrofitFailure","Couldn't reach the server ${t.message.toString()}")
                binding.button1.isEnabled = true
                binding.button1.setBackgroundResource(R.drawable.primary_button)
            }

        })
    }
    private fun makeTeams(service: UserService, team: String){
        Log.d("RetrofitTeams","Creating new teams")
        service.makeTeams(binding.etteamview.text.toString()).enqueue(object : Callback<ProfileResponse>{
            override fun onResponse(call: Call<ProfileResponse>, response: Response<ProfileResponse>) {
                if (response.isSuccessful){
                    Toast.makeText(this@CoachAccountFinalization, "Team created successfully", Toast.LENGTH_SHORT).show()
                    Log.d("RetrofitSuccess", "your request was successful ${response.body()?.message}")
                    finishCoachAccount(team, service)
                }else{
                    Log.e("RetrofitError","there was an error ${response.errorBody().toString()}")
                }
            }
            override fun onFailure(call: Call<ProfileResponse>, t: Throwable) {
                Log.e("RetrofitFailure", "couldn't reach the server ${t.message.toString()}")
            }
        })
    }
    private fun finishCoachAccount(team: String, service: UserService){
        val userName = intent.getStringExtra("Extra_username").toString()
        val bio = binding.etbiofield.text.toString()
        val coachProfileBody = CoachProfileBody(
            team,
            bio
        )
        service.finishCoachProfile(coachProfileBody).enqueue(object : Callback<ProfileResponse>{
            override fun onResponse(call: Call<ProfileResponse>, response: Response<ProfileResponse>) {
                if (response.isSuccessful){
                    Log.d("RetrofitSuccess", "Coach details updated successfully")
                    Intent(this@CoachAccountFinalization, CoachOnboardEnd::class.java).also {
                        it.putExtra("Extra_username", userName)
                        startActivity(it)
                    }
                }else{
                    val errorMessage = response.errorBody()?.string() ?: "Unknown error"
                    Log.e("RetrofitError", "Error uploading file $errorMessage")
                    binding.button1.isEnabled = true
                    binding.button1.setBackgroundResource(R.drawable.primary_button)
                }
            }

            override fun onFailure(call: Call<ProfileResponse>, t: Throwable) {
                Log.e("RetrofitError", "Error reaching server ${t.message.toString()}")
                binding.button1.isEnabled = true
                binding.button1.setBackgroundResource(R.drawable.primary_button)
            }
        })
    }
    private fun showDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_team_explanation, null)
        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .setCancelable(false)
            .create()
        dialogView.findViewById<TextView>(R.id.tvTeamProceed).setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }
}