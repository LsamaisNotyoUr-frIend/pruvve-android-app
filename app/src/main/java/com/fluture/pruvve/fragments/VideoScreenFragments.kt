package com.fluture.pruvve.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.fluture.pruvve.R
import com.fluture.pruvve.adapters.VideoPageItems
import com.fluture.pruvve.adapters.VideosPageAdapter1
import com.fluture.pruvve.auth.AuthInterceptor
import com.fluture.pruvve.auth.LoginManager
import com.fluture.pruvve.databinding.FragmentVideoScreenFragmentsBinding
import com.fluture.pruvve.retrofittcalls.GetAllPosts
import com.fluture.pruvve.retrofittcalls.GetFeedsMedia
import com.fluture.pruvve.retrofittcalls.UploadImage
import com.fluture.pruvve.retrofittcalls.UploadResponse
import com.fluture.pruvve.retrofittcalls.UserService
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

class VideoScreenFragments : Fragment(R.layout.fragment_video_screen_fragments) {
    private lateinit var binding: FragmentVideoScreenFragmentsBinding
    private lateinit var adapter: VideosPageAdapter1
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding = FragmentVideoScreenFragmentsBinding.bind(view)
        LoginManager.init(requireContext())
        super.onViewCreated(view, savedInstanceState)

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
        val myUrl = "https://i.pinimg.com/236x/aa/4f/55/aa4f55ccb2674bd8ca76053f2b5bab60.jpg"
        val myUrl2 = "https://i.pinimg.com/236x/cc/0c/af/cc0caf4f332b9925d89f0a5d6f8f87e0.jpg"

        val videos1Recycler  = binding.rvVideos1
        binding.wvFunny.loadUrl(myUrl)
        binding.wvGoals.loadUrl(myUrl)
        binding.wvSkills.loadUrl(myUrl)
        Glide.with(requireContext())
            .load(myUrl)
            .apply(RequestOptions().centerCrop())
            .into(binding.wvTopVideo)

        val videos1 = mutableListOf(
            VideoPageItems(myUrl), VideoPageItems(myUrl2), VideoPageItems(myUrl),
            VideoPageItems(myUrl), VideoPageItems(myUrl2), VideoPageItems(myUrl),
            VideoPageItems(myUrl2), VideoPageItems(myUrl), VideoPageItems(myUrl2),
            VideoPageItems(myUrl2), VideoPageItems(myUrl), VideoPageItems(myUrl2),
            VideoPageItems(myUrl), VideoPageItems(myUrl2), VideoPageItems(myUrl),
            VideoPageItems(myUrl), VideoPageItems(myUrl2), VideoPageItems(myUrl)
        )
        getVideos(service)
        adapter= VideosPageAdapter1(videos1)
        val layoutManager = GridLayoutManager(requireContext(), 2, GridLayoutManager.VERTICAL, false)
        videos1Recycler.adapter = adapter
        videos1Recycler.layoutManager = layoutManager
    }

    private fun getVideos(service: UserService){
        val getPostMedia = GetFeedsMedia(1, 5)
        service.getPosts(getPostMedia).enqueue(object : Callback<GetAllPosts> {
            @SuppressLint("NotifyDataSetChanged")
            override fun onResponse(call: Call<GetAllPosts>, response: Response<GetAllPosts>) {
                if(response.isSuccessful){
                    val list = response.body()?.data?.list
                    val listToReturn = mutableListOf<VideoPageItems>()
                    if (list!= null){
                        for (post in list){
                            val url = post.mediaUrl
                            val downloadImage2 = UploadImage(
                                fileName = url,
                                purpose = "DOWNLOAD"
                            )
                            service.uploadPicture(downloadImage2).enqueue(object: Callback<UploadResponse> {
                                override fun onResponse(call: Call<UploadResponse>, response: Response<UploadResponse>) {
                                    if (response.isSuccessful){
                                        listToReturn.add(VideoPageItems(response.body()?.data.toString()))
                                        adapter = VideosPageAdapter1(listToReturn)
                                        adapter.notifyDataSetChanged()
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
    }
}