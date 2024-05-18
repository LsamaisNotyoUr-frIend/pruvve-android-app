package com.fluture.pruvve

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.GridLayoutManager
import com.fluture.pruvve.adapters.VideoPageItems
import com.fluture.pruvve.adapters.VideosPageAdapter1
import com.fluture.pruvve.auth.AuthInterceptor
import com.fluture.pruvve.auth.LoginManager
import com.fluture.pruvve.databinding.ActivityCoachHomePageBinding
import com.fluture.pruvve.retrofittcalls.UserService
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

class CoachHomePage : AppCompatActivity() {
    private lateinit var binding: ActivityCoachHomePageBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityCoachHomePageBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        LoginManager.init(this)
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

        val myUrl = "https://i.pinimg.com/236x/4e/80/50/4e80508b0f22dfc42ce98bb8d0acb563.jpg"
        val myUrl2 = "https://i.pinimg.com/236x/0e/88/20/0e8820df856a51cdcb7a791396846421.jpg"
        val thisURL = "https://i.pinimg.com/236x/c8/71/8e/c8718e9e41758a502a709765792b7de3.jpg"
        val recycler1 = binding.rvCoachFeeds1

        val videos1 = mutableListOf(
            VideoPageItems(myUrl),
            VideoPageItems(myUrl2),
            VideoPageItems(thisURL),
            VideoPageItems(myUrl),
            VideoPageItems(myUrl2),
            VideoPageItems(myUrl),
            VideoPageItems(myUrl2),
            VideoPageItems(thisURL),
            VideoPageItems(myUrl2),
            VideoPageItems(thisURL),
            VideoPageItems(myUrl),
            VideoPageItems(myUrl2),
            VideoPageItems(myUrl),
            VideoPageItems(thisURL),
            VideoPageItems(myUrl),
            VideoPageItems(myUrl),
            VideoPageItems(myUrl2),
            VideoPageItems(myUrl)
        )
        val adapter1 = VideosPageAdapter1(videos1)
        val layoutManager = GridLayoutManager(this@CoachHomePage, 2, GridLayoutManager.VERTICAL, false)
        recycler1.adapter = adapter1
        recycler1.layoutManager = layoutManager

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
            supportFragmentManager.beginTransaction().apply {
                replace(R.id.CoachHomeScreenFragment, TeamsFragment())
                binding.videoPageButton.setImageResource(R.drawable.video_icon)
                binding.bookPitchButton.setImageResource(R.drawable.book_pitch_icon)
                binding.profileButton.setImageResource(R.drawable.clicked_profile_button)
                commit()
            }
        }
    }
}