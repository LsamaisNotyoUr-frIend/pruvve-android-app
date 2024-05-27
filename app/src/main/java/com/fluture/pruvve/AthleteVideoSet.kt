package com.fluture.pruvve

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.fluture.pruvve.auth.AuthInterceptor
import com.fluture.pruvve.auth.LoginManager
import com.fluture.pruvve.retrofittcalls.UploadData
import com.fluture.pruvve.retrofittcalls.UploadImage
import com.fluture.pruvve.retrofittcalls.UploadResponse
import com.fluture.pruvve.retrofittcalls.UserService
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.fluture.pruvve.databinding.ActivityAthleteVideoSetBinding
import com.fluture.pruvve.essentials.VideoUploader
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.InputStream
import java.text.SimpleDateFormat
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
            val userName = intent.getStringExtra("Extra_username").toString()
            Intent(this, AthleteAccountFinalization::class.java).also {
                it.putExtra("Extra_username", userName)
                startActivity(it)
            }
        }
        binding.button.setOnClickListener {
            binding.button.setBackgroundResource(R.drawable.disabled_button)
            binding.button.isEnabled = false
            val userName = intent.getStringExtra("Extra_username").toString()
            val filename = generateFilename(userName)
            val purpose = "UPLOAD"
            val purpose2 = "DOWNLOAD"
            val uploadImage = UploadImage(
                filename,
                purpose)
            val uploadImage2 = UploadImage(
                filename,
                purpose2)
            service.uploadPicture(uploadImage).enqueue(object : Callback<UploadResponse> {
                override fun onResponse(call: Call<UploadResponse>, response: Response<UploadResponse>) {
                    if (response.isSuccessful){
                        val signedUrl = response.body()?.data.toString()
                        val videoUploader = VideoUploader()
                        videoUploader.uploadVideo(uriToByteArray(this@AthleteVideoSet, videoUri!!)!!, signedUrl)
                        service.uploadPicture(uploadImage2).enqueue(object: Callback<UploadResponse>{
                            override fun onResponse(call: Call<UploadResponse>, response: Response<UploadResponse>) {
                                if (response.isSuccessful){
                                    Log.d("RetrofitUrl", "your url is ${response.body()?.data}")
                                    val imageData = UploadData(
                                        mediaUrl = filename,
                                        mediaType = "INTRO_VIDEO")
                                    service.uploadData(imageData).enqueue(object : Callback<UploadResponse> {
                                        override fun onResponse(call: Call<UploadResponse>, response: Response<UploadResponse>) {
                                            if (response.isSuccessful){
                                                Toast.makeText(this@AthleteVideoSet, "File uploaded successfully", Toast.LENGTH_SHORT).show()
                                                Log.d("Retrofit", "The video has been uploaded ${response.body().toString()}")
                                                Intent(this@AthleteVideoSet, AthleteAccountFinalization::class.java).also{
                                                    it.putExtra("Extra_username", userName)
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
                                }
                                else{
                                    Log.e("RetrofitError", "Error: ${response.errorBody()}")
                                    binding.button.setBackgroundResource(R.drawable.primary_button)
                                    binding.button.isEnabled = true
                                }
                            }
                            override fun onFailure(call: Call<UploadResponse>, t: Throwable) {
                                Log.e("RetrofitFailure", t.message.toString())
                                binding.button.setBackgroundResource(R.drawable.primary_button)
                                binding.button.isEnabled = true
                            }
                        })
                    }else{
                        val errorMessage = response.errorBody()?.string() ?: "Unknown error"
                        Log.e("RetrofitError", "An error occurred when sending image $errorMessage")
                        binding.button.setBackgroundResource(R.drawable.primary_button)
                        binding.button.isEnabled = true
                    }
                }
                override fun onFailure(call: Call<UploadResponse>, t: Throwable) {
                    Log.e("RetrofitError", "There was a problem reaching the server ${t.message.toString()}")
                    binding.button.setBackgroundResource(R.drawable.primary_button)
                    binding.button.isEnabled = true
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

    fun uriToByteArray(context: Context, uri: Uri): ByteArray? {
        var inputStream: InputStream? = null
        var byteArrayOutputStream: ByteArrayOutputStream? = null
        var bytes: ByteArray? = null
        try {
            inputStream = context.contentResolver.openInputStream(uri)
            byteArrayOutputStream = ByteArrayOutputStream()
            inputStream?.use { input ->
                byteArrayOutputStream.use { output ->
                    input.copyTo(output)
                }
            }
            bytes = byteArrayOutputStream.toByteArray()
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            inputStream?.close()
            byteArrayOutputStream?.close()
        }
        return bytes
    }
}