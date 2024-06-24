package com.fluture.pruvve
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.room.Room
import com.fluture.pruvve.adapters.MoreVideosAdapter
import com.fluture.pruvve.auth.AuthInterceptor
import com.fluture.pruvve.auth.LoginManager
import com.fluture.pruvve.databinding.ActivityMorePageBinding
import com.fluture.pruvve.essentials.DateUtils
import com.fluture.pruvve.localdatabase.PostRepository
import com.fluture.pruvve.localdatabase.PostViewModel
import com.fluture.pruvve.localdatabase.PostViewModelFactory
import com.fluture.pruvve.localdatabase.PruvveDatabase
import com.fluture.pruvve.localdatabase.SavedPost
import com.fluture.pruvve.retrofittcalls.AllPostItems
import com.fluture.pruvve.retrofittcalls.FollowStatusReply
import com.fluture.pruvve.retrofittcalls.FollowedId
import com.fluture.pruvve.retrofittcalls.FollowerId
import com.fluture.pruvve.retrofittcalls.GetAllPosts
import com.fluture.pruvve.retrofittcalls.GetFeedsMedia
import com.fluture.pruvve.retrofittcalls.GetPostsSummary
import com.fluture.pruvve.retrofittcalls.UploadImage
import com.fluture.pruvve.retrofittcalls.UploadResponse
import com.fluture.pruvve.retrofittcalls.UserService
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory


class MorePage : AppCompatActivity() {
    private lateinit var binding: ActivityMorePageBinding
    private lateinit var repository: PostRepository
    private val postViewModel: PostViewModel by viewModels {
        PostViewModelFactory(PostRepository(PruvveDatabase.getDatabase(this).postDao))
    }
    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityMorePageBinding.inflate(layoutInflater)
        LoginManager.init(this)
        val database = Room.databaseBuilder(this, PruvveDatabase::class.java, "pruvve_database").build()
        val dao = database.postDao
        repository = PostRepository(dao)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        binding.rvFeeds.visibility = View.GONE
        val listPosts = mutableListOf(
            SavedPost(
                "empty", "10minutes ago", "user1",
                "The end of times", "empty", true, "1000 views", "500 comments", "250 likes", 5, 3
            )
        )
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

        val adapter = MoreVideosAdapter(listPosts, service)
        Log.d("Database2", "Recycler being filled")
        binding.rvFeeds.adapter = adapter
        binding.rvFeeds.layoutManager = LinearLayoutManager(this@MorePage)
        val id = intent.getIntExtra("userId", 5)
        getPosts(service, id)

        postViewModel.allPosts.observe(this@MorePage) { posts ->
            Log.e("Database", "Started")
            if (posts.isNotEmpty()) {
                listPosts.clear()  // Clear the initial post
                for (post in posts) {
                    Log.d("Url", post.videoUrl)
                    val postToUpload = SavedPost(
                        post.profilePicUrl, post.timeStamp, post.name, post.title,
                        post.videoUrl, post.follow, post.views, post.comments, post.likes, post.otherUsersId, post.postId
                    )
                    listPosts.add(postToUpload)
                }
                adapter.notifyDataSetChanged()
                binding.rvFeeds.visibility = View.VISIBLE
            } else {
                Log.d("Database", "No posts collected")
            }
        }

        Log.d("RetrofitPosts", "your list size is ${listPosts.size}")

        Log.d("RetrofitPosts", "your list size is ${listPosts.size}")
        val popular = binding.tvPopular
        val nearby = binding.tvNearby
        val new = binding.tvNew
        val following = binding.tvFollowing
        val paramsPopular = popular.layoutParams as LinearLayout.LayoutParams
        val paramsNearby = nearby.layoutParams as LinearLayout.LayoutParams
        val paramsNew = new.layoutParams as LinearLayout.LayoutParams
        val paramsFollowing = following.layoutParams as LinearLayout.LayoutParams
        binding.button1.setOnClickListener {
            finish()
        }

        binding.imvAddAPost.setOnClickListener {
            Intent(this@MorePage, CustomGallery::class.java).also {
                startActivity(it)
            }
        }

        popular.setOnClickListener {
            paramsPopular.weight = 1.6F
            paramsNearby.weight = 1.9F
            paramsNew.weight = 2.0F
            paramsFollowing.weight = 1.7F

            popular.setBackgroundResource(R.drawable.dialogue_background3)
            nearby.setBackgroundResource(R.drawable.more_videos_backgrounds)
            new.setBackgroundResource(R.drawable.more_videos_backgrounds)
            following.setBackgroundResource(R.drawable.more_videos_backgrounds)
        }
        nearby.setOnClickListener {
            paramsPopular.weight = 1.8F
            paramsNearby.weight = 1.7F
            paramsNew.weight = 2.0F
            paramsFollowing.weight = 1.7F

            popular.setBackgroundResource(R.drawable.more_videos_backgrounds)
            nearby.setBackgroundResource(R.drawable.dialogue_background3)
            new.setBackgroundResource(R.drawable.more_videos_backgrounds)
            following.setBackgroundResource(R.drawable.more_videos_backgrounds)
        }

        new.setOnClickListener {
            paramsPopular.weight = 1.8F
            paramsNearby.weight = 1.9F
            paramsNew.weight = 1.7F
            paramsFollowing.weight = 1.7F

            popular.setBackgroundResource(R.drawable.more_videos_backgrounds)
            nearby.setBackgroundResource(R.drawable.more_videos_backgrounds)
            new.setBackgroundResource(R.drawable.dialogue_background3)
            following.setBackgroundResource(R.drawable.more_videos_backgrounds)
        }

