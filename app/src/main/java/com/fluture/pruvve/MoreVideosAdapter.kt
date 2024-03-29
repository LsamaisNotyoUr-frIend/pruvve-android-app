package com.fluture.pruvve


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class MoreVideosAdapter(private var videos: List<VideoItems>):RecyclerView.Adapter<MoreVideosAdapter.FeedsViewHolder>() {
    inner class FeedsViewHolder(itemView: View): RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup,viewType: Int):MoreVideosAdapter.FeedsViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_feed_videos, parent, false)
        return FeedsViewHolder(view)
    }
    override fun onBindViewHolder(holder: FeedsViewHolder, position: Int) {
        holder.itemView.apply {
            findViewById<WebView>(R.id.wvFeedsProfilePicture).loadUrl(videos[position].profilePicUrl)
            findViewById<TextView>(R.id.tvTimestamp).text = videos[position].timeStamp
            findViewById<TextView>(R.id.tvProfileName).text = videos[position].name
            findViewById<WebView>(R.id.wvFeeds).loadUrl(videos[position].videoUrl)
            findViewById<TextView>(R.id.tvFollowButton).text = videos[position].follow
            findViewById<TextView>(R.id.tvViews).text = videos[position].views
            findViewById<TextView>(R.id.tvComments).text = videos[position].comments
            findViewById<TextView>(R.id.tvLikes).text = videos[position].likes
        }
    }
    override fun getItemCount(): Int {
        return videos.size
    }
}

data class VideoItems(
    val profilePicUrl: String,
    val timeStamp:String,
    val name: String,
    val title:String,
    val videoUrl:String,
    val follow: String,
    val views: String,
    val comments: String,
    val likes:String
)