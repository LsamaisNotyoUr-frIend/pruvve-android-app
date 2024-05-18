package com.fluture.pruvve.adapters

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import androidx.recyclerview.widget.RecyclerView
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
            findViewById<WebView>(R.id.imvMediaMedium).loadUrl(currentFilter.imageUri.toString())
        }
        holder.itemView.setOnClickListener {
            onItemClick(currentFilter.imageUri)
        }
    }

    override fun getItemCount(): Int {
        return  mediaList.size
    }
}

data class MediaFilter(
    val imageUri: Uri
)