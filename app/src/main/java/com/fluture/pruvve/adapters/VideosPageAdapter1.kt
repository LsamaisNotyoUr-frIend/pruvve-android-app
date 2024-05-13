package com.fluture.pruvve.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import androidx.recyclerview.widget.RecyclerView
import com.fluture.pruvve.R

class VideosPageAdapter1(private var videos:List<VideoPageItems>):RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val layoutIds = arrayOf(
        R.layout.item_videos_small,
        R.layout.item_videos_medium,
        R.layout.item_videos_large
    )

    inner class VideoSmallViewHolder(itemView: View): RecyclerView.ViewHolder(itemView)
    inner class VideoMediumViewHolder(itemView: View): RecyclerView.ViewHolder(itemView)
    inner class VideoLargeViewHolder(itemView: View): RecyclerView.ViewHolder(itemView)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutId = layoutIds.random()
        val view = LayoutInflater.from(parent.context).inflate(layoutId, parent, false)
        return when (layoutId) {
            R.layout.item_videos_small -> VideoSmallViewHolder(view)
            R.layout.item_videos_medium -> VideoMediumViewHolder(view)
            R.layout.item_videos_large -> VideoLargeViewHolder(view)
            else -> throw IllegalArgumentException("Invalid layout ID")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder){
            is VideoSmallViewHolder -> holder.itemView.apply {
                findViewById<WebView>(R.id.wvSmallVideos).loadUrl(videos[position].url)
            }
            is VideoMediumViewHolder -> holder.itemView.apply {
                findViewById<WebView>(R.id.wvMediumVideos).loadUrl(videos[position].url)
            }
            is VideoLargeViewHolder -> holder.itemView.apply {
                findViewById<WebView>(R.id.wvLargeVideos).loadUrl(videos[position].url)
            }
        }
    }

    override fun getItemCount(): Int {
        return videos.size
    }
}

data class VideoPageItems(var url: String)