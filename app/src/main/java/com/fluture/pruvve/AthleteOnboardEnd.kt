package com.fluture.pruvve

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.SpannableString
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.text.style.UnderlineSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.fluture.pruvve.auth.LoginManager
import com.fluture.pruvve.databinding.ActivityAthleteOnboardEndBinding
import java.io.BufferedReader
import java.io.InputStreamReader

class AthleteOnboardEnd : AppCompatActivity() {
    private lateinit var binding: ActivityAthleteOnboardEndBinding
    private var accountType: String = ""
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityAthleteOnboardEndBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        LoginManager.init(this)

        setContentView(binding.root)
        val text1 = binding.tvtos.text.toString()
        val mySpan = SpannableString(text1)
        setClickableSpan(mySpan, "terms of service")
        setClickableSpan(mySpan, "additional terms")
        setClickableSpan(mySpan, "privacy policy")
        val username = LoginManager.getUsername()
        binding.tvwelcome.text = "Welcome to Pruvve, \n$username"

        binding.tvtos.apply {
            text = mySpan
            movementMethod = LinkMovementMethod.getInstance()
        }
        binding.button1.setOnClickListener {
            finish() }

        binding.button.setOnClickListener {
            val intent = Intent(this@AthleteOnboardEnd, SplashScreen::class.java)
            intent.putExtra("accountType", accountType)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
            finish()
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
}
