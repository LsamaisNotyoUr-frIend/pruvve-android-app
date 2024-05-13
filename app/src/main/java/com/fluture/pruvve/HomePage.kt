package com.fluture.pruvve

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.LinearLayoutManager
import com.fluture.pruvve.adapters.GetUserResponse
import com.fluture.pruvve.adapters.Stories
import com.fluture.pruvve.adapters.StoryAdapter
import com.fluture.pruvve.auth.AuthInterceptor
import com.fluture.pruvve.auth.LoginManager
import com.fluture.pruvve.databinding.ActivityHomePageBinding
import com.fluture.pruvve.retrofittcalls.InputStreamRequestBody
import com.fluture.pruvve.retrofittcalls.UploadImage
import com.fluture.pruvve.retrofittcalls.UploadResponse
import com.fluture.pruvve.retrofittcalls.UserService
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

class HomePage : AppCompatActivity() {
    private lateinit var binding: ActivityHomePageBinding
    private var mediaUri: Uri? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityHomePageBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        LoginManager.init(this)
        setContentView(binding.root)
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

        val myUrl = "https://i.pinimg.com/236x/63/cc/06/63cc06edc7c8222eaee125beb92bfc99.jpg"
        val myUrl2 = "https://i.pinimg.com/236x/e5/97/79/e59779258a86991a933e45143bf3db4c.jpg"

        binding.wvfeedsHp.loadUrl(myUrl)
        binding.wvNewsCover.loadUrl(myUrl2)
        binding.wvFeedsCoverPfp.loadUrl(myUrl2)
        binding.wvNewsCoverPfp.loadUrl(myUrl)
        binding.wvProfilePlace.loadUrl(myUrl)

        binding.homePageButton.setOnClickListener {
            supportFragmentManager.beginTransaction().apply {
                supportFragmentManager.fragments.forEach { remove(it) }
                binding.homePageButton.setImageResource(R.drawable.clicked_home_button)
                binding.videoPageButton.setImageResource(R.drawable.video_icon)
                binding.bookPitchButton.setImageResource(R.drawable.book_pitch_icon)
                binding.profileButton.setImageResource(R.drawable.profile_icon)
                commit()
            }
        }
        binding.videoPageButton.setOnClickListener {
            supportFragmentManager.beginTransaction().apply {
                replace(R.id.homeScreenFragment, VideoScreenFragments())
                binding.homePageButton.setImageResource(R.drawable.home_button)
                binding.videoPageButton.setImageResource(R.drawable.clicked_video_icon)
                binding.bookPitchButton.setImageResource(R.drawable.book_pitch_icon)
                binding.profileButton.setImageResource(R.drawable.profile_icon)
                commit()
            }        }

        binding.bookPitchButton.setOnClickListener {
            supportFragmentManager.beginTransaction().apply {
                replace(R.id.homeScreenFragment, BookPitchFragment())
                binding.homePageButton.setImageResource(R.drawable.home_button)
                binding.videoPageButton.setImageResource(R.drawable.video_icon)
                binding.bookPitchButton.setImageResource(R.drawable.clicked_book_pitch)
                binding.profileButton.setImageResource(R.drawable.profile_icon)
                commit()
            }
        }
        binding.profileButton.setOnClickListener {
            supportFragmentManager.beginTransaction().apply {
                replace(R.id.homeScreenFragment, ProfileFragment())
                binding.homePageButton.setImageResource(R.drawable.home_button)
                binding.videoPageButton.setImageResource(R.drawable.video_icon)
                binding.bookPitchButton.setImageResource(R.drawable.book_pitch_icon)
                binding.profileButton.setImageResource(R.drawable.clicked_profile_button)
                commit()
            }
        }
        val url = "https://i.pinimg.com/236x/83/20/37/8320378b76c73172661e40e2e190d80b.jpg"
        val stories = mutableListOf(
            Stories("Micheal", url = url),
            Stories("Irvin", url = "https://i.pinimg.com/236x/a7/b5/71/a7b57157f7e23ed35c73153b61efa490.jpg"),
            Stories("Mahmud", url = "https://i.pinimg.com/236x/e4/be/bb/e4bebbaf7a4b7efa305e198720248e9b.jpg")
        )
        binding.llAddStory.setOnClickListener{
            openMediaChooser()
            val uploader = UploadImage(
                fileName = "Story1",
                purpose = "UPLOAD"
            )
            val uploader2 = UploadImage(
                fileName = "Story1",
                purpose = "DOWNLOAD"
            )
            service.uploadPicture(uploader).enqueue(object: Callback<UploadResponse>{
                override fun onResponse(
                    call: Call<UploadResponse>,
                    response: Response<UploadResponse>
                ) {
                    if (response.isSuccessful){
                        val signedUrl = response.body()?.data.toString()
                        uploadFile(signedUrl, mediaUri!!)
                        service.uploadPicture(uploader2).enqueue(object : Callback<UploadResponse>{
                            override fun onResponse(
                                call: Call<UploadResponse>,
                                response: Response<UploadResponse>
                            ) {
                                if(response.isSuccessful){
                                    val uploadUrl = response.body()?.data.toString()
                                    stories.add(Stories("mikererl", uploadUrl))
                                }
                            }

                            override fun onFailure(call: Call<UploadResponse>, t: Throwable) {
                                TODO("Not yet implemented")
                            }
                        })
                    }
                }

                override fun onFailure(call: Call<UploadResponse>, t: Throwable) {
                    TODO("Not yet implemented")
                }
            })
        }

        binding.tvMoreNews.setOnClickListener {
            supportFragmentManager.beginTransaction().apply {
                replace(R.id.homeScreenFragment, NewsFragment())
                commit()
            }
        }

        binding.tvMoreFeeds.setOnClickListener {
            Intent(this, MorePage::class.java).also{
                startActivity(it)
            }
        }

        val storiesRecycler = binding.rvStories
        storiesRecycler.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        val adapter = StoryAdapter(stories)
        storiesRecycler.adapter = adapter
        val usernameDisplay = findViewById<TextView>(R.id.tvUsername)

        service.getUserCredentials().enqueue(object: Callback<GetUserResponse> {
            @SuppressLint("SetTextI18n")
            override fun onResponse(call: Call<GetUserResponse>, response: Response<GetUserResponse>) {
                if(response.isSuccessful){
                    val informationSource = response.body()?.data
                    Log.d("RetrofitSuccess", informationSource?.profilePictureUrl.toString())
                    Log.d("RetrofitSuccess", response.body().toString())
                    usernameDisplay.text = "\t\t${informationSource?.username.toString()}"
                }else{
                    Log.e("RetrofitError", "could not load in username and profile pic ${response.errorBody().toString()}")
                    Toast.makeText(HomePage() ,"Loading error", Toast.LENGTH_SHORT).show()
                }
            }
            override fun onFailure(call: Call<GetUserResponse>, t: Throwable) {
                Log.e("RetrofitFailure", "could not reach the server ${t.message.toString()}")
            }
        })
    }
    private fun openMediaChooser() {
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
                mediaUri = uri
            }
        }
    }
    private fun uploadFile(url: String, fileUri: Uri) {
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
                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {
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