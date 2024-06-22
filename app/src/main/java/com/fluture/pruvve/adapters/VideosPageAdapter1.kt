package com.fluture.pruvve.adapters

import android.media.MediaPlayer
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.VideoView
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
                val videoView = findViewById<VideoView>(R.id.vvSmallVideos)
                val imageView = findViewById<ImageView>(R.id.imvPlayVideos)
                setUpVideoView(videoView, imageView, videos[position].url)
            }
            is VideoMediumViewHolder -> holder.itemView.apply {
                val videoView = findViewById<VideoView>(R.id.vvMediumVideos)
                val imageView = findViewById<ImageView>(R.id.imvPlayVideos)
                setUpVideoView(videoView, imageView, videos[position].url)
            }
            is VideoLargeViewHolder -> holder.itemView.apply {
                val videoView = findViewById<VideoView>(R.id.vvLargeVideos)
                val imageView = findViewById<ImageView>(R.id.imvPlayVideos)
                setUpVideoView(videoView, imageView, videos[position].url)
            }
        }
    }

    private fun setUpVideoView(videoView: VideoView, imageView: ImageView, videoUrl: String) {
        val videoUri = Uri.parse(videoUrl)
        videoView.setVideoURI(videoUri)
        videoView.setMediaController(null)
        videoView.setOnPreparedListener {
            imageView.visibility = View.GONE
            it.isLooping = true
            it.setVideoScalingMode(MediaPlayer.VIDEO_SCALING_MODE_SCALE_TO_FIT)
            it.start()
        }
    }

    override fun getItemCount(): Int {
        return videos.size
    }
}

data class VideoPageItems(var url: String)