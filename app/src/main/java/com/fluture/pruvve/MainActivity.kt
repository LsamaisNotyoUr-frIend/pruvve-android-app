package com.fluture.pruvve
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.fluture.pruvve.auth.LoginManager
import com.fluture.pruvve.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val coroutineScope = CoroutineScope(Dispatchers.Main)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        LoginManager.init(this)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        Glide.with(this)
            .load(R.drawable.soccer)
            .into(binding.image24)

        coroutineScope.launch {
            if (LoginManager.isLoggedIn()) {
                // User is logged in, navigate to main screen
                navigateToMainScreen(LoginManager.getAccountType())
            } else {
                // User is not logged in, navigate to login screen
                navigateToLoginScreen()
            }
        }
    }

    private fun navigateToMainScreen(accountType: String?) {
        val intent = Intent(this@MainActivity, HomePage::class.java)
        intent.putExtra("accountType", accountType)
        startActivity(intent)
        finish()
    }

    private fun navigateToLoginScreen() {
        val intent = Intent(this@MainActivity, MainSignup::class.java)
        startActivity(intent)
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        coroutineScope.cancel() // Cancel the coroutine scope to avoid memory leaks
    }
}