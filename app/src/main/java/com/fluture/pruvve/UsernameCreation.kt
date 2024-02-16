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
import com.fluture.pruvve.databinding.ActivityUsernameCreationBinding
import java.io.BufferedReader
import java.io.InputStreamReader

class UsernameCreation : AppCompatActivity() {
    private lateinit var binding: ActivityUsernameCreationBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityUsernameCreationBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.button1.setOnClickListener {
            finish()
        }
        val text1 = binding.tvtos.text.toString()
        val my_span = SpannableString(text1)
        setClickableSpan(my_span, "terms of service")
        setClickableSpan(my_span, "additional terms")
        setClickableSpan(my_span, "privacy policy")

        binding.tvtos.apply {
            text = my_span
            movementMethod = LinkMovementMethod.getInstance()
        }
        binding.button.setOnClickListener {
            val firstName = intent.getStringExtra("Extra_firstname")
            val lastName = intent.getStringExtra("Extra_lastname")
            val zipCode = intent.getStringExtra("Extra_zipcode")
            val gender = intent.getStringExtra("Extra_gender")
            val dateOfBirth = intent.getStringExtra("Extra_dateOfBirth")
            val userName = binding.usernameField.text.toString()
            val passWord = binding.passwordField.text.toString()
            Intent(this, DenominatorSelector::class.java).also {
                it.putExtra("Extra_firstname", firstName)
                it.putExtra("Extra_lastname", lastName)
                it.putExtra("Extra_zipcode", zipCode)
                it.putExtra("Extra_gender", gender)
                it.putExtra("Extra_dateOfBirth", dateOfBirth)
                it.putExtra("Extra_username", userName)
                it.putExtra("Extra_password", passWord)
                startActivity(it)
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