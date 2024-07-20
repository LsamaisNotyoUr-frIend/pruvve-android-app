package com.fluture.pruvve

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.SpannableString
import android.text.TextWatcher
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
import androidx.appcompat.app.AppCompatActivity
import com.fluture.pruvve.adapters.LoginResponse
import com.fluture.pruvve.auth.LoginManager
import com.fluture.pruvve.databinding.ActivityUsernameCreationBinding
import com.fluture.pruvve.retrofittcalls.LoginInfo
import com.fluture.pruvve.retrofittcalls.User
import com.fluture.pruvve.retrofittcalls.UserService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.io.BufferedReader
import java.io.InputStreamReader

class UsernameCreation : AppCompatActivity() {
    private lateinit var binding: ActivityUsernameCreationBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityUsernameCreationBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        LoginManager.init(this)
        setContentView(binding.root)
        val service = Retrofit.Builder()
            .baseUrl("https://pruvve-backend-9a89de78d2a1.herokuapp.com/api/")
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
            .create(UserService::class.java)

        val firstName = intent.getStringExtra("Extra_firstname").toString()
        val lastName = intent.getStringExtra("Extra_lastname").toString()
        val zipCode = intent.getStringExtra("Extra_zipcode").toString()
        val gender = intent.getStringExtra("Extra_gender").toString()
        val dateOfBirth = intent.getStringExtra("Extra_dateOfBirth").toString()
        val email = intent.getStringExtra("Extra_email").toString()

        binding.button1.setOnClickListener {
            finish()
        }

        val text1 = binding.tvtos.text.toString()
        val mySpan = SpannableString(text1)
        setClickableSpan(mySpan, "terms of service")
        setClickableSpan(mySpan, "additional terms")
        setClickableSpan(mySpan, "privacy policy")

        binding.tvtos.apply {
            text = mySpan
            movementMethod = LinkMovementMethod.getInstance()
        }

        val textWatcher = object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val userName = binding.usernameField.text.toString()
                val passWord = binding.passwordField.text.toString()

                binding.usernameLengthChecker.text = if (userName.length in 3..11) "Good" else "Must be between 2 and 12 characters"
                binding.passwordLengthChecker.text = if (passWord.length >= 8) "Good" else "Must be 8 or more characters"

                binding.button.isEnabled = userName.isNotEmpty() && passWord.isNotEmpty() &&
                        userName.length in 3..11 && passWord.length >= 8

                val buttonBackground = if (binding.button.isEnabled) R.drawable.primary_button else R.drawable.secondary_button
                binding.button.setBackgroundResource(buttonBackground)
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // No action needed before text changes
            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // No action needed before text changes
            }
        }
        binding.usernameField.addTextChangedListener(textWatcher)
        binding.passwordField.addTextChangedListener(textWatcher)

        binding.button.setOnClickListener {
            binding.button.setBackgroundResource(R.drawable.disabled_button)
            binding.button.isEnabled = false
            val username = binding.usernameField.text.toString()
            val password = binding.passwordField.text.toString()
            val userToCreate = User(
                firstName = firstName.trim(),
                lastName = lastName.trim(),
                email = email.trim(),
                zipCode = zipCode,
                gender = gender,
                dateOfBirth = dateOfBirth,
                username = username.trim(),
                password = password.trim()
            )
            service.createUser(userToCreate).enqueue(object : Callback<User> {
                override fun onResponse(call: Call<User>, response: Response<User>) {
                    if (response.isSuccessful) {
                        Toast.makeText(this@UsernameCreation, "User created successfully", Toast.LENGTH_SHORT).show()
                        val userLogin = LoginInfo(
                            username,
                            password
                        )
                        service.getUser(userLogin).enqueue(object : Callback<LoginResponse> {
                            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                                if (response.isSuccessful) {
                                    val token = response.body()?.data?.token.toString()
                                    Log.d("RetrofitToken", token)
                                    Log.d("RetrofitLogin", "User Logged in successfully")
                                    LoginManager.saveToken(token)
                                    response.body()?.data?.user?.let {
                                        LoginManager.saveUserInfo(it.id, it.username, it.accountType)
                                    }
                                    Intent(this@UsernameCreation, AccountTypeSelector::class.java).also {
                                        it.putExtra("Extra_username", username)
                                        startActivity(it)
                                    }
                                } else {
                                    Log.e("RetrofitError", "User authorization failed: ${response.errorBody()?.toString()}")
                                    Toast.makeText(this@UsernameCreation, "Couldn't authorize user", Toast.LENGTH_SHORT).show()
                                }
                            }
                            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                                Log.e("RetrofitError", "Authorization failed: ${t.message.toString()}")
                                Toast.makeText(this@UsernameCreation, "Error during authorization", Toast.LENGTH_SHORT).show()
                            }
                        })
                    } else {
                        Log.e("RetrofitError", "RetrofitError:${response.errorBody()?.string()!!}")
                        Toast.makeText(this@UsernameCreation, "User creation failed", Toast.LENGTH_SHORT).show()
                        binding.button.setBackgroundResource(R.drawable.primary_button)
                        binding.button.isEnabled = true
                    }
                }

                override fun onFailure(call: Call<User>, t: Throwable) {
                    Log.e("RetrofitFailure", "error: ${t.message.toString()}")
                    Toast.makeText(this@UsernameCreation, "User could not be created", Toast.LENGTH_SHORT).show()
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
}