package com.fluture.pruvve

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.fluture.pruvve.adapters.GetUserResponse
import com.fluture.pruvve.auth.AuthInterceptor
import com.fluture.pruvve.auth.LoginManager
import com.fluture.pruvve.databinding.ActivityCutomGalleryBinding
import com.fluture.pruvve.essentials.ImageUploader
import com.fluture.pruvve.essentials.VideoUploader
import com.fluture.pruvve.retrofittcalls.PostsMedia
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

class CustomGallery : AppCompatActivity(){
    private lateinit var binding: ActivityCutomGalleryBinding
    private var imageUri: Uri? = null
    private var videoUri: Uri? = null
    private var imageChosen: Boolean = false
    private lateinit var username: String
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityCutomGalleryBinding.inflate(layoutInflater)
        LoginManager.init(this)
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)

        openImageChooser()

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

        binding.btnAddMedia.isEnabled = false

        service.getUserCredentials().enqueue(object : Callback<GetUserResponse>{
            override fun onResponse(call: Call<GetUserResponse>, response: Response<GetUserResponse>) {
                if (response.isSuccessful){
                    username = response.body()?.data?.username.toString()
                    Log.e("RetrofitSuccess", "User credentials for $username gotten successfully")
                }else{
                    Log.e("RetrofitError", "Could not get user credentials")
                }
            }
            override fun onFailure(call: Call<GetUserResponse>, t: Throwable) {
                Log.e("RetrofitFailure", "Could not reach the server")
            }
        })

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.clGallery)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.btnAddMedia.setOnClickListener{
            binding.btnAddMedia.setBackgroundResource(R.drawable.disabled_button)
            binding.btnAddMedia.isEnabled = false
            sortUpload(service)
        }
    }

    private fun sortUpload(service: UserService){
        val errorName = "Unknown error"
        val filename = generateFilename(username)
        val purpose = "UPLOAD"
        val uploadImage = UploadImage(
            filename,
            purpose)
        if (imageChosen){
            service.uploadPicture(uploadImage).enqueue(object : Callback<UploadResponse>{
                override fun onResponse(call: Call<UploadResponse>, response: Response<UploadResponse>)
                {
                    if(response.isSuccessful){
                        val signedUrl= response.body()?.data.toString()
                        val imageUploader = ImageUploader()
                        val imageData = UploadData(
                            mediaUrl = filename,
                            mediaType = "PROFILE_PICTURE"
                        )
                        imageUploader.uploadImage((uriToByteArray(this@CustomGallery, imageUri!!)!!), signedUrl)
                        service.uploadData(imageData).enqueue(object: Callback<UploadResponse>{
                            override fun onResponse(call: Call<UploadResponse>, response: Response<UploadResponse>)
                            {
                                if (response.isSuccessful){
                                    val post = PostsMedia(
                                        mediaUrl = filename,
                                        mediaType = "IMAGE",
                                        caption = binding.etAddMediaCaption.text.toString()
                                    )
                                    service.makePost(post).enqueue(object: Callback<UploadResponse>{
                                        override fun onResponse(call: Call<UploadResponse>, response: Response<UploadResponse>)
                                        {
                                            if (response.isSuccessful){
                                                Log.d("RetrofitSuccess", response.body()?.message.toString())
                                                finish()
                                            }else{
                                                val errorMessage = response.errorBody()?.string() ?: errorName
                                                Log.e("RetrofitError", "Error uploading file $errorMessage")
                                                binding.btnAddMedia.setBackgroundResource(R.drawable.primary_button)
                                                binding.btnAddMedia.isEnabled = true
                                            }
                                        }
                                        override fun onFailure(call: Call<UploadResponse>, t: Throwable){
                                            Log.e("RetrofitFailure", t.message.toString())
                                            binding.btnAddMedia.setBackgroundResource(R.drawable.primary_button)
                                            binding.btnAddMedia.isEnabled = true
                                        }
                                    })
                                }else{
                                    val errorMessage = response.errorBody()?.string() ?: errorName
                                    Log.e("RetrofitError", "Error uploading file $errorMessage")
                                    binding.btnAddMedia.setBackgroundResource(R.drawable.primary_button)
                                    binding.btnAddMedia.isEnabled = true
                                }
                            }

                            override fun onFailure(call: Call<UploadResponse>, t: Throwable) {
                                Log.e("RetrofitFailure", "problem reaching server ${t.message.toString()}")
                                binding.btnAddMedia.setBackgroundResource(R.drawable.primary_button)
                                binding.btnAddMedia.isEnabled = true
                            }
                        })
                    }else{
                        val errorMessage = response.errorBody()?.string() ?: errorName
                        Log.e("RetrofitError", "Error uploading file $errorMessage")
                        binding.btnAddMedia.setBackgroundResource(R.drawable.primary_button)
                        binding.btnAddMedia.isEnabled = true
                    }
                }
                override fun onFailure(call: Call<UploadResponse>, t: Throwable) {
                    Log.e("RetrofitFailure", "problem reaching server ${t.message.toString()}")
                    binding.btnAddMedia.setBackgroundResource(R.drawable.primary_button)
                    binding.btnAddMedia.isEnabled = true
                }
            })
        }else{
            service.uploadPicture(uploadImage).enqueue(object : Callback<UploadResponse>{
                override fun onResponse(call: Call<UploadResponse>, response: Response<UploadResponse>){
                    if(response.isSuccessful){
                        val signedUrl= response.body()?.data.toString()
                        val imageUploader = VideoUploader()
                        val imageData = UploadData(
                            mediaUrl = filename,
                            mediaType = "INTRO_VIDEO")
                        imageUploader.uploadVideo((uriToByteArray(this@CustomGallery, videoUri!!)!!), signedUrl)
                        service.uploadData(imageData).enqueue(object: Callback<UploadResponse>{
                            override fun onResponse(call: Call<UploadResponse>, response: Response<UploadResponse>)
                            {
                                if (response.isSuccessful){
                                    val post = PostsMedia(
                                        mediaUrl = filename,
                                        mediaType = "VIDEO",
                                        caption = binding.etAddMediaCaption.text.toString()
                                    )
                                    service.makePost(post).enqueue(object: Callback<UploadResponse>{
                                        override fun onResponse(call: Call<UploadResponse>, response: Response<UploadResponse>)
                                        {
                                            if (response.isSuccessful){
                                                Log.d("RetrofitSuccess", response.body()?.message.toString())
                                                finish()
                                            }else{
                                                val errorMessage = response.errorBody()?.string() ?: errorName
                                                Log.e("RetrofitError", "Error uploading file $errorMessage")
                                                binding.btnAddMedia.setBackgroundResource(R.drawable.primary_button)
                                                binding.btnAddMedia.isEnabled = true
                                            }
                                        }
                                        override fun onFailure(call: Call<UploadResponse>, t: Throwable){
                                            Log.e("RetrofitFailure", t.message.toString())
                                            binding.btnAddMedia.setBackgroundResource(R.drawable.primary_button)
                                            binding.btnAddMedia.isEnabled = true
                                        }
                                    })
                                }else{
                                    val errorMessage = response.errorBody()?.string() ?: errorName
                                    Log.e("RetrofitError", "Error uploading file $errorMessage")
                                    binding.btnAddMedia.setBackgroundResource(R.drawable.primary_button)
                                    binding.btnAddMedia.isEnabled = true
                                }
                            }

                            override fun onFailure(call: Call<UploadResponse>, t: Throwable) {
                                Log.e("RetrofitFailure", "problem reaching server ${t.message.toString()}")
                                binding.btnAddMedia.setBackgroundResource(R.drawable.primary_button)
                                binding.btnAddMedia.isEnabled = true
                            }
                        })
                    }else{
                        val errorMessage = response.errorBody()?.string() ?: errorName
                        Log.e("RetrofitError", "Error uploading file $errorMessage")
                        binding.btnAddMedia.setBackgroundResource(R.drawable.primary_button)
                        binding.btnAddMedia.isEnabled = true
                    }
                }
                override fun onFailure(call: Call<UploadResponse>, t: Throwable) {
                    Log.e("RetrofitFailure", t.message.toString())
                    binding.btnAddMedia.setBackgroundResource(R.drawable.primary_button)
                    binding.btnAddMedia.isEnabled = true
                }
            })
        }
    }
    private fun openImageChooser() {
        val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
            type = "image/* video/*"
        }
        resultLauncher.launch(intent)
    }

    @SuppressLint("SetTextI18n")
    private val resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data
            data?.data?.let { uri ->
                if (isImage(uri)) {
                    imageUri = uri
                    videoUri = null
                    imageChosen = true
                    binding.imvAddAPost.setImageURI(uri)
                    Log.d("RetrofitImage", "your file is an image")
                } else if (isVideo(uri)) {
                    videoUri = uri
                    imageUri = null
                    imageChosen = false
                    binding.imvAddAPost.setImageURI(uri)
                    Log.d("RetrofitImage", "your file is a video")
                }
                binding.btnAddMedia.setBackgroundResource(R.drawable.primary_button)
                binding.btnAddMedia.isEnabled = true
            }
        }
    }

    private fun isImage(uri: Uri): Boolean {
        return contentResolver.getType(uri)?.startsWith("image") ?: false
    }

    private fun isVideo(uri: Uri): Boolean {
        return contentResolver.getType(uri)?.startsWith("video") ?: false
    }
    private fun generateFilename(username: String): String {
        val currentTimeMillis = System.currentTimeMillis()
        val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date(currentTimeMillis))
        return "${username}_${timestamp}_Post"
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