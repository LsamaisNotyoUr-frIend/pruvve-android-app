package com.fluture.pruvve

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.VideoView
import androidx.recyclerview.widget.RecyclerView

class StoryAdapter(private var story: List<Stories>):RecyclerView.Adapter<StoryAdapter.StoryViewHolder>() {
    inner class StoryViewHolder(itemView: View): RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoryViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_storyobjects, parent, false)
        return StoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: StoryViewHolder, position: Int) {
        holder.itemView.apply {
            findViewById<TextView>(R.id.tvStoryName).text = story[position].name
            findViewById<VideoView>(R.id.vvStory).setVideoPath(Uri.parse(story[position].url).toString())
        }
    }

    override fun getItemCount(): Int {
        return story.size
    }
}

data class Stories(
    val name:String,
    val url: String
)