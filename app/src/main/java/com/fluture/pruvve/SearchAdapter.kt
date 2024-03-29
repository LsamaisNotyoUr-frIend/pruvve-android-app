package com.fluture.pruvve

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class SearchAdapter(private val searches: List<SearchItems>): RecyclerView.Adapter<SearchAdapter.SearchViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_pitch_locations, parent, false)
        return SearchViewHolder(view)
    }

    override fun onBindViewHolder(holder: SearchViewHolder, position: Int) {
        val currentItem = searches[position]
        holder.itemView.apply {
            findViewById<TextView>(R.id.tvLocationSearchTitle).text = currentItem.title
            findViewById<TextView>(R.id.tvSearchSummary).text = currentItem.content
        }
    }

    override fun getItemCount(): Int {
        return  searches.size
    }

    inner class SearchViewHolder(itemView: View): RecyclerView.ViewHolder(itemView)
}
data class SearchItems(
    val title: String,
    val content: String
)