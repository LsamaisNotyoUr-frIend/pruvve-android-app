package com.fluture.pruvve.adapters


import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.Guideline
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fluture.pruvve.R
import com.fluture.pruvve.ShowVideos
import com.fluture.pruvve.auth.AuthInterceptor
import com.fluture.pruvve.auth.LoginManager
import com.fluture.pruvve.retrofittcalls.FollowsAndUnfollows
import com.fluture.pruvve.retrofittcalls.FollowsReply
import com.fluture.pruvve.retrofittcalls.UserService
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

class MoreVideosAdapter(private var videos: List<VideoItems>, private val service: UserService):RecyclerView.Adapter<MoreVideosAdapter.VideosViewHolder>() {
    inner class VideosViewHolder(itemView: View): RecyclerView.ViewHolder(itemView)
    override fun onCreateViewHolder(parent: ViewGroup,viewType: Int): VideosViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_feed_videos, parent, false)
        return VideosViewHolder(view)
    }
    override fun onBindViewHolder(holder: VideosViewHolder, position: Int) {
        val actualPosition = position + 1 // Shift the position by 1 to start from the second item
        if (actualPosition < videos.size) {
            var isLiked = false
            val currentItem = videos[actualPosition]
            holder.itemView.apply {
                var isCommentClicked =  false
                val guideline = findViewById<Guideline>(R.id.guideline324)
                val constraintLayout = findViewById<ConstraintLayout>(R.id.clFeedVideos)
                val clParams = constraintLayout.layoutParams
                val params = guideline.layoutParams as ConstraintLayout.LayoutParams
                findViewById<WebView>(R.id.wvFeedsProfilePicture).loadUrl(videos[position].profilePicUrl)
                findViewById<TextView>(R.id.tvTimestamp).text = currentItem.timeStamp
                findViewById<TextView>(R.id.tvProfileName).text = currentItem.name
                findViewById<WebView>(R.id.wvFeeds).loadUrl(currentItem.videoUrl)
                findViewById<TextView>(R.id.tvViews).text = currentItem.views
                findViewById<TextView>(R.id.tvComments).text = currentItem.comments
                findViewById<TextView>(R.id.tvLikes).text = currentItem.likes
                findViewById<ImageView>(R.id.ivLikeButton).setOnClickListener {
                    isLiked = !isLiked
                    if (isLiked){
                        findViewById<ImageView>(R.id.ivLikeButton).setImageResource(R.drawable.like_cliked)
                    }else{
                        findViewById<ImageView>(R.id.ivLikeButton).setImageResource(R.drawable.like_heart)
                    }
                }
                findViewById<ImageView>(R.id.ivCommentButton).setOnClickListener {
                    isCommentClicked = !isCommentClicked
                    if (isCommentClicked){
                        params.guidePercent = 0.00001f
                        guideline.layoutParams = params
                        findViewById<ConstraintLayout>(R.id.clFeedVideos)
                        clParams.height = ConstraintLayout.LayoutParams.MATCH_PARENT // Set height to MATCH_PARENT
                        constraintLayout.layoutParams = clParams
                        findViewById<WebView>(R.id.wvFeeds).visibility = View.GONE
                        findViewById<WebView>(R.id.wvFeedsProfilePicture).visibility = View.GONE
                        findViewById<ConstraintLayout>(R.id.clCommentsAndLikes).bringToFront()
                        findViewById<CardView>(R.id.cvWVFeeds).visibility = View.GONE
                        findViewById<CardView>(R.id.cvMainFeeds).visibility = View.GONE
                    }else{
                        params.guidePercent = 1.0f
                        guideline.layoutParams = params
                        findViewById<ConstraintLayout>(R.id.clFeedVideos)
                        clParams.height = resources.dpToPx(400)
                        constraintLayout.layoutParams = clParams
                        findViewById<WebView>(R.id.wvFeeds).visibility = View.VISIBLE
                        findViewById<WebView>(R.id.wvFeedsProfilePicture).visibility = View.VISIBLE
                        findViewById<CardView>(R.id.cvWVFeeds).visibility = View.VISIBLE
                        findViewById<CardView>(R.id.cvMainFeeds).visibility = View.VISIBLE
                    }
                }
                toggleFollowStatus(findViewById(R.id.tvFollowButton), currentItem.follow)
                changeFollowStatus(findViewById(R.id.tvFollowButton), currentItem.follow, currentItem.otherUsersId)
                currentItem.follow = changeFollowStatus(findViewById(R.id.tvFollowButton), currentItem.follow, currentItem.otherUsersId)

                findViewById<WebView>(R.id.wvFeeds).setOnClickListener {
                    val intent = Intent(context, ShowVideos::class.java)
                    intent.putExtra("videoUrl", videos[position].videoUrl)
                    context.startActivity(intent)
                }
            }
        }
    }
    override fun getItemCount(): Int {
        return videos.size
    }
    @SuppressLint("SetTextI18n")
    fun toggleFollowStatus(textView: TextView, isFollowed: Boolean) {
        if (isFollowed) {
            textView.text = "   Follow   "
            textView.setBackgroundResource(R.drawable.sign_in_viewbackgrounds)
        }else {
            textView.text = "  Unfollow  "
            textView.setBackgroundResource(R.drawable.unfollow_background)
        }
    }

    private fun changeFollowStatus(textView: TextView, isFollowed: Boolean, userId: Int):Boolean {
        toggleFollowStatus(textView, isFollowed)
        textView.setOnClickListener {
            val currentFollow = FollowsAndUnfollows(
                userId = userId
            )
            if (isFollowed){
                service.unFollowUser(currentFollow).enqueue(object : Callback<FollowsReply>{
                    override fun onResponse(call: Call<FollowsReply>, response: Response<FollowsReply>) {
                        if (response.isSuccessful){
                            Log.d("RetrofitSuccess", response.body()?.message.toString())
                            return
                        }else{
                            Log.e("RetrofitError", "Error in making the request ${response.errorBody().toString()}")
                        }
                    }
                    override fun onFailure(call: Call<FollowsReply>, t: Throwable) {
                        Log.d("RetrofitFailure", "couldn't reach the server ${t.message.toString()}")
                    }
                })
                !isFollowed
                toggleFollowStatus(textView, false)
            }else{
                service.followUser(currentFollow).enqueue(object : Callback<FollowsReply>{
                    override fun onResponse(call: Call<FollowsReply>, response: Response<FollowsReply>) {
                        if (response.isSuccessful){
                            Log.d("RetrofitSuccess", response.body()?.message.toString())
                            return
                        }else{
                            Log.e("RetrofitError", "Error in making the request ${response.errorBody().toString()}")
                        }
                    }
                    override fun onFailure(call: Call<FollowsReply>, t: Throwable) {
                        Log.d("RetrofitFailure", "couldn't reach the server ${t.message.toString()}")
                    }
                })
                !isFollowed
                toggleFollowStatus(textView, true)
            }
        }
        return isFollowed
    }
    private fun Resources.dpToPx(dp: Int): Int {
        val density = displayMetrics.density
        return (dp * density).toInt()
    }
    private fun getComments(context: Context){
        LoginManager.init(context)
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

    }
    @SuppressLint("NotifyDataSetChanged")
    private fun PopulateCommentsAndLikes(comments: MutableList<Comments>, likes: MutableList<Likes>, recyclerView: RecyclerView, click: Boolean,
         editText: EditText, context: Context, username:String, url: String, sendIcon: ImageView, likeIcon: ImageView){
        val commentAdapter = CommentAdapter(comments)
        val likeAdapter = LikeAdapter(likes)
        recyclerView.adapter = commentAdapter
        recyclerView.layoutManager = LinearLayoutManager(context)
        sendIcon.setOnClickListener {
            if (editText.text != null){
                val position = comments.size
                comments.add(Comments(username, url, editText.text.toString()))
                commentAdapter.notifyItemInserted(position)
            }
        }
        likeIcon.setOnClickListener {
            val position = likes.size
            likes.add(Likes(username, url))
            likeAdapter.notifyItemInserted(position)
        }
        if(click){
            recyclerView.adapter = commentAdapter
        }else{
            recyclerView.adapter = likeAdapter
        }
    }
}

data class VideoItems(
    val profilePicUrl: String,
    val timeStamp:String,
    val name: String,
    val title:String,
    val videoUrl:String,
    var follow: Boolean,
    val views: String,
    val comments: String,
    val likes:String,
    val otherUsersId : Int
)