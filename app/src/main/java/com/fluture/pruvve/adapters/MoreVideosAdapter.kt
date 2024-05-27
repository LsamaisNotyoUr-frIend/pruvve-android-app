package com.fluture.pruvve.adapters


import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.Guideline
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.fluture.pruvve.R
import com.fluture.pruvve.ShowVideos
import com.fluture.pruvve.retrofittcalls.FollowsAndUnfollows
import com.fluture.pruvve.retrofittcalls.FollowsReply
import com.fluture.pruvve.retrofittcalls.MakeComments
import com.fluture.pruvve.retrofittcalls.RequestObjects
import com.fluture.pruvve.retrofittcalls.ServerComments
import com.fluture.pruvve.retrofittcalls.ServerLikes
import com.fluture.pruvve.retrofittcalls.UploadImage
import com.fluture.pruvve.retrofittcalls.UploadResponse
import com.fluture.pruvve.retrofittcalls.UserService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MoreVideosAdapter(private var videos: List<VideoItems>, private val service: UserService):RecyclerView.Adapter<MoreVideosAdapter.VideosViewHolder>() {
    inner class VideosViewHolder(itemView: View): RecyclerView.ViewHolder(itemView)
    override fun onCreateViewHolder(parent: ViewGroup,viewType: Int): VideosViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_feed_videos, parent, false)
        return VideosViewHolder(view)
    }
    override fun onBindViewHolder(holder: VideosViewHolder, position: Int) {
        val currentItem = videos[position]
        holder.itemView.apply {
            var isCommentClicked =  false
            var isLiked = false
            var commentSelected: Boolean
            val guideline = findViewById<Guideline>(R.id.guideline324)
            val constraintLayout = findViewById<ConstraintLayout>(R.id.clFeedVideos)

            Glide.with(context)
                .load(currentItem.profilePicUrl)
                .apply(RequestOptions.circleCropTransform())
                .into(findViewById(R.id.imvFeedsProfilePicture))

            Glide.with(context)
                .load(currentItem.videoUrl)
                .apply(RequestOptions().fitCenter())
                .into(findViewById(R.id.imvFeeds))

            findViewById<TextView>(R.id.tvTimestamp).text = currentItem.timeStamp
            findViewById<TextView>(R.id.tvProfileName).text = currentItem.name
            findViewById<TextView>(R.id.tvViews).text = currentItem.views
            findViewById<TextView>(R.id.tvComments).text = currentItem.comments
            findViewById<TextView>(R.id.tvLikes).text = currentItem.likes
            val likesButton = findViewById<TextView>(R.id.tvLikeItems)
            val commentButton = findViewById<TextView>(R.id.tvMyComments)

            findViewById<ImageView>(R.id.ivLikeButton).setOnClickListener {
                isLiked = !isLiked
                sortLikes(findViewById(R.id.ivLikeButton), isLiked, currentItem.postId)
            }
            findViewById<ImageView>(R.id.ivCommentButton).setOnClickListener {
                isCommentClicked = !isCommentClicked
                openCommentSection(isCommentClicked, constraintLayout, guideline, this, currentItem.postId, this.context, true)
            }
            findViewById<ImageView>(R.id.imvExitCommentSection).setOnClickListener {
                openCommentSection(false, constraintLayout, guideline, this, currentItem.postId, this.context, true)
            }
            toggleFollowStatus(findViewById(R.id.tvFollowButton), currentItem.follow)
            val userId = currentItem.otherUsersId
            changeFollowStatus(findViewById(R.id.tvFollowButton), currentItem.follow, userId)

            findViewById<ImageView>(R.id.imvFeeds).setOnClickListener {
                val intent = Intent(context, ShowVideos::class.java)
                intent.putExtra("isLiked", isLiked)
                intent.putExtra("videoUrl", currentItem.videoUrl)
                intent.putExtra("VideoId", currentItem.postId)
                context.startActivity(intent)
            }
            likesButton.setOnClickListener {
                commentSelected = false
                openCommentSection(isCommentClicked, constraintLayout, guideline, this, currentItem.postId, this.context, commentSelected)
                likesButton.setBackgroundResource(R.drawable.dialogue_background3)
                commentButton.setBackgroundResource(R.drawable.dialogue_background4)
            }
            commentButton.setOnClickListener {
                commentSelected = true
                openCommentSection(isCommentClicked, constraintLayout, guideline, this, currentItem.postId, this.context, commentSelected)
                likesButton.setBackgroundResource(R.drawable.dialogue_background4)
                commentButton.setBackgroundResource(R.drawable.dialogue_background3)
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
                service.unFollowUser(userId).enqueue(object : Callback<FollowsReply>{
                    override fun onResponse(call: Call<FollowsReply>, response: Response<FollowsReply>) {
                        if (response.isSuccessful){
                            Log.d("RetrofitSuccess", response.body()?.message.toString())
                            return
                        }else{
                            val errorBody = response.errorBody()?.string()
                            Log.e("RetrofitError", "Error in making the request $errorBody")
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
                            val errorBody = response.errorBody()?.string()
                            Log.e("RetrofitError", "Error in making the request $errorBody")
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
    private fun getComments(context: Context, postId: Int, recyclerView: RecyclerView){
        val request = RequestObjects(
            1,
            5)
        val comments = mutableListOf<Comments>()
        service.getComments(postId, request).enqueue(object : Callback<ServerComments>{
            override fun onResponse(call: Call<ServerComments>, response: Response<ServerComments>) {
                if (response.isSuccessful){
                    comments.clear()
                    val commentLists = response.body()?.data?.list
                    if (commentLists != null){
                        for (commentItems in commentLists){
                            val user = commentItems.user
                            val downloadImage = UploadImage(
                                fileName = user.profilePictureUrl,
                                purpose = "DOWNLOAD")
                            service.uploadPicture(downloadImage).enqueue(object : Callback<UploadResponse>{
                                @SuppressLint("NotifyDataSetChanged")
                                override fun onResponse(call: Call<UploadResponse>, response: Response<UploadResponse>) {
                                    if (response.isSuccessful){
                                        val signedUrl = response.body()?.data.toString()
                                        val commentToAdd = Comments(user.username, signedUrl, commentItems.comment)
                                        comments.add(commentToAdd)
                                        if (comments.size == commentLists.size) {
                                            Log.d("RetrofitSuccess", "the loop is done")
                                            val commentAdapter = CommentAdapter(comments)
                                            recyclerView.adapter = commentAdapter
                                            recyclerView.layoutManager = LinearLayoutManager(context)
                                            commentAdapter.notifyDataSetChanged()
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
            override fun onFailure(call: Call<ServerComments>, t: Throwable) {
                Log.e("RetrofitFailure", "Failed to reach the server ${t.message.toString()}")
            }
        })
    }

    private fun getLikes(context: Context, postId: Int, recyclerView: RecyclerView){
        val request = RequestObjects(
            1,
            20)
        val likes = mutableListOf<Likes>()
        service.getLikes(postId, request).enqueue(object : Callback<ServerLikes>{
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

    private fun sortLikes(imageView: ImageView, isLiked: Boolean, postId: Int){
        if (isLiked){
            service.likePost(postId).enqueue(object : Callback<FollowsReply>{
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
            service.unLikePost(postId).enqueue(object : Callback<FollowsReply>{
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
        } }

    private fun makeComments(postId: Int, textToUpload: String){
        val makeComments = MakeComments(
            textToUpload
        )
        service.makeComment(postId, makeComments).enqueue(object :Callback<FollowsReply>{
            override fun onResponse(call: Call<FollowsReply>, response: Response<FollowsReply>) {
                if (response.isSuccessful){
                    Log.d("RetrofitSuccess", "Your comment has been uploaded successfully ${response.body()?.message.toString()}")
                }else{
                    Log.e("RetrofitError", "Problem uploading your comment")
                }
            }
            override fun onFailure(call: Call<FollowsReply>, t: Throwable) {
                Log.e("RetrofitFailure", "Couldn't reach the server ${t.message.toString()}")
            }
        })
    }

    private fun openCommentSection(isCommentClicked:Boolean,constraintLayout: ConstraintLayout,guideline: Guideline, itemView: View, postId: Int, context: Context,
                                   commentSelected:Boolean) {
        val recyclerView = (itemView.context as Activity).findViewById<RecyclerView>(R.id.rvFeeds)
        val clParams = constraintLayout.layoutParams
        val params = guideline.layoutParams as ConstraintLayout.LayoutParams
        if (isCommentClicked){
            recyclerView.isNestedScrollingEnabled = false
            params.guidePercent = 0.00001f
            guideline.layoutParams = params
            itemView.findViewById<ConstraintLayout>(R.id.clFeedVideos)
            clParams.height = ConstraintLayout.LayoutParams.MATCH_PARENT
            constraintLayout.layoutParams = clParams
            getComments(context, postId, itemView.findViewById(R.id.rvCommentsAndLikes))
            itemView.findViewById<ImageView>(R.id.imvFeeds).visibility = View.GONE
            itemView.findViewById<ImageView>(R.id.imvFeedsProfilePicture).visibility = View.GONE
            itemView.findViewById<ConstraintLayout>(R.id.clCommentsAndLikes).bringToFront()
            itemView.findViewById<LinearLayout>(R.id.llFeedsPosts).visibility = View.GONE
            itemView.findViewById<ConstraintLayout>(R.id.clMainFeeds).visibility = View.GONE
            itemView.findViewById<ImageView>(R.id.imvCommentSend).setOnClickListener {
                makeComments(postId, itemView.findViewById<EditText>(R.id.etCommentText).text.toString())
                itemView.findViewById<EditText>(R.id.etCommentText).text.clear()
                getComments(itemView.context, postId, itemView.findViewById(R.id.rvCommentsAndLikes))
            }
            if (commentSelected){
                getComments(itemView.context, postId, itemView.findViewById(R.id.rvCommentsAndLikes))
            }else{
                getLikes(itemView.context, postId, itemView.findViewById(R.id.rvCommentsAndLikes))
            }
        }else{
            params.guidePercent = 1.0f
            guideline.layoutParams = params
            itemView.findViewById<ConstraintLayout>(R.id.clFeedVideos)
            clParams.height = itemView.resources.dpToPx(400)
            constraintLayout.layoutParams = clParams
            recyclerView.isNestedScrollingEnabled = true
            itemView.findViewById<ImageView>(R.id.imvFeeds).visibility = View.VISIBLE
            itemView.findViewById<ImageView>(R.id.imvFeedsProfilePicture).visibility = View.VISIBLE
            itemView.findViewById<LinearLayout>(R.id.llFeedsPosts).visibility = View.VISIBLE
            itemView.findViewById<ConstraintLayout>(R.id.clMainFeeds).visibility = View.VISIBLE
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
    val otherUsersId : Int,
    val postId: Int
)