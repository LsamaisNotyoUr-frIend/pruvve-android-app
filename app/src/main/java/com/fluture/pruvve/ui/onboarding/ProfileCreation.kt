package com.fluture.pruvve.ui.onboarding

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.SpannableString
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.text.style.UnderlineSpan
import android.util.Patterns
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.NumberPicker
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.fluture.pruvve.R
import com.fluture.pruvve.databinding.ActivityProfileCreationBinding
import dagger.hilt.android.AndroidEntryPoint
import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.Calendar

@AndroidEntryPoint
class ProfileCreation : AppCompatActivity() {
    private lateinit var binding: ActivityProfileCreationBinding
    override fun onCreate(savedInstanceState: Bundle?) {

        binding = ActivityProfileCreationBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        binding.pruvve1.alpha = 0.5f

        binding.button1.setOnClickListener {
            finish()
        }
        binding.tvgenderview.setOnClickListener {
            showDialogue()
        }
        binding.lldays.setOnClickListener {
            showCustomDialog()
        }

        val text1 = binding.tvtos.text.toString()
        val mySpan = SpannableString(text1)
        setClickableSpan(mySpan, "terms of service")
        setClickableSpan(mySpan, "additional terms")
        setClickableSpan(mySpan, "privacy policy")

        binding.tvtos.apply {
            text = mySpan
            movementMethod = LinkMovementMethod.getInstance()}

        binding.button.setOnClickListener {
            val firstName = binding.firstNameField.text.toString()
            val lastName = binding.lastNameField.text.toString()
            val email = binding.emailaddressfield.text.toString().trim()
            val zipCode = binding.zipCodeField.text.toString()
            val gender = binding.tvgenderview.text.toString()
            val dateOfBirth = "${binding.tvyearview.text}-${binding.tvmonthview.text}-${binding.tvdayview.text}"

            if (validateForm(firstName, lastName, email, gender, zipCode, dateOfBirth)) {
                Intent(this, UsernameCreation::class.java).also {
                    it.putExtra("Extra_firstname", firstName)
                    it.putExtra("Extra_lastname", lastName)
                    it.putExtra("Extra_zipcode", zipCode)
                    it.putExtra("Extra_gender", gender.uppercase())
                    it.putExtra("Extra_dateOfBirth", dateOfBirth)
                    it.putExtra("Extra_email", email)
                    startActivity(it)}
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
        val backButton = dialogView.findViewById<TextView>(R.id.backbuttontc)

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


        backButton.setOnClickListener {
            dialog.dismiss()
        }
        dialogView.setOnClickListener {
            dialog.dismiss()
        }
    }

    private fun showCustomDialog() {
        val dialogView = layoutInflater.inflate(R.layout.date_picker, null)
        val builder = AlertDialog.Builder(this)
        builder.setView(dialogView)
        val dialog = builder.create()

        val dayPicker: NumberPicker = dialogView.findViewById(R.id.dayPicker)
        val monthPicker: NumberPicker = dialogView.findViewById(R.id.monthPicker)
        val yearPicker: NumberPicker = dialogView.findViewById(R.id.yearPicker)
        val monthLabel: TextView = dialogView.findViewById(R.id.monthLabel)

        dayPicker.minValue = 1
        dayPicker.maxValue = 31

        val months = arrayOf(
            "January", "February", "March", "April",
            "May", "June", "July", "August", "September",
            "October", "November", "December"
        )
        monthPicker.minValue = 0
        monthPicker.maxValue = 11

        monthPicker.displayedValues = months
        monthPicker.setOnValueChangedListener { _, _, newVal ->
            monthLabel.text = months[newVal] }

        val currentYear = Calendar.getInstance()[Calendar.YEAR]
        yearPicker.minValue = currentYear - 124
        yearPicker.maxValue = currentYear + 100

        val cal = Calendar.getInstance()
        dayPicker.value = cal[Calendar.DAY_OF_MONTH]
        monthPicker.value = cal[Calendar.MONTH]
        yearPicker.value = cal[Calendar.YEAR]

        dialog.setOnDismissListener {
            val selectedDay = dayPicker.value
            val selectedMonth = (monthPicker.value + 1).toString().padStart(2, '0')
            val selectedYear = yearPicker.value
            binding.tvdayview.text = selectedDay.toString().padStart(2, '0')

            binding.tvmonthview.text = selectedMonth
            binding.tvyearview.text = selectedYear.toString()
        }
        val window = dialog.window
        window?.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT)
        window?.setGravity(Gravity.BOTTOM)
        dialog.show()
    }

    private fun showDialogue(){
        val dialogView = layoutInflater.inflate(R.layout.gender_picker, null)
        val builder = AlertDialog.Builder(this)
        builder.setView(dialogView)
        val dialog = builder.create()

        val genderPicker: NumberPicker = dialogView.findViewById(R.id.genderPicker)
        val genderlabel: TextView = dialogView.findViewById(R.id.genderLabel)
        val gender = arrayOf("Male","Female")
        genderPicker.minValue = 0
        genderPicker.maxValue = 1
        genderPicker.displayedValues = gender
        genderPicker.setOnValueChangedListener { _, _, newVal ->
            genderlabel.text = gender[newVal]
            binding.imgender.visibility = View.VISIBLE
        }

        dialog.setOnDismissListener {
            binding.tvgenderview.text = gender[genderPicker.value]
            binding.imgender.visibility = View.INVISIBLE
        }
        val window = dialog.window
        window?.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT)
        window?.setGravity(Gravity.BOTTOM)
        dialog.show()
    }

    // Function to check if the email is valid
    private fun isValidEmail(email: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun validateForm(
        firstName: String,
        lastName: String,
        email: String,
        gender: String,
        zipCode: String,
        dateOfBirth: String
    ): Boolean {
        val fields = listOf(
            Pair(firstName, binding.firstNameField to "First Name is required"),
            Pair(lastName, binding.lastNameField to "Last Name is required"),
            Pair(email, binding.emailaddressfield to "Valid email is required"),
            Pair(gender, binding.tvgenderview to "Valid gender is required"),
            Pair(zipCode, binding.zipCodeField to "Valid Zip Code is required"),
            Pair(dateOfBirth, binding.tvyearview to "Valid date of birth is required")
        )

        val validGenders = listOf("Male", "Female")
        val pattern =  Patterns.EMAIL_ADDRESS.matcher(email).matches()

        for ((value, pair) in fields) {
            val (field, errorMessage) = pair
            field.error = null // Clear the previous error message

            when (field) {
                binding.firstNameField, binding.lastNameField -> if (value.isEmpty()) {
                    field.error = errorMessage
                    return false
                }
                binding.emailaddressfield -> if (value.isEmpty() || !pattern ) {
                    field.error = errorMessage
                    return false
                }
                binding.tvgenderview -> if (value.isEmpty() || !validGenders.contains(value)) {
                    field.error = errorMessage
                    return false
                }
                binding.zipCodeField -> if (value.isEmpty() || value.length != 5) {
                    field.error = errorMessage
                    return false
                }
                binding.tvyearview -> if (value.isEmpty()) {
                    field.error = errorMessage
                    return false
                }
            }
        }
        return true
    }
}