package com.fluture.pruvve.adapters

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.fluture.pruvve.R

class MediaAdapter (private val mediaList: List<MediaFilter>): RecyclerView.Adapter<MediaAdapter.FilterViewHolder>() {
    private var listener: OnItemClickListener? = null
    interface OnItemClickListener {
        fun onItemClick(filterPosition: Int)
    }
    inner class FilterViewHolder(itemView: View):RecyclerView.ViewHolder(itemView), View.OnClickListener {
        init {
            itemView.setOnClickListener(this)
        }
        override fun onClick(v: View?) {
            val position = adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                listener?.onItemClick(position)
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FilterViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.media_medium, parent, false)
        return FilterViewHolder(view)
    }

    override fun onBindViewHolder(holder: FilterViewHolder, position: Int) {
        val currentFilter = mediaList[position]
        holder.itemView.apply {
            findViewById<ImageView>(R.id.imvMediaMedium).setImageURI(currentFilter.imageUri)

        }
    }

    override fun getItemCount(): Int {
        TODO("Not yet implemented")
    }
}

data class MediaFilter(
    val name: String,
    val imageUri: Uri
)