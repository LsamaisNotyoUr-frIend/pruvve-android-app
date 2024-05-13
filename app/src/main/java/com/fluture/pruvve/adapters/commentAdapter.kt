package com.fluture.pruvve.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.fluture.pruvve.R

class CommentAdapter(private var comments:List<Comments>):RecyclerView.Adapter<CommentAdapter.CommentViewHolder>() {
    inner class CommentViewHolder(itemview: View):RecyclerView.ViewHolder(itemview)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_comments, parent, false)
        return CommentViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: CommentViewHolder, position: Int) {
        val comment = comments[position].comment
        val index = comments[position]
        holder.itemView.apply {
            val tvComment = findViewById<TextView>(R.id.tvUserComments)
            val tvViewMore = findViewById<TextView>(R.id.tvViewMore)

            tvComment.text = comment
            if (comment.length > 300) {
                tvViewMore.visibility = View.VISIBLE
                tvComment.maxLines = 4

                tvViewMore.setOnClickListener {
                    if (tvComment.maxLines == 4) {
                        tvComment.maxLines = Integer.MAX_VALUE
                        tvViewMore.text = "View Less"
                    } else {
                        tvComment.maxLines = 4
                        tvViewMore.text = "View More"
                    }
                }
            } else {
                tvViewMore.visibility = View.GONE
            }
            findViewById<TextView>(R.id.tvCommentsUsername).text = index.username
            findViewById<WebView>(R.id.wvCommentProfilePicture).loadUrl(index.url)
        }
    }

    override fun getItemCount(): Int {
        return comments.size
    }
}
data class Comments(
    val username: String,
    val url: String,
    val comment:String
)

class LikeAdapter(private var likes:List<Likes>):RecyclerView.Adapter<LikeAdapter.LikesViewHolder>() {
    inner class LikesViewHolder(itemview: View) : RecyclerView.ViewHolder(itemview)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LikesViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_likes, parent, false)
        return LikesViewHolder(view)
    }

    override fun getItemCount(): Int {
        return likes.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: LikesViewHolder, position: Int) {
        val index = likes[position]
        holder.itemView.apply {
            findViewById<TextView>(R.id.tvLikesUsername).text = index.username
            findViewById<WebView>(R.id.wvLikesProfilePicture).loadUrl(index.url)
        }
    }
}
data class Likes(
    val username: String,
    val url: String
)
