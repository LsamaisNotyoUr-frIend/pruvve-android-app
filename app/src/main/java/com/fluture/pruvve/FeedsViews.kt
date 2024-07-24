package com.fluture.pruvve

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.fluture.pruvve.adapters.CommentAdapter
import com.fluture.pruvve.adapters.Comments
import com.fluture.pruvve.adapters.LikeAdapter
import com.fluture.pruvve.adapters.Likes
import com.fluture.pruvve.databinding.ActivityFeedsViewsBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FeedsViews : AppCompatActivity() {
    private lateinit var binding :ActivityFeedsViewsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityFeedsViewsBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        val myUrl = "https://www.pinterest.com/pin/13299761394513929/"
        val myUrl2 = "https://www.pinterest.com/pin/2462974790630602/"
        val commentRecycler =binding.rvCommentsAndLikes

        val commentTextView = binding.tvMyComments
        val likesTextView = binding.tvLikeItems
        commentTextView.setOnClickListener {
            val comments = mutableListOf(
                Comments("irvin", myUrl, "i think those are nice"),
                Comments("poole", myUrl, "what would happen otherwise "),
                Comments("Browning", myUrl, "where abouts of a mysterious gangster"),
                Comments("damian", myUrl2, "humanity is its out plight"),
            )
            val commentAdapter = CommentAdapter(comments)
            commentRecycler.adapter = commentAdapter
            commentRecycler.layoutManager = LinearLayoutManager(this)
            commentTextView.bringToFront()
            commentTextView.setBackgroundResource(R.drawable.dialogue_background3)
            likesTextView.setBackgroundResource(R.drawable.dialogue_background4)
        }
        likesTextView.setOnClickListener {
            val likes = mutableListOf(
                Likes("Markus", myUrl),
                Likes("pinion", myUrl),
                Likes("Dumus", myUrl),
                Likes("Rachel", myUrl2),
                Likes("killer", myUrl),
                Likes("wbna", myUrl),
                Likes("shitmus", myUrl),
                Likes("KingDavid", myUrl)
            )
            val likesAdapter = LikeAdapter(likes)
            commentRecycler.adapter = likesAdapter
            commentRecycler.layoutManager = LinearLayoutManager(this)
            likesTextView.bringToFront()
            commentTextView.setBackgroundResource(R.drawable.dialogue_background4)
            likesTextView.setBackgroundResource(R.drawable.dialogue_background3)
        }
    }
}