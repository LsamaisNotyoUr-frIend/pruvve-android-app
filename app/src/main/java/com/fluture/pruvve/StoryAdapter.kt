package com.fluture.pruvve

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.widget.TextView
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
            findViewById<WebView>(R.id.wvStory).loadUrl(story[position].url)
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