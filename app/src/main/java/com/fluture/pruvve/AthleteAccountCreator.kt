package com.fluture.pruvve

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.fluture.pruvve.auth.AuthInterceptor
import com.fluture.pruvve.auth.LoginManager
import com.fluture.pruvve.databinding.ActivityPlayerAccountCreatorBinding
import com.fluture.pruvve.essentials.ImageUploader
import com.fluture.pruvve.retrofittcalls.UploadData
import com.fluture.pruvve.retrofittcalls.UploadImage
import com.fluture.pruvve.retrofittcalls.UploadResponse
import com.fluture.pruvve.retrofittcalls.UserService
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
            binding.button.setBackgroundResource(R.drawable.disabled_button)
            binding.button.isEnabled = false
            val userName = intent.getStringExtra("Extra_username").toString()
            Log.e("RetrofitUri", "your uri is: $imageUri")
            val filename = generateFilename(userName)
            val purpose = "UPLOAD"
            val purpose2 = "DOWNLOAD"
            val uploadImage = UploadImage(
                filename,
                purpose)
            val uploadImage2 = UploadImage(
                filename,
                purpose2)
            service.uploadPicture(uploadImage).enqueue(object : Callback<UploadResponse>{
                override fun onResponse(call: Call<UploadResponse>, response: Response<UploadResponse>) {
                    if (response.isSuccessful){
                        val signedUrl= response.body()?.data.toString()
                        val imageData = UploadData(
                            mediaUrl = filename,
                            mediaType = "PROFILE_PICTURE"
                        )
                        Log.d("RetrofitUrl", "Your url is $signedUrl")
                        val imageUploader = ImageUploader()

                        imageUploader.uploadImage(uriToByteArray(this@AthleteAccountCreator, imageUri!!)!!, signedUrl)
                        service.uploadPicture(uploadImage2).enqueue(object : Callback<UploadResponse>{
                            override fun onResponse(call: Call<UploadResponse>, response: Response<UploadResponse>) {
                                if (response.isSuccessful){
                                    Log.d("RetrofitUrl", "Your url is ${response.body()?.data.toString()}")
                                    LoginManager.saveProfileUrl(response.body()?.data ?: "empty")
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
                                                binding.button.setBackgroundResource(R.drawable.primary_button)
                                                binding.button.isEnabled = true
                                            }
                                        }
                                        override fun onFailure(call: Call<UploadResponse>, t: Throwable) {
                                            Log.e("RetrofitError", "Error reaching server ${t.message.toString()}")
                                            binding.button.setBackgroundResource(R.drawable.primary_button)
                                            binding.button.isEnabled = true
                                        }
                                    })

                                }else {
                                    Log.e("RetrofitError", response.errorBody().toString())
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
                binding.btnaddimage.text = "Change Profile Picture"
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