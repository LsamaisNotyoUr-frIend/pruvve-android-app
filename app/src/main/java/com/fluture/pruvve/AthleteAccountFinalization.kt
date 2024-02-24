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
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.NumberPicker
import android.widget.TextView
import com.fluture.pruvve.databinding.ActivityAthleteAccountFinalizationBinding
import java.io.BufferedReader
import java.io.InputStreamReader

class AthleteAccountFinalization : AppCompatActivity() {
    private lateinit var binding: ActivityAthleteAccountFinalizationBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityAthleteAccountFinalizationBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        val text1 = binding.tvtos.text.toString()
        val mySpan = SpannableString(text1)
        setClickableSpan(mySpan, "terms of service")
        setClickableSpan(mySpan, "additional terms")
        setClickableSpan(mySpan, "privacy policy")

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
            val firstName = intent.getStringExtra("Extra_firstname").toString()
            val lastName = intent.getStringExtra("Extra_lastname").toString()
            val zipCode = intent.getStringExtra("Extra_zipcode").toString()
            val gender = intent.getStringExtra("Extra_gender").toString()
            val dateOfBirth = intent.getStringExtra("Extra_dateOfBirth").toString()
            val userName = intent.getStringExtra("Extra_username").toString()
            val passWord = intent.getStringExtra("Extra_password").toString()
            val imageAddress = intent.getStringExtra("Extra_profilePic").toString()
            val videoAddress = intent.getStringExtra("Extra_introVideo").toString()
            val position = binding.tvpositionview.text.toString()
            val height = binding.etheightField.text.toString()
            val feet = binding.etfeetField.text.toString()
            val bio = binding.etbiofield.text.toString()
            Intent(this, AthleteOnboardEnd::class.java).also {
                it.putExtra("Extra_firstname", firstName)
                it.putExtra("Extra_lastname", lastName)
                it.putExtra("Extra_zipcode", zipCode)
                it.putExtra("Extra_gender", gender)
                it.putExtra("Extra_dateOfBirth", dateOfBirth)
                it.putExtra("Extra_username", userName)
                it.putExtra("Extra_password", passWord)
                it.putExtra("Extra_profilePic", imageAddress)
                it.putExtra("Extra_introVideo", videoAddress)
                it.putExtra("Extra_position", position)
                it.putExtra("Extra_height", height)
                it.putExtra("Extra_feet", feet)
                it.putExtra("Extra_bio",bio)
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