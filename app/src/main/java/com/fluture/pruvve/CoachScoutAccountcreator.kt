package com.fluture.pruvve

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.fluture.pruvve.auth.AuthInterceptor
import com.fluture.pruvve.auth.LoginManager
import com.fluture.pruvve.databinding.ActivityCoachScoutAccountcreatorBinding
import com.fluture.pruvve.retrofittcalls.InputStreamRequestBody
import com.fluture.pruvve.retrofittcalls.UploadData
import com.fluture.pruvve.retrofittcalls.UploadImage
import com.fluture.pruvve.retrofittcalls.UploadResponse
import com.fluture.pruvve.data.api.UserService
import dagger.hilt.android.AndroidEntryPoint
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@AndroidEntryPoint
class CoachScoutAccountcreator : AppCompatActivity() {
    private lateinit var binding: ActivityCoachScoutAccountcreatorBinding
    private var imageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCoachScoutAccountcreatorBinding.inflate(layoutInflater)
        LoginManager.init(this)
        setContentView(binding.root)

        binding.button1.setOnClickListener {
            finish()
        }
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

        binding.button.isEnabled = false

        binding.profpicplacholder.setOnClickListener {
            openImageChooser()
        }

        binding.btnaddimage.setOnClickListener {
            openImageChooser()
        }

        binding.button.setOnClickListener {
            val userName = intent.getStringExtra("Extra_username").toString()
            Log.e("RetrofitUri", "your uri is: $imageUri")
            val imageToUpload = imageUri
            val filename = generateFilename(userName)
            val purpose = "UPLOAD"
            val uploadImage = UploadImage(
                filename,
                purpose)
            service.uploadPicture(uploadImage).enqueue(object : Callback<UploadResponse>{
                override fun onResponse(call: Call<UploadResponse>, response: Response<UploadResponse>) {
                    if (response.isSuccessful){
                        val signedUrl= response.body()?.data.toString()
                        val imageData = UploadData(
                            mediaUrl = filename,
                            mediaType = "PROFILE_PICTURE"
                        )
                        Log.d("RetrofitUrl", "Your url is $signedUrl")
                        uploadFile(signedUrl, imageToUpload!!)
                        service.uploadData(imageData).enqueue(object : Callback<UploadResponse>{
                            override fun onResponse(call: Call<UploadResponse>, response: Response<UploadResponse>) {
                                if (response.isSuccessful){
                                    Toast.makeText(this@CoachScoutAccountcreator, "File uploaded successfully", Toast.LENGTH_SHORT).show()
                                    Log.d("Retrofit", "The image has been uploaded ${response.body().toString()}")
                                    Intent(this@CoachScoutAccountcreator, CoachAccountFinalization::class.java).also{
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
                binding.btnaddimage.text = "Change profile picture"
                binding.button.isEnabled = true
                binding.button.setBackgroundResource(R.drawable.primary_button)
            }
        }
    }
    private fun generateFilename(username: String): String {
        val currentTimeMillis = System.currentTimeMillis()
        val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date(currentTimeMillis))
        return "${username}_${timestamp}_ProfilePic"
    }
    fun uploadFile(url: String, fileUri: Uri) {
        LoginManager.init(this)
        val token = LoginManager.getToken()
        val httpClient = OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor(token.toString()))
            .build()
        val service = Retrofit.Builder()
            .baseUrl("https://pruvve-backend-9a89de78d2a1.herokuapp.com/api/")
            .client(httpClient)
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
            .create(UserService::class.java)
        contentResolver.getType(fileUri)?.let { mimeType ->
            val requestBody = InputStreamRequestBody(contentResolver, fileUri)
            service.uploadFile(mimeType, url, requestBody).enqueue(object: Callback<ResponseBody>{
                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                    Log.d("RetrofitUploadSuccess", response.body().toString())
                    Log.d("RetrofitUploadSuccess", "your file has been uploaded to $url")
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    Log.e("RetrofitUploadFailure", "your file could not be uploaded ${t.message.toString()}")
                }
            })
        }
    }
}