package com.fluture.pruvve.adapters

import android.annotation.SuppressLint
import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.fluture.pruvve.R
import com.fluture.pruvve.essentials.TextManager

class SearchAdapter(private var searches: List<SearchItems>,private val myContext: Activity): RecyclerView.Adapter<SearchAdapter.SearchViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_pitch_locations, parent, false)
        return SearchViewHolder(view)
    }
    @SuppressLint("NotifyDataSetChanged")
    fun setData(newSearches: List<SearchItems>) {
        searches = newSearches
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: SearchViewHolder, position: Int) {
        val currentItem = searches[position]
        holder.itemView.apply {
            TextManager.init(context)
            val title =  findViewById<TextView>(R.id.tvLocationSearchTitle)
            title.text = currentItem.title
            findViewById<TextView>(R.id.tvSearchSummary).text = currentItem.content
            setOnClickListener {
                TextManager.saveText(currentItem.title)
                myContext.finish()
            }
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