@file:Suppress("DEPRECATION")

package com.fluture.pruvve

import android.Manifest.permission.CAMERA
import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.fluture.pruvve.adapters.GetUserResponse
import com.fluture.pruvve.auth.AuthInterceptor
import com.fluture.pruvve.auth.LoginManager
import com.fluture.pruvve.databinding.ActivityStoryMakerBinding
import com.fluture.pruvve.essentials.ImageUploader
import com.fluture.pruvve.essentials.VideoUploader
import com.fluture.pruvve.retrofittcalls.PostsMedia
import com.fluture.pruvve.retrofittcalls.UploadImage
import com.fluture.pruvve.retrofittcalls.UploadResponse
import com.fluture.pruvve.data.api.UserService
import com.google.android.exoplayer2.ExoPlayer.*
import com.google.android.exoplayer2.MediaItem
import dagger.hilt.android.AndroidEntryPoint
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@AndroidEntryPoint
class StoryMaker : AppCompatActivity() {
    private lateinit var binding:ActivityStoryMakerBinding
    private var imageUri: Uri? = null
    private var videoUri: Uri? = null
    private var imageChosen: Boolean = false
    private val videoResponse = "your file is a video"
    private lateinit var username: String
    private val authority = "com.fluture.pruvve.fileprovider"
    private var currentPhotoPath: String? = null
    private var currentVideoPath: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityStoryMakerBinding.inflate(layoutInflater)
        LoginManager.init(this)
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        binding.llCaption.visibility = View.GONE
        binding.etStoryCaption.visibility = View.GONE
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.clStoryMaker)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        openMediaChooser()
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
        binding.imvWriteStory.setOnClickListener {
            binding.llCaption.visibility = View.VISIBLE
            binding.etStoryCaption.visibility = View.VISIBLE
        }
        binding.btnDone.setOnClickListener {
            binding.btnDone.setBackgroundResource(R.drawable.disabled_button)
            binding.btnDone.isEnabled = false
            sortUpload(service)

        }

        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                if (currentPhotoPath != null) {
                    val file = File(currentPhotoPath!!)
                    val photoUri = FileProvider.getUriForFile(this, authority, file)
                    imageUri = photoUri
                    videoUri = null
                    imageChosen = true
                    binding.imvAddAStory.visibility = View.VISIBLE
                    Glide.with(this)
                        .load(imageUri)
                        .apply(RequestOptions().centerCrop())
                        .into(binding.imvAddAStory)
                } else if (currentVideoPath != null) {
                    val file = File(currentVideoPath!!)
                    val videoUriFromDevice = FileProvider.getUriForFile(this, authority, file)
                    videoUri = videoUriFromDevice
                    imageUri = null
                    imageChosen = false
                    val playerView = binding.epAddVideo
                    playerView.visibility = View.VISIBLE
                    val exoPlayer = Builder(this@StoryMaker).build()
                    playerView.player = exoPlayer
                    val mediaItem = MediaItem.fromUri(videoUri ?: Uri.parse(""))
                    exoPlayer.setMediaItem(mediaItem)
                    exoPlayer.prepare()
                    exoPlayer.playWhenReady = true
                    exoPlayer.repeatMode = REPEAT_MODE_ONE

                    playerView.addOnAttachStateChangeListener(object : View.OnAttachStateChangeListener {
                        override fun onViewAttachedToWindow(v: View) {
                            // No action needed
                        }

                        override fun onViewDetachedFromWindow(v: View) {
                            exoPlayer.release()
                        }
                    })
                    Log.d("RetrofitImage", videoResponse)
                }
            }
        }

        if (ContextCompat.checkSelfPermission(this, CAMERA) != PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(this, WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(CAMERA, WRITE_EXTERNAL_STORAGE), 123)
        } else {
            openCamera()
        }
        binding.imvCameraStory.setOnClickListener {
            openCamera()
        }
        binding.imvCameraStory.setOnLongClickListener {
            startVideoRecording()
            true
        }
    }
    private fun sortUpload(service: UserService){
        val errorName = "Unknown error"
        val filenameImage = generateFilename(username)
        val filenameVideo = generateFilename2(username)
        val purpose = "UPLOAD"
        val uploadImage = UploadImage(filenameImage, purpose)
        val uploadVideo = UploadImage(filenameVideo, purpose)

        if (imageChosen){
            service.uploadPicture(uploadImage).enqueue(object : Callback<UploadResponse>{
                override fun onResponse(call: Call<UploadResponse>, response: Response<UploadResponse>) {
                    if(response.isSuccessful){
                        val signedUrl= response.body()?.data.toString()
                        val imageUploader = ImageUploader()
                        imageUploader.uploadImage((uriToByteArray(this@StoryMaker, imageUri!!)!!), signedUrl)
                        val post = PostsMedia(
                            mediaUrl = filenameImage,
                            mediaType = "IMAGE",
                            caption = binding.etStoryCaption.text.toString()
                        )
                        service.postStory(post).enqueue(object: Callback<UploadResponse>{
                            override fun onResponse(call: Call<UploadResponse>, response: Response<UploadResponse>)
                            {
                                if (response.isSuccessful){
                                    Log.d("RetrofitSuccess", response.body()?.message.toString())
                                    Intent(this@StoryMaker, HomePage::class.java).also {
                                        it.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
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
            service.uploadPicture(uploadVideo).enqueue(object : Callback<UploadResponse>{
                override fun onResponse(call: Call<UploadResponse>, response: Response<UploadResponse>){
                    if(response.isSuccessful){
                        val signedUrl= response.body()?.data.toString()
                        val imageUploader = VideoUploader()
                        imageUploader.uploadVideo((uriToByteArray(this@StoryMaker, videoUri!!)!!), signedUrl)
                        val post = PostsMedia(
                            mediaUrl = filenameVideo,
                            mediaType = "VIDEO",
                            caption = binding.etStoryCaption.text.toString()
                        )
                        service.postStory(post).enqueue(object: Callback<UploadResponse>{
                            override fun onResponse(call: Call<UploadResponse>, response: Response<UploadResponse>)
                            {
                                if (response.isSuccessful){
                                    Log.d("RetrofitSuccess", response.body()?.message.toString())
                                    Intent(this@StoryMaker, HomePage::class.java).also {
                                        it.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
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
            type = "*/*"
            putExtra(Intent.EXTRA_MIME_TYPES, arrayOf("image/*", "video/*"))
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
                    binding.imvAddAStory.visibility = View.VISIBLE
                    binding.epAddVideo.visibility = View.GONE
                    binding.imvAddAStory.setImageURI(uri)
                    Log.d("Url", "your uri has been gotten")
                    Log.d("RetrofitImage", "your file is an image")
                } else{
                    videoUri = uri
                    imageUri = null
                    imageChosen = false
                    val playerView = binding.epAddVideo
                    playerView.visibility = View.VISIBLE
                    binding.imvAddAStory.visibility = View.GONE

                    playerView.visibility = View.VISIBLE
                    val exoPlayer = Builder(this@StoryMaker).build()
                    playerView.player = exoPlayer
                    val mediaItem = MediaItem.fromUri(videoUri ?: Uri.parse(""))
                    exoPlayer.setMediaItem(mediaItem)
                    exoPlayer.prepare()
                    exoPlayer.playWhenReady = true
                    exoPlayer.repeatMode = REPEAT_MODE_ONE

                    playerView.addOnAttachStateChangeListener(object : View.OnAttachStateChangeListener {
                        override fun onViewAttachedToWindow(v: View) {
                            // No action needed
                        }

                        override fun onViewDetachedFromWindow(v: View) {
                            exoPlayer.release()
                        }
                    })
                    Log.d("RetrofitImage", videoResponse)
                }
                binding.btnDone.setBackgroundResource(R.drawable.primary_button)
                binding.btnDone.isEnabled = true
            }
        }
    }
    private fun isImage(uri: Uri): Boolean {
        return contentResolver.getType(uri)?.startsWith("image") ?: false
    }
    private fun generateFilename(username: String): String {
        val currentTimeMillis = System.currentTimeMillis()
        val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date(currentTimeMillis))
        return "${username}_${timestamp}_ProfilePic"
    }
    private fun generateFilename2(username: String): String {
        val currentTimeMillis = System.currentTimeMillis()
        val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date(currentTimeMillis))
        return "${username}_${timestamp}_Video"
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

    private val takePicture = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val photoUri = result.data?.data
            binding.imvAddAStory.visibility = View.VISIBLE
            binding.epAddVideo.visibility = View.GONE
            Log.d("Url", "your uri has been gotten")
            Log.d("RetrofitImage", "your file is an image")
            binding.imvAddAStory.setImageURI(photoUri)
        }
    }

    private val takeVideo = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val videoUri = result.data?.data
            val playerView = binding.epAddVideo
            playerView.visibility = View.VISIBLE
            binding.imvAddAStory.visibility = View.GONE
            val exoPlayer = Builder(this@StoryMaker).build()
            playerView.player = exoPlayer
            val mediaItem = MediaItem.fromUri(videoUri ?: Uri.parse(""))
            exoPlayer.setMediaItem(mediaItem)
            exoPlayer.prepare()
            exoPlayer.playWhenReady = true
            exoPlayer.repeatMode = REPEAT_MODE_ONE

            playerView.addOnAttachStateChangeListener(object : View.OnAttachStateChangeListener {
                override fun onViewAttachedToWindow(v: View) {
                    // No action needed
                }

                override fun onViewDetachedFromWindow(v: View) {
                    exoPlayer.release()
                }
            })
            Log.d("RetrofitImage", videoResponse)
        }
        binding.btnDone.setBackgroundResource(R.drawable.primary_button)
        binding.btnDone.isEnabled = true
    }

    private fun openCamera() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        val takeVideoIntent = Intent(MediaStore.ACTION_VIDEO_CAPTURE)
        if (takePictureIntent.resolveActivity(packageManager) != null || takeVideoIntent.resolveActivity(packageManager) != null) {
            val photoFile: File? = try {
                createImageFile()
            } catch (ex: IOException) {
                null
            }
            if (photoFile != null) {
                val photoURI: Uri = FileProvider.getUriForFile(this, authority, photoFile)
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                takePicture.launch(takePictureIntent)
            }
        }
    }

    @Throws(IOException::class)
    private fun createImageFile(): File {
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val storageDir: File? = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            "JPEG_${timeStamp}_",
            ".jpg",
            storageDir
        ).apply {
            currentPhotoPath = absolutePath
        }
    }

    private fun startVideoRecording() {
        val takeVideoIntent = Intent(MediaStore.ACTION_VIDEO_CAPTURE)
        if (takeVideoIntent.resolveActivity(packageManager) != null) {
            val videoFile: File? = try {
                createVideoFile()
            } catch (ex: IOException) {
                null
            }
            if (videoFile != null) {
                val videoURI: Uri = FileProvider.getUriForFile(this, authority, videoFile)
                takeVideoIntent.putExtra(MediaStore.EXTRA_OUTPUT, videoURI)
                takeVideo.launch(takeVideoIntent)
            }
        }
    }

    @Throws(IOException::class)
    private fun createVideoFile(): File {
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val storageDir: File? = getExternalFilesDir(Environment.DIRECTORY_MOVIES)
        return File.createTempFile(
            "MP4_${timeStamp}_",
            ".mp4",
            storageDir
        ).apply {
            currentVideoPath = absolutePath
        }
    }

}