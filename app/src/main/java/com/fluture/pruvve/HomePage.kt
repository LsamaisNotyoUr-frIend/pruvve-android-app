package com.fluture.pruvve

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.fluture.pruvve.databinding.ActivityHomePageBinding
import de.hdodenhof.circleimageview.CircleImageView
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

        binding.svHomePage.visibility = View.VISIBLE

        binding.homePageButton.setOnClickListener {
            supportFragmentManager.beginTransaction().apply {
                supportFragmentManager.fragments.forEach { remove(it) }
                binding.homePageButton.setImageResource(R.drawable.clicked_home_button)
                binding.videoPageButton.setImageResource(R.drawable.video_icon)
                binding.bookPitchButton.setImageResource(R.drawable.book_pitch_icon)
                commit()
            }
        }
        binding.videoPageButton.setOnClickListener {
            supportFragmentManager.beginTransaction().apply {
                replace(R.id.homeScreenFragment, VideoScreenFragments())
                binding.homePageButton.setImageResource(R.drawable.home_button)
                binding.videoPageButton.setImageResource(R.drawable.clicked_video_icon)
                binding.bookPitchButton.setImageResource(R.drawable.book_pitch_icon)
                commit()
            }        }

        binding.bookPitchButton.setOnClickListener {
            supportFragmentManager.beginTransaction().apply {
                replace(R.id.homeScreenFragment, BookPitchFragment())
                binding.homePageButton.setImageResource(R.drawable.home_button)
                binding.videoPageButton.setImageResource(R.drawable.video_icon)
                binding.bookPitchButton.setImageResource(R.drawable.clicked_book_pitch)
                commit()
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

        val url = "https://archive.org/details/soccer_202403"
        val storiesRecycler = binding.rvStories
        storiesRecycler.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        val stories = mutableListOf(
            Stories("john legend", url = url),
            Stories("mark Ronson", url = url),
            Stories("post Malone", url = url),
            Stories("mike postner", url = url)
        )
        val adapter = StoryAdapter(stories)
        storiesRecycler.adapter = adapter
        val profilePic = findViewById<CircleImageView>(R.id.imvprofilePlace)
        val usernameDisplay = findViewById<TextView>(R.id.tvUsername)

        service.getUserCredentials().enqueue(object: Callback<GetUserResponse> {
            @SuppressLint("SetTextI18n")
            override fun onResponse(call: Call<GetUserResponse>, response: Response<GetUserResponse>) {
                if(response.isSuccessful){
                    val informationSource = response.body()?.data
                    val profilePicUrl = informationSource?.profilePicUrl.toString()
                    Log.d("RetrofitSuccess", informationSource?.profilePicUrl.toString())
                    Log.d("RetrofitSuccess", response.body().toString())
                    Glide.with(this@HomePage)
                        .load(profilePicUrl)
                        .into(profilePic)
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
}