        following.setOnClickListener {
            paramsPopular.weight = 1.8F
            paramsNearby.weight = 1.9F
            paramsNew.weight = 2.0F
            paramsFollowing.weight = 1.5F

            popular.setBackgroundResource(R.drawable.more_videos_backgrounds)
            nearby.setBackgroundResource(R.drawable.more_videos_backgrounds)
            new.setBackgroundResource(R.drawable.more_videos_backgrounds)
            following.setBackgroundResource(R.drawable.dialogue_background3)
        }
    }

    private fun getPosts(service: UserService, id: Int) {
        service.getPosts(GetFeedsMedia(1, 5)).enqueue(object : Callback<GetAllPosts> {
            override fun onResponse(call: Call<GetAllPosts>, response: Response<GetAllPosts>) {
                if (response.isSuccessful) {
                    response.body()?.data?.list?.let {
                        Log.d("Database1", "Uploading Posts")
                        it.forEach { post -> uploadMediaAndProfilePictures(service, post, id) }
                    } ?: Log.e("Error", "List is empty")
                } else {
                    logError(response.errorBody()?.toString(), "Couldn't get the media URL")
                }
            }

            override fun onFailure(call: Call<GetAllPosts>, t: Throwable) {
                logFailure(t, "Couldn't reach the server")
            }
        })
    }

    private fun uploadMediaAndProfilePictures(service: UserService, posts: AllPostItems, id: Int) {
        uploadImage(service, posts.mediaUrl) { mediaToUploadUrl ->
            Log.d("Database2", "Uploading Posts")
            uploadImage(service, posts.user.profilePictureUrl) { profilePicToUploadUrl ->
                Log.d("Database3", "Uploading Posts")
                getPostSummary(service, posts,true, mediaToUploadUrl, profilePicToUploadUrl)
            }
        }
    }
    private fun getFollowStatus(service: UserService, posts: AllPostItems, mediaUrl: String?, profilePicUrl: String?, id: Int) {
        service.getFollowStatus(FollowerId(id), FollowedId(posts.user.id)).enqueue(object : Callback<FollowStatusReply> {
            override fun onResponse(call: Call<FollowStatusReply>, response: Response<FollowStatusReply>) {
                if (response.isSuccessful) {
                    Log.d("Database4", "Uploading Posts")
                    getPostSummary(service, posts, response.body()?.data ?: true, mediaUrl, profilePicUrl)
                } else {
                    logError(response.errorBody()?.toString(), "Couldn't get follow status")
                }
            }

            override fun onFailure(call: Call<FollowStatusReply>, t: Throwable) {
                logFailure(t, "Couldn't reach the server for follow status")
            }
        })
    }

    private fun getPostSummary(service: UserService, posts: AllPostItems, followStatus: Boolean, mediaUrl: String?, profilePicUrl: String?) {
        service.getPostSummary(posts.id).enqueue(object : Callback<GetPostsSummary> {
            override fun onResponse(call: Call<GetPostsSummary>, response: Response<GetPostsSummary>) {
                if (response.isSuccessful) {
                    Log.d("Database6", "Uploading Posts")
                    val summary = response.body()?.data
                    savePostToDatabase(posts, mediaUrl, profilePicUrl, followStatus,
                        summary?.viewCount ?: 1000,
                        summary?.likeCount ?: 500,
                        summary?.commentCount ?: 20,)
                } else {
                    logError(response.errorBody()?.toString(), "Couldn't get post summary")
                }
            }

            override fun onFailure(call: Call<GetPostsSummary>, t: Throwable) {
                logFailure(t, "Couldn't reach the server for post summary")
            }
        })
    }

    private fun savePostToDatabase(posts: AllPostItems, mediaUrl: String?, profilePicUrl: String?, followStatus: Boolean,
                                   viewCount: Int, likeCount: Int, commentCount: Int) {
        Log.d("DatabaseFinal", "Uploading Posts")
        val postToUpload = SavedPost(profilePicUrl = profilePicUrl ?: "s", timeStamp = DateUtils.getRelativeTimeString(posts.creationDate) ,
            name = posts.user.username, title = posts.caption, videoUrl = mediaUrl ?: "weee", follow = followStatus,
            views = "$viewCount views", comments = "$commentCount comments", likes = "$likeCount likes",
            otherUsersId = posts.user.id, postId = posts.id)
        postViewModel.upsertPost(postToUpload)
        Log.d("Database", "Posts Uploaded")
    }

    private fun uploadImage(service: UserService, url: String, onSuccess: (String?) -> Unit) {
        service.uploadPicture(UploadImage(url, "DOWNLOAD")).enqueue(object : Callback<UploadResponse> {
            override fun onResponse(call: Call<UploadResponse>, response: Response<UploadResponse>) {
                if (response.isSuccessful) {
                    onSuccess(response.body()?.data)
                } else {
                    logError(response.errorBody()?.toString(), "Couldn't get the image URL")
                }
            }
            override fun onFailure(call: Call<UploadResponse>, t: Throwable) {
                logFailure(t, "Couldn't reach the server")
            }
        })
    }

    private fun logError(errorBody: String?, message: String) {
        Log.e("Error", "$message: $errorBody")
    }

    private fun logFailure(t: Throwable, message: String) {
        Log.e("Failure", "$message: ${t.message}")
    }
}