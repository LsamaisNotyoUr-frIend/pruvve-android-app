package com.fluture.pruvve

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.fluture.pruvve.databinding.ActivityPlayerAccountCreatorBinding
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

class AthleteAccountCreator : AppCompatActivity() {
    private lateinit var binding: ActivityPlayerAccountCreatorBinding
    private var imageUri: Uri? = null

    @SuppressLint("Recycle")
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityPlayerAccountCreatorBinding.inflate(layoutInflater)
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


        binding.profpicplacholder.setOnClickListener {
            openImageChooser()
        }

        binding.btnaddimage.setOnClickListener {
            openImageChooser()
        }

        binding.button.setOnClickListener {
            val userName = intent.getStringExtra("Extra_username").toString()
            Log.e("RetrofitUri", "your uri is: $imageUri")
            val imageToUpload = contentResolver.openInputStream(imageUri!!)?.readBytes()
            val byteImage = imageToUpload?.let { Base64.getEncoder().encodeToString(imageToUpload) }
            val filename = generateFilename(userName)
            val fileData = byteImage.toString()
            val file = "$filename:$fileData"
            val purpose = "UPLOAD"
            val uploadImage = UploadImage(
                file,
                purpose)
            service.uploadPicture(uploadImage).enqueue(object : Callback<UploadResponse>{
                override fun onResponse(call: Call<UploadResponse>, response: Response<UploadResponse>) {
                    if (response.isSuccessful){
                        Log.d("RetrofitUrl", "Your url is ${response.body()?.data.toString()}")
                        val imageData = UploadData(
                            url = response.body()?.data.toString(),
                            mediaType = "PROFILE_PICTURE"
                        )
                        service.uploadData(imageData).enqueue(object : Callback<UploadResponse>{
                            override fun onResponse(call: Call<UploadResponse>, response: Response<UploadResponse>) {
                                if (response.isSuccessful){
                                    Toast.makeText(this@AthleteAccountCreator, "File uploaded successfully", Toast.LENGTH_SHORT).show()
                                    Log.d("Retrofit", "The image has been uploaded ${response.body().toString()}")
                                    Intent(this@AthleteAccountCreator, AthleteVideoSet::class.java).also{
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

    private fun openImageChooser() {
        val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
            type = "image/*"
        }
        resultLauncher.launch(intent)
    }

    @SuppressLint("SetTextI18n")
    private val resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data
            data?.data?.let { uri ->
                imageUri = uri
                binding.profpicplacholder.setImageURI(uri)
                binding.btnaddimage.text = "Add Another Image"
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