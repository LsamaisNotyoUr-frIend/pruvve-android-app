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
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.fluture.pruvve.databinding.ActivityDenominatorSelectorBinding
import java.io.BufferedReader
import java.io.InputStreamReader

class DenominatorSelector : AppCompatActivity() {
    private lateinit var binding: ActivityDenominatorSelectorBinding
    private var selectedTextView: TextView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityDenominatorSelectorBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        binding.pruvve1.alpha = 0.5f
        binding.button1.setOnClickListener {
            finish()}
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
            val firstName = intent.getStringExtra("Extra_firstname").toString()
            val lastName = intent.getStringExtra("Extra_lastname").toString()
            val zipCode = intent.getStringExtra("Extra_zipcode").toString()
            val gender = intent.getStringExtra("Extra_gender").toString()
            val dateOfBirth = intent.getStringExtra("Extra_dateOfBirth").toString()
            val userName = intent.getStringExtra("Extra_username").toString()
            val passWord = intent.getStringExtra("Extra_password").toString()
            if (selectedTextView == binding.tvteamcoach) {
                val intent = Intent(this@DenominatorSelector, CoachScoutAccountcreator::class.java)
                intent.putExtra("Extra_firstname", firstName)
                intent.putExtra("Extra_lastname", lastName)
                intent.putExtra("Extra_zipcode", zipCode)
                intent.putExtra("Extra_gender", gender)
                intent.putExtra("Extra_dateOfBirth", dateOfBirth)
                intent.putExtra("Extra_username", userName)
                intent.putExtra("Extra_password", passWord)
                startActivity(intent)
            } else if (selectedTextView == binding.tvathlete) {
                val intent = Intent(this@DenominatorSelector, AthleteAccountCreator::class.java)
                intent.putExtra("Extra_firstname", firstName)
                intent.putExtra("Extra_lastname", lastName)
                intent.putExtra("Extra_zipcode", zipCode)
                intent.putExtra("Extra_gender", gender)
                intent.putExtra("Extra_dateOfBirth", dateOfBirth)
                intent.putExtra("Extra_username", userName)
                intent.putExtra("Extra_password", passWord)
                startActivity(intent)
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
}