package com.fluture.pruvve

import android.annotation.SuppressLint
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.Guideline
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.fluture.pruvve.adapters.CommentAdapter
import com.fluture.pruvve.adapters.Comments
import com.fluture.pruvve.adapters.LikeAdapter
import com.fluture.pruvve.adapters.Likes
import com.fluture.pruvve.auth.AuthInterceptor
import com.fluture.pruvve.auth.LoginManager
import com.fluture.pruvve.databinding.ActivityShowVideosBinding
import com.fluture.pruvve.retrofittcalls.FollowsReply
import com.fluture.pruvve.retrofittcalls.MakeComments
import com.fluture.pruvve.retrofittcalls.RequestObjects
import com.fluture.pruvve.retrofittcalls.ServerComments
import com.fluture.pruvve.retrofittcalls.ServerLikes
import com.fluture.pruvve.retrofittcalls.UploadImage
import com.fluture.pruvve.retrofittcalls.UploadResponse
import com.fluture.pruvve.retrofittcalls.UserService
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

class ShowVideos : AppCompatActivity() {
    private lateinit var binding: ActivityShowVideosBinding
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityShowVideosBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        LoginManager.init(this)
        setContentView(binding.root)
        var commentSelected = true
        var isLiked = intent.getBooleanExtra("isLiked", false)
        val videoUrl = intent.getStringExtra("videoUrl")!!
        val videoId = intent.getIntExtra("VideoId", 3)
        val textView = binding.tvCommentOrLikes
        Glide.with(this@ShowVideos)
            .load(videoUrl)
            .into(binding.imvShowChosenVideo)
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

        binding.imvCommentOnVideo.setOnClickListener {
            openCommentSection(true, binding.guideline339, commentSelected, service, videoId)
        }
        binding.imvExitVideoCommentSection.setOnClickListener {
            openCommentSection(false, binding.guideline339,commentSelected, service, videoId)
        }
        textView.setOnClickListener {
            commentSelected = !commentSelected
            if (commentSelected){
                textView.text = "Comments"
            }else{
                textView.text = "Likes"
            }
            openCommentSection(true, binding.guideline339, commentSelected, service, videoId)
        }
        val likesButton = binding.imvLikeVideo
        likesButton.setOnClickListener {
            isLiked = !isLiked
            sortLikes(service, likesButton, isLiked, videoId)
        }
    }
    private fun getComments(service: UserService, context: Context, recyclerView: RecyclerView, videoId: Int) {
        val request = RequestObjects(1, 5)
        val comments = mutableListOf<Comments>()
        service.getComments(videoId, request).enqueue(object : Callback<ServerComments> {
            override fun onResponse(call: Call<ServerComments>, response: Response<ServerComments>) {
                if (response.isSuccessful) {
                    comments.clear()
                    val commentLists = response.body()?.data?.list
                    if (commentLists != null) {
                        for (commentItems in commentLists) {
                            val user = commentItems.user
                            val downloadImage = UploadImage(
                                fileName = user.profilePictureUrl,
                                purpose = "DOWNLOAD"
                            )
                            service.uploadPicture(downloadImage).enqueue(object :
                                Callback<UploadResponse> {
                                @SuppressLint("NotifyDataSetChanged")
                                override fun onResponse(
                                    call: Call<UploadResponse>,
                                    response: Response<UploadResponse>
                                ) {
                                    if (response.isSuccessful) {
                                        val signedUrl = response.body()?.data.toString()
                                        val commentToAdd =
                                            Comments(user.username, signedUrl, commentItems.comment)
                                        comments.add(commentToAdd)
                                        if (comments.size == commentLists.size) {
                                            Log.d("RetrofitSuccess", "the loop is done")
                                            val commentAdapter = CommentAdapter(comments)
                                            recyclerView.adapter = commentAdapter
                                            recyclerView.layoutManager =
                                                LinearLayoutManager(context)
                                            commentAdapter.notifyDataSetChanged()
                                        } else {
                                            Log.e("RetrofitError", "problem finishing the loop")
                                        }
                                    } else {
                                        Log.e(
                                            "RetrofitError",
                                            "An error has occurred ${
                                                response.errorBody().toString()
                                            }"
                                        )
                                    }
                                }

                                override fun onFailure(call: Call<UploadResponse>, t: Throwable) {
                                    Log.e(
                                        "RetrofitFailure",
                                        "Failed to reach the server ${t.message.toString()}"
                                    )
                                }
                            })
                        }
                    } else {
                        Log.e("RetrofitLists", "Your list is null")
                    }
                }
            }

            override fun onFailure(call: Call<ServerComments>, t: Throwable) {
                Log.e("RetrofitFailure", "Failed to reach the server ${t.message.toString()}")
            }
        })
    }

    private fun openCommentSection(isCommentClicked: Boolean, guideline: Guideline,commentSelected: Boolean, service: UserService, videoId: Int
    ) {
        val params = guideline.layoutParams as ConstraintLayout.LayoutParams
        if (isCommentClicked) {
            params.guidePercent = 0.45f
            guideline.layoutParams = params
            binding.llVideoOptions.visibility = View.GONE
            getComments(service, this@ShowVideos, binding.rvChosenCommentsOrLikes, videoId)
            binding.imvVideoCommentSend.setOnClickListener {
                makeComments(service, binding.etVideoCommentText.text.toString(), videoId)
                binding.etVideoCommentText.text.clear()
                getComments(service, this@ShowVideos,binding.rvChosenCommentsOrLikes, videoId)
            }
            if (commentSelected) {
                getComments(service, this@ShowVideos, binding.rvChosenCommentsOrLikes, videoId)
            } else {
                getLikes(service, this@ShowVideos, binding.rvChosenCommentsOrLikes, videoId)
            }

        } else {
            params.guidePercent = 1.0f
            binding.llVideoOptions.visibility = View.VISIBLE
            guideline.layoutParams = params
        }
    }
    private fun makeComments(service: UserService, textToUpload: String, videoId: Int) {
        val makeComments = MakeComments(
            textToUpload
        )
        service.makeComment(videoId, makeComments).enqueue(object : Callback<FollowsReply> {
            override fun onResponse(call: Call<FollowsReply>, response: Response<FollowsReply>) {
                if (response.isSuccessful) {
                    Log.d(
                        "RetrofitSuccess",
                        "Your comment has been uploaded successfully ${response.body()?.message.toString()}"
                    )
                } else {
                    Log.e("RetrofitError", "Problem uploading your comment")
                }
            }

            override fun onFailure(call: Call<FollowsReply>, t: Throwable) {
                Log.e("RetrofitFailure", "Couldn't reach the server ${t.message.toString()}")
            }
        })
    }

    private fun getLikes(service: UserService, context: Context, recyclerView: RecyclerView, videoId: Int){
        val request = RequestObjects(1, 20)
        val likes = mutableListOf<Likes>()
        service.getLikes(videoId, request).enqueue(object : Callback<ServerLikes>{
            override fun onResponse(call: Call<ServerLikes>, response: Response<ServerLikes>) {
                if (response.isSuccessful){
                    likes.clear()
                    val likesLists = response.body()?.data?.list
                    if (likesLists != null){
                        for (likedItems in likesLists){
                            val user = likedItems.user
                            val downloadImage = UploadImage(
                                fileName = user.profilePictureUrl,
                                purpose = "DOWNLOAD")
                            service.uploadPicture(downloadImage).enqueue(object : Callback<UploadResponse>{
                                @SuppressLint("NotifyDataSetChanged")
                                override fun onResponse(call: Call<UploadResponse>, response: Response<UploadResponse>) {
                                    if (response.isSuccessful){
                                        val signedUrl = response.body()?.data.toString()
                                        val likesToAdd = Likes(user.username, signedUrl)
                                        likes.add(likesToAdd)
                                        if (likes.size == likesLists.size) {
                                            Log.d("RetrofitSuccess", "the loop is done")
                                            val likesAdapter = LikeAdapter(likes)
                                            recyclerView.adapter = likesAdapter
                                            recyclerView.layoutManager = LinearLayoutManager(context)
                                            likesAdapter.notifyDataSetChanged()
                                        }else{
                                            Log.e("RetrofitError", "problem finishing the loop")
                                        }
                                    }else{
                                        Log.e("RetrofitError", "An error has occurred ${response.errorBody().toString()}")
                                    }
                                }
                                override fun onFailure(call: Call<UploadResponse>, t: Throwable) {
                                    Log.e("RetrofitFailure", "Failed to reach the server ${t.message.toString()}")
                                }
                            })
                        }
                    }else{
                        Log.e("RetrofitLists", "Your list is null")
                    }
                }
            }
            override fun onFailure(call: Call<ServerLikes>, t: Throwable) {
                Log.e("RetrofitFailure", "Failed to reach the server ${t.message.toString()}")
            }
        })
    }

    private fun sortLikes(service: UserService, imageView: ImageView, isLiked: Boolean, videoId: Int){
        if (isLiked){
            service.likePost(videoId).enqueue(object : Callback<FollowsReply>{
                override fun onResponse(call: Call<FollowsReply>, response: Response<FollowsReply>) {
                    if (response.isSuccessful){
                        Log.d("RetrofitLikes", "your post has been liked ${response.body()?.message.toString()}")
                        imageView.setImageResource(R.drawable.like_clicked)
                    }else{
                        Log.e("RetrofitLikesError", "problem liking post ${response.errorBody().toString()}")
                    }
                }
                override fun onFailure(call: Call<FollowsReply>, t: Throwable) {
                    Log.e("RetrofitFailure", "Couldn't reach the server ${t.message.toString()}")
                }
            })
        }else{
            service.unLikePost(videoId).enqueue(object : Callback<FollowsReply>{
                override fun onResponse(call: Call<FollowsReply>, response: Response<FollowsReply>) {
                    if (response.isSuccessful){
                        Log.d("RetrofitLikes", "your post has been ${response.body()?.message.toString()}")
                        imageView.setImageResource(R.drawable.like_heart)
                    }else{
                        Log.e("RetrofitLikesError", "problem liking post ${response.errorBody().toString()}")
                    }
                }
                override fun onFailure(call: Call<FollowsReply>, t: Throwable) {
                    Log.e("RetrofitFailure", "Couldn't reach the server ${t.message.toString()}")
                }
            })
        }
    }
}