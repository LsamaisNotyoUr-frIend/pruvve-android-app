package com.fluture.pruvve

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.fluture.pruvve.adapters.VideoPageItems
import com.fluture.pruvve.adapters.VideosPageAdapter1
import com.fluture.pruvve.auth.AuthInterceptor
import com.fluture.pruvve.auth.LoginManager
import com.fluture.pruvve.databinding.ActivityCoachHomePageBinding
import com.fluture.pruvve.fragments.BookPitchFragment
import com.fluture.pruvve.fragments.TeamsFragment
import com.fluture.pruvve.retrofittcalls.GetAllPosts
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

class CoachHomePage : AppCompatActivity() {
    private lateinit var binding: ActivityCoachHomePageBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityCoachHomePageBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        LoginManager.init(this)
        val id: Int = intent.getIntExtra("ProfileId", 3)
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

        val teamId = intent.getIntExtra("Extra_teamId", 5)

        val myUrl = "https://i.pinimg.com/236x/4e/80/50/4e80508b0f22dfc42ce98bb8d0acb563.jpg"
        val recycler1 = binding.rvCoachFeeds1

        val video = mutableListOf(VideoPageItems(myUrl))

        val getPostMedia = GetPostsMedia(1, 20, id)
        service.getPosts(getPostMedia).enqueue(object : Callback<GetAllPosts>{
            override fun onResponse(call: Call<GetAllPosts>, response: Response<GetAllPosts>) {
                if(response.isSuccessful){
                    val list = response.body()?.data?.list
                    if (list!= null){
                        for (post in list){
                            val url = post.mediaUrl
                            val downloadImage2 = UploadImage(
                                fileName = url,
                                purpose = "DOWNLOAD"
                            )
                            service.uploadPicture(downloadImage2).enqueue(object: Callback<UploadResponse>{
                                override fun onResponse(
                                    call: Call<UploadResponse>,
                                    response: Response<UploadResponse>
                                ) {
                                    if (response.isSuccessful){
                                        video.add(VideoPageItems(response.body()?.data.toString()))
                                        if (video.size == list.size){
                                            val adapter1 = VideosPageAdapter1(video)
                                            val layoutManager = GridLayoutManager(this@CoachHomePage, 2, GridLayoutManager.VERTICAL, false)
                                            recycler1.adapter = adapter1
                                            recycler1.layoutManager = layoutManager
                                        }
                                    }else{
                                        Log.e("RetrofitError", "Error taking data from the server")
                                    }
                                }
                                override fun onFailure(call: Call<UploadResponse>, t: Throwable) {
                                    Log.e("RetrofitFailure", "Couldn't reach the server")
                                }
                            })
                        }
                    }else{
                        Log.e("RetrofitError", "Your list is empty")
                    }
                }else{
                    Log.e("RetrofitError", "Error taking data from the server")
                }
            }
            override fun onFailure(call: Call<GetAllPosts>, t: Throwable) {
                Log.e("RetrofitFailure", "Couldn't reach the server")
            }
        })
        binding.videoPageButton.setImageResource(R.drawable.clicked_video_icon)
        binding.bookPitchButton.setImageResource(R.drawable.book_pitch_icon)
        binding.profileButton.setImageResource(R.drawable.profile_icon)

        supportFragmentManager.beginTransaction().apply {
            supportFragmentManager.fragments.forEach { remove(it) }
            binding.videoPageButton.setImageResource(R.drawable.clicked_video_icon)
            binding.bookPitchButton.setImageResource(R.drawable.book_pitch_icon)
            binding.profileButton.setImageResource(R.drawable.profile_icon)
            commit()
        }
        binding.videoPageButton.setOnClickListener {
            supportFragmentManager.beginTransaction().apply {
                supportFragmentManager.fragments.forEach { remove(it) }
                binding.videoPageButton.setImageResource(R.drawable.clicked_video_icon)
                binding.bookPitchButton.setImageResource(R.drawable.book_pitch_icon)
                binding.profileButton.setImageResource(R.drawable.profile_icon)
                commit()
            }
        }
        binding.bookPitchButton.setOnClickListener {
            supportFragmentManager.beginTransaction().apply {
                replace(R.id.CoachHomeScreenFragment, BookPitchFragment())
                binding.videoPageButton.setImageResource(R.drawable.video_icon)
                binding.bookPitchButton.setImageResource(R.drawable.clicked_book_pitch)
                binding.profileButton.setImageResource(R.drawable.profile_icon)
                commit()
            }
        }
        binding.profileButton.setOnClickListener {
            val bundle = Bundle().apply {
                putInt("teamId", teamId)
            }

            val teamsFragment = TeamsFragment().apply {
                arguments = bundle
            }
            supportFragmentManager.beginTransaction().apply {
                replace(R.id.CoachHomeScreenFragment, teamsFragment)
                binding.videoPageButton.setImageResource(R.drawable.video_icon)
                binding.bookPitchButton.setImageResource(R.drawable.book_pitch_icon)
                binding.profileButton.setImageResource(R.drawable.clicked_profile_button)
                commit()
            }
        }
    }
}