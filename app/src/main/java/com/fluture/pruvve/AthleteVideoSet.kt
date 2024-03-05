package com.fluture.pruvve

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.fluture.pruvve.databinding.ActivityAthleteVideoSetBinding
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.text.SimpleDateFormat
import java.util.Base64
import java.util.Date
import java.util.Locale

class AthleteVideoSet : AppCompatActivity() {
    private lateinit var binding:ActivityAthleteVideoSetBinding
    private var videoUri: Uri? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityAthleteVideoSetBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        LoginManager.init(this)
        setContentView(binding.root)
        binding.button1.setOnClickListener {
            finish()
        }
        binding.button.isEnabled = false

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

        binding.intvidplacholder.setOnClickListener {
            openVideoChooser()
        }
        binding.btnaddvideo.setOnClickListener {
            openVideoChooser()
        }

        binding.introvid.setOnPreparedListener {
            binding.intvidplacholder.visibility = View.GONE
            it.isLooping = true
            it.setVideoScalingMode(MediaPlayer.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING)
            binding.introvid.start()
        }
        binding.tvskip.setOnClickListener {
            val firstName = intent.getStringExtra("Extra_firstname").toString()
            val lastName = intent.getStringExtra("Extra_lastname").toString()
            val zipCode = intent.getStringExtra("Extra_zipcode").toString()
            val gender = intent.getStringExtra("Extra_gender").toString()
            val dateOfBirth = intent.getStringExtra("Extra_dateOfBirth").toString()
            val userName = intent.getStringExtra("Extra_username").toString()
            val passWord = intent.getStringExtra("Extra_password").toString()
            val imageAddress = intent.getStringExtra("Extra_profilePic").toString()
            Intent(this, AthleteAccountFinalization::class.java).also {
                it.putExtra("Extra_firstname", firstName)
                it.putExtra("Extra_lastname", lastName)
                it.putExtra("Extra_zipcode", zipCode)
                it.putExtra("Extra_gender", gender)
                it.putExtra("Extra_dateOfBirth", dateOfBirth)
                it.putExtra("Extra_username", userName)
                it.putExtra("Extra_password", passWord)
                it.putExtra("Extra_profilePic", imageAddress)
                startActivity(it)
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
            val imageAddress = intent.getStringExtra("Extra_profilePic").toString()
            val videoToUpload = contentResolver.openInputStream(videoUri!!)?.readBytes()
            val byteVideo = videoToUpload?.let { Base64.getEncoder().encodeToString(it) }
            val filename = generateFilename(userName)
            val fileData = "$byteVideo"
            val file = "$filename:$fileData"
            val purpose = "UPLOAD"
            val uploadImage = UploadImage(
                file,
                purpose)
            service.uploadPicture(uploadImage).enqueue(object : Callback<UploadResponse> {
                override fun onResponse(call: Call<UploadResponse>, response: Response<UploadResponse>) {
                    if (response.isSuccessful){
                        val imageData = UploadData(
                            url = response.body()?.url.toString(),
                            mediaType = "INTRO_VIDEO"
                        )
                        service.uploadData(imageData).enqueue(object : Callback<UploadResponse> {
                            override fun onResponse(call: Call<UploadResponse>, response: Response<UploadResponse>) {
                                if (response.isSuccessful){
                                    Toast.makeText(this@AthleteVideoSet, "File uploaded successfully", Toast.LENGTH_SHORT).show()
                                    Log.d("Retrofit", "The video has been uploaded ${response.body().toString()}")
                                    Intent(this@AthleteVideoSet, AthleteAccountFinalization::class.java).also{
                                        it.putExtra("Extra_firstname", firstName)
                                        it.putExtra("Extra_lastname", lastName)
                                        it.putExtra("Extra_zipcode", zipCode)
                                        it.putExtra("Extra_gender", gender)
                                        it.putExtra("Extra_dateOfBirth", dateOfBirth)
                                        it.putExtra("Extra_username", userName)
                                        it.putExtra("Extra_password", passWord)
                                        it.putExtra("Extra_profilePic", imageAddress)
                                        startActivity(it)
                                    }
                                }else{
                                    val errorMessage = response.errorBody()?.string() ?: "Unknown error"
                                    Log.e("RetrofitError", "Error uploading file $errorMessage")
                                }
                            }
                            override fun onFailure(call: Call<UploadResponse>, t: Throwable) {
                                Log.e("RetrofitError", "Error reaching server ${t.message.toString()}")
                            }
                        })
                    }else{
                        val errorMessage = response.errorBody()?.string() ?: "Unknown error"
                        Log.e("RetrofitError", "An error occurred when sending image $errorMessage")
                    }
                }

                override fun onFailure(call: Call<UploadResponse>, t: Throwable) {
                    Log.e("RetrofitError", "There was a problem reaching the server ${t.message.toString()}")
                }
            })
        }
    }
    private fun openVideoChooser() {
        val intent = Intent(Intent.ACTION_GET_CONTENT).also {
            it.type = "video/*"
        }
        resultLauncher.launch(intent)
    }
    @SuppressLint("SetTextI18n")
    private val resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data
            data?.data?.let { uri ->
                binding.introvid.setMediaController(null)
                videoUri = uri
                binding.introvid.setVideoURI(uri)
                binding.btnaddvideo.text = "Add Another Video"
                binding.button.isEnabled = true
                binding.button.setBackgroundResource(R.drawable.primary_button)
            }
        }
    }
    private fun generateFilename(username: String): String {
        val currentTimeMillis = System.currentTimeMillis()
        val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date(currentTimeMillis))
        return "${username}_$timestamp"
    }
}