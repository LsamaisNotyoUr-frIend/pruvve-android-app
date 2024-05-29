package com.fluture.pruvve.adapters

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.fluture.pruvve.R

class MediaAdapter (private val mediaList: List<MediaFilter>, private val onItemClick: (Uri) -> Unit): RecyclerView.Adapter<MediaAdapter.FilterViewHolder>() {
    inner class FilterViewHolder(itemView: View):RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FilterViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.media_medium, parent, false)
        return FilterViewHolder(view)
    }

    override fun onBindViewHolder(holder: FilterViewHolder, position: Int) {
        val currentFilter = mediaList[position]
        holder.itemView.apply {
            val imageView = findViewById<ImageView>(R.id.imvMediaMedium)
            Glide.with(context)
                .load(currentFilter.mediaUri)
                .apply(RequestOptions().centerCrop())
                .into(imageView)
        }
        holder.itemView.setOnClickListener {
            onItemClick(currentFilter.mediaUri)
        }
    }

    override fun getItemCount(): Int {
        return  mediaList.size
    }
}

data class MediaFilter(
    val mediaUri: Uri
)