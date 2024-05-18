package com.fluture.pruvve

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentUris
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.GridLayoutManager
import com.fluture.pruvve.adapters.GetUserResponse
import com.fluture.pruvve.adapters.MediaAdapter
import com.fluture.pruvve.adapters.MediaFilter
import com.fluture.pruvve.auth.AuthInterceptor
import com.fluture.pruvve.auth.LoginManager
import com.fluture.pruvve.databinding.ActivityStoryMakerBinding
import com.fluture.pruvve.essentials.ImageUploader
import com.fluture.pruvve.essentials.VideoUploader
import com.fluture.pruvve.retrofittcalls.PostsMedia
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

class StoryMaker : AppCompatActivity() {
    private lateinit var binding:ActivityStoryMakerBinding
    private var imageUri: Uri? = null
    private var videoUri: Uri? = null
    private var imageChosen: Boolean = false
    private lateinit var username: String
    private val mediaList = mutableListOf<MediaFilter>()
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityStoryMakerBinding.inflate(layoutInflater)
        LoginManager.init(this)
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        loadMediaFiles()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.clStoryMaker)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
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

        val recyclerView = binding.rvGalleryChooser
        recyclerView.layoutManager = GridLayoutManager(this, 2, GridLayoutManager.VERTICAL, false)

        val mediaAdapter = MediaAdapter(mediaList) { uri ->
            onMediaItemSelected(uri)
        }
        recyclerView.adapter = mediaAdapter

        service.getUserCredentials().enqueue(object : Callback<GetUserResponse> {
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
        binding.btnBack.setOnClickListener {
            finish()
        }

        binding.btnDone.setOnClickListener {
            binding.btnDone.setBackgroundResource(R.drawable.disabled_button)
            binding.btnDone.isEnabled = false
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
                override fun onResponse(call: Call<UploadResponse>, response: Response<UploadResponse>) {
                    if(response.isSuccessful){
                        val signedUrl= response.body()?.data.toString()
                        val imageUploader = ImageUploader()
                        imageUploader.uploadImage((uriToByteArray(this@StoryMaker, imageUri!!)!!), signedUrl)
                        val post = PostsMedia(
                            mediaUrl = filename,
                            mediaType = "IMAGE",
                            caption = binding.etStoryCaption.text.toString()
                        )
                        service.postStory(post).enqueue(object: Callback<UploadResponse>{
                            override fun onResponse(call: Call<UploadResponse>, response: Response<UploadResponse>)
                            {
                                if (response.isSuccessful){
                                    Log.d("RetrofitSuccess", response.body()?.message.toString())
                                    Intent(this@StoryMaker, MorePage::class.java).also {
                                        startActivity(it)
                                    }
                                    finish()
                                }else{
                                    val errorMessage = response.errorBody()?.string() ?: errorName
                                    Log.e("RetrofitError", "Error uploading file $errorMessage")
                                    binding.btnDone.setBackgroundResource(R.drawable.primary_button)
                                    binding.btnDone.isEnabled = true
                                }
                            }
                            override fun onFailure(call: Call<UploadResponse>, t: Throwable){
                                Log.e("RetrofitFailure", t.message.toString())
                                binding.btnDone.setBackgroundResource(R.drawable.primary_button)
                                binding.btnDone.isEnabled = true
                            }
                        })
                    }else{
                        val errorMessage = response.errorBody()?.string() ?: errorName
                        Log.e("RetrofitError", "Error uploading file $errorMessage")
                        binding.btnDone.setBackgroundResource(R.drawable.primary_button)
                        binding.btnDone.isEnabled = true
                    }
                }
                override fun onFailure(call: Call<UploadResponse>, t: Throwable) {
                    Log.e("RetrofitFailure", "problem reaching server ${t.message.toString()}")
                    binding.btnDone.setBackgroundResource(R.drawable.primary_button)
                    binding.btnDone.isEnabled = true
                }
            })
        }else{
            service.uploadPicture(uploadImage).enqueue(object : Callback<UploadResponse>{
                override fun onResponse(call: Call<UploadResponse>, response: Response<UploadResponse>){
                    if(response.isSuccessful){
                        val signedUrl= response.body()?.data.toString()
                        val imageUploader = VideoUploader()
                        imageUploader.uploadVideo((uriToByteArray(this@StoryMaker, videoUri!!)!!), signedUrl)
                        val post = PostsMedia(
                            mediaUrl = filename,
                            mediaType = "VIDEO",
                            caption = binding.etStoryCaption.text.toString()
                        )
                        service.postStory(post).enqueue(object: Callback<UploadResponse>{
                            override fun onResponse(call: Call<UploadResponse>, response: Response<UploadResponse>)
                            {
                                if (response.isSuccessful){
                                    Log.d("RetrofitSuccess", response.body()?.message.toString())
                                    Intent(this@StoryMaker, MorePage::class.java).also {
                                        startActivity(it)
                                    }
                                    finish()
                                }else{
                                    val errorMessage = response.errorBody()?.string() ?: errorName
                                    Log.e("RetrofitError", "Error uploading file $errorMessage")
                                    binding.btnDone.setBackgroundResource(R.drawable.primary_button)
                                    binding.btnDone.isEnabled = true
                                }
                            }
                            override fun onFailure(call: Call<UploadResponse>, t: Throwable){
                                Log.e("RetrofitFailure", t.message.toString())
                                binding.btnDone.setBackgroundResource(R.drawable.primary_button)
                                binding.btnDone.isEnabled = true
                            }
                        })
                    }else{
                        val errorMessage = response.errorBody()?.string() ?: errorName
                        Log.e("RetrofitError", "Error uploading file $errorMessage")
                        binding.btnDone.setBackgroundResource(R.drawable.primary_button)
                        binding.btnDone.isEnabled = true
                    }
                }
                override fun onFailure(call: Call<UploadResponse>, t: Throwable) {
                    Log.e("RetrofitFailure", t.message.toString())
                    binding.btnDone.setBackgroundResource(R.drawable.primary_button)
                    binding.btnDone.isEnabled = true
                }
            })
        }
    }

    private fun openMediaChooser() {
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
                    binding.imvAddAStory.loadUrl(uri.toString())
                    Log.d("RetrofitImage", "your file is an image")
                } else if (isVideo(uri)) {
                    videoUri = uri
                    imageUri = null
                    imageChosen = false
                    binding.imvAddAStory.loadUrl(uri.toString())
                    Log.d("RetrofitImage", "your file is a video")
                }
                binding.btnDone.setBackgroundResource(R.drawable.primary_button)
                binding.btnDone.isEnabled = true
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

    private fun loadMediaFiles() {
        val projection = arrayOf(
            MediaStore.Files.FileColumns._ID,
            MediaStore.Files.FileColumns.MEDIA_TYPE
        )

        val selection = "${MediaStore.Files.FileColumns.MEDIA_TYPE}=? OR ${MediaStore.Files.FileColumns.MEDIA_TYPE}=?"
        val selectionArgs = arrayOf(
            MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE.toString(),
            MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO.toString()
        )

        val queryUri = MediaStore.Files.getContentUri("external")

        val cursor: Cursor? = contentResolver.query(
            queryUri,
            projection,
            selection,
            selectionArgs,
            null
        )

        cursor?.use {
            val idColumn = it.getColumnIndexOrThrow(MediaStore.Files.FileColumns._ID)
            val typeColumn = it.getColumnIndexOrThrow(MediaStore.Files.FileColumns.MEDIA_TYPE)

            while (it.moveToNext()) {
                val id = it.getLong(idColumn)
                val mediaType = it.getInt(typeColumn)
                val contentUri: Uri = if (mediaType == MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE) {
                    ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)
                } else {
                    ContentUris.withAppendedId(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, id)
                }
                mediaList.add(MediaFilter(contentUri))
            }
        }
    }
    private fun onMediaItemSelected(uri: Uri) {
        if (isImage(uri)) {
            imageUri = uri
            Log.d("Url", "your uri has been gotten")
        } else if (isVideo(uri)) {
            videoUri = uri
            Log.d("Url", "your video has been gotten")
        }
    }
}