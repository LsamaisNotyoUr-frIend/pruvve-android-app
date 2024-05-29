package com.fluture.pruvve.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.fluture.pruvve.R

class StoryAdapter(private var story: List<Stories>):RecyclerView.Adapter<StoryAdapter.StoryViewHolder>() {
    inner class StoryViewHolder(itemView: View): RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoryViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_storyobjects, parent, false)
        return StoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: StoryViewHolder, position: Int) {
        holder.itemView.apply {
            Glide.with(context)
                .load(story[position].url)
                .apply(RequestOptions().centerCrop())
                .into(findViewById(R.id.imvStory))
            findViewById<TextView>(R.id.tvStoryName).text = story[position].name
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