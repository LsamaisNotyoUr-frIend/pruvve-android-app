package com.fluture.pruvve

import android.annotation.SuppressLint
import android.app.AlertDialog
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
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.NumberPicker
import android.widget.TextView
import com.fluture.pruvve.auth.AuthInterceptor
import com.fluture.pruvve.auth.LoginManager
import com.fluture.pruvve.databinding.ActivityAthleteAccountFinalizationBinding
import com.fluture.pruvve.data.api.UserService
import dagger.hilt.android.AndroidEntryPoint
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.io.BufferedReader
import java.io.InputStreamReader

@AndroidEntryPoint
class AthleteAccountFinalization : AppCompatActivity() {
    private lateinit var binding: ActivityAthleteAccountFinalizationBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityAthleteAccountFinalizationBinding.inflate(layoutInflater)
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

        binding.tvtos.apply {
            text = mySpan
            movementMethod = LinkMovementMethod.getInstance()
        }
        binding.tvpositionview.setOnClickListener {
            showDialogue()
        }
        binding.button1.setOnClickListener {
            finish() }
        binding.button.setOnClickListener {
            binding.button.setBackgroundResource(R.drawable.disabled_button)
            binding.button.isEnabled = false
            val userName = intent.getStringExtra("Extra_username").toString()
            val position = binding.tvpositionview.text.toString()
            val height = binding.etheightField.text.toString()
            val feet = binding.etfeetField.text.toString()
            val bio = binding.etbiofield.text.toString()
            val profileBody = ProfileBody(
                position,
                height,
                feet,
                bio
            )
            service.finishAthleteProfile(profileBody).enqueue(object : Callback<ProfileResponse>{
                override fun onResponse(call: Call<ProfileResponse>, response: Response<ProfileResponse>) {
                    if (response.isSuccessful){
                        Intent(this@AthleteAccountFinalization, AthleteOnboardEnd::class.java).also {
                            Log.d("Retrofit", response.message().toString())
                            it.putExtra("Extra_username", userName)
                            startActivity(it)
                        }
                    }else{
                        val errorMessage = response.errorBody()?.string() ?: "Unknown error"
                        Log.e("RetrofitError", "Error updating user details $errorMessage")
                        binding.button.setBackgroundResource(R.drawable.primary_button)
                        binding.button.isEnabled = true
                    }
                }

                override fun onFailure(call: Call<ProfileResponse>, t: Throwable) {
                    Log.e("Retrofit", "Error reaching server${t.message.toString()}")
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

    private fun showDialogue(){
        val dialogView = layoutInflater.inflate(R.layout.gender_picker, null)
        val builder = AlertDialog.Builder(this)
        builder.setView(dialogView)
        val dialog = builder.create()

        val positionPicker: NumberPicker = dialogView.findViewById(R.id.genderPicker)
        val positionLabels: TextView = dialogView.findViewById(R.id.genderLabel)
        val positions = arrayOf("Goalkeeper(GK)", "Center-back(CB)", "Left-back(LB)", "Right-back(RB)",
            "Defensive midfielder (DM)" ,"Central midfielder(CM)" ,"Attacking midfielder(AM)",
            "Striker(ST)" , "Left-winger(LW)" , "Right-winger(RW)")
        positionPicker.minValue = 0
        positionPicker.maxValue = 9
        positionPicker.displayedValues = positions
        positionPicker.setOnValueChangedListener { _, _, newVal ->
            positionLabels.text = positions[newVal]
            binding.imgender.visibility = View.VISIBLE
        }

        dialog.setOnDismissListener {
            binding.tvpositionview.text = positions[positionPicker.value]
            binding.imgender.visibility = View.INVISIBLE
        }
        val window = dialog.window
        window?.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT)
        window?.setGravity(Gravity.BOTTOM)
        dialog.show()
    }
}