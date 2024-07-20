package com.fluture.pruvve.adapters

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.fluture.pruvve.R
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.ui.PlayerView

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
                val videoView = findViewById<PlayerView>(R.id.epSmallVideos)
                val imageView = findViewById<ImageView>(R.id.imvPlayVideos)
                setUpVideoView(videoView, imageView, videos[position].url, this.context)
            }
            is VideoMediumViewHolder -> holder.itemView.apply {
                val videoView = findViewById<PlayerView>(R.id.epMediumVideos)
                val imageView = findViewById<ImageView>(R.id.imvPlayVideos)
                setUpVideoView(videoView, imageView, videos[position].url, this.context)
            }
            is VideoLargeViewHolder -> holder.itemView.apply {
                val videoView = findViewById<PlayerView>(R.id.epLargeVideos)
                val imageView = findViewById<ImageView>(R.id.imvPlayVideos)
                setUpVideoView(videoView, imageView, videos[position].url, this.context)
            }
        }
    }

    private fun setUpVideoView(playerView: PlayerView, imageView: ImageView, videoUrl: String, context: Context) {
        playerView.visibility = View.VISIBLE
        val exoPlayer = ExoPlayer.Builder(context).build()
        playerView.player = exoPlayer
        val mediaItem = MediaItem.fromUri(Uri.parse(videoUrl))
        exoPlayer.setMediaItem(mediaItem)
        exoPlayer.prepare()
        exoPlayer.playWhenReady = true
        exoPlayer.repeatMode = ExoPlayer.REPEAT_MODE_ONE
        imageView.visibility = View.GONE

        playerView.addOnAttachStateChangeListener(object : View.OnAttachStateChangeListener {
            override fun onViewAttachedToWindow(v: View) {
                // No action needed
            }

            override fun onViewDetachedFromWindow(v: View) {
                exoPlayer.release()
            }
        })
    }

    override fun getItemCount(): Int {
        return videos.size
    }
}

data class VideoPageItems(var url: String)