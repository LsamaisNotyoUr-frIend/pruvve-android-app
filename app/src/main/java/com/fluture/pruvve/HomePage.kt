package com.fluture.pruvve

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.fluture.pruvve.adapters.Stories
import com.fluture.pruvve.adapters.StoryAdapter
import com.fluture.pruvve.auth.AuthInterceptor
import com.fluture.pruvve.auth.LoginManager
import com.fluture.pruvve.databinding.ActivityHomePageBinding
import com.fluture.pruvve.retrofittcalls.GetPost
import com.fluture.pruvve.retrofittcalls.GetPostsMedia
import com.fluture.pruvve.retrofittcalls.UploadImage
import com.fluture.pruvve.retrofittcalls.UploadResponse
import com.fluture.pruvve.retrofittcalls.UserService
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

class HomePage : AppCompatActivity() {
    private lateinit var binding: ActivityHomePageBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityHomePageBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        LoginManager.init(this)
        setContentView(binding.root)
        val token = LoginManager.getToken()
        Log.d("RetrofitToken", token.toString())
        val id: Int = intent.getIntExtra("ProfileId", 3)
        val username = intent.getStringExtra("profileUsername") ?: "AdminSomething"
        val profilePic = intent.getStringExtra("profileUrl") ?: "https://i.pinimg.com/236x/e5/97/79/e59779258a86991a933e45143bf3db4c.jpg"
        val httpClient = OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor(token.toString()))
            .build()

        val service = Retrofit.Builder()
            .baseUrl("https://pruvve-backend-9a89de78d2a1.herokuapp.com/api/")
            .client(httpClient)
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
            .create(UserService::class.java)

        val downloadImage = UploadImage(
            fileName = profilePic,
            "DOWNLOAD"
        )
        service.uploadPicture(downloadImage).enqueue(object :Callback<UploadResponse>{
            override fun onResponse(call: Call<UploadResponse>, response: Response<UploadResponse>
            ) {
                val url = response.body()?.data
                if (response.isSuccessful){
                    Glide.with(this@HomePage)
                        .load(url)
                        .apply(RequestOptions.circleCropTransform())
                        .into(binding.imvProfilePlace)
                }else{
                    Log.e("RetrofitError","an error occurred ${response.errorBody().toString()}")
                }
            }
            override fun onFailure(call: Call<UploadResponse>, t: Throwable) {
                Log.e("RetrofitError","an error occurred ${t.message.toString()}")
            }
        })

        val myUrl2 = "https://i.pinimg.com/236x/e5/97/79/e59779258a86991a933e45143bf3db4c.jpg"
        val stories = mutableListOf(
            Stories(username, url = myUrl2),
        )
        Log.e("RetrofitId", "your id is $id")
        getStories(service, id, stories)

        val myUrl = "https://i.pinimg.com/236x/63/cc/06/63cc06edc7c8222eaee125beb92bfc99.jpg"

        Glide.with(this)
            .load(myUrl)
            .apply(RequestOptions.circleCropTransform())
            .into(binding.imvNewsCoverPfp)

        Glide.with(this)
            .load(myUrl)
            .apply(RequestOptions.circleCropTransform())
            .into(binding.imvFeedsCoverPfp)

        Glide.with(this)
            .load(myUrl2)
            .apply(RequestOptions().centerCrop())
            .into(binding.imvfeedsHp)

        Glide.with(this)
            .load(myUrl2)
            .apply(RequestOptions().centerCrop())
            .into(binding.imvNewsCover)

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
        binding.llAddStory.setOnClickListener{
            Intent(this@HomePage, StoryMaker::class.java).also {
                startActivity(it)
                finish()
            }
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
    }
    private fun getStories(service: UserService,userid: Int, stories: MutableList<Stories>){
        val postMedia = GetPostsMedia(
            page = 1,
            size = 10,
            userId = userid
        )
        val storiesRecycler = binding.rvStories
        service.getStories(postMedia).enqueue(object: Callback<GetPost>{
            override fun onResponse(call: Call<GetPost>, response: Response<GetPost>) {
                Log.e("RetrofitService", "Service starting")
                if (response.isSuccessful) {
                    val postList = response.body()?.data?.list
                    if (postList != null) {
                        for (postData in postList) {
                            val user = postData.user
                            val downloadImage = UploadImage(
                                fileName = postData.mediaUrl,
                                purpose = "DOWNLOAD"
                            )
                            service.uploadPicture(downloadImage).enqueue(object : Callback<UploadResponse> {
                                override fun onResponse(call: Call<UploadResponse>, response: Response<UploadResponse>) {
                                    if (response.isSuccessful) {
                                        val signedUrl2 = response.body()?.data.toString()
                                        val postToAdd = Stories(user.username, signedUrl2)
                                        stories.add(postToAdd)
                                        if (stories.size == postList.size) {
                                            Log.d("RetrofitSuccess", "the loop is done")
                                            val adapter = StoryAdapter(stories)
                                            storiesRecycler.adapter = adapter
                                            storiesRecycler.layoutManager = LinearLayoutManager(this@HomePage, LinearLayoutManager.HORIZONTAL, false)
                                        }else{
                                            Log.d("RetrofitSuccess", "the loop is done")
                                        }
                                    } else {
                                        Log.e("RetrofitError", "An error has occurred ${response.errorBody().toString()}")
                                    }
                                }

                                override fun onFailure(call: Call<UploadResponse>, t: Throwable) {
                                    Log.e("RetrofitFailure", "Failed to reach the server ${response.errorBody().toString()}")
                                }
                            })
                        }
                    } else {
                        Log.e("RetrofitLists", "Your list is null")
                    }
                } else {
                    Log.e("RetrofitError", "An error occurred ${response.errorBody().toString()}")
                }

            }

            override fun onFailure(call: Call<GetPost>, t: Throwable) {
                Log.e("RetrofitFailure", "Failed to reach the server ${t.message.toString()}")
            }
        })
    }
}