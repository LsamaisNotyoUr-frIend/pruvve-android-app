package com.fluture.pruvve

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class NewsAdapter(private var news: List<NewsItems>): RecyclerView.Adapter<NewsAdapter.NewsViewHolder>() {
    inner class NewsViewHolder(itemView: View): RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_newsitems, parent, false)
        return NewsViewHolder(view)
    }

    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {
        holder.itemView.apply {
            findViewById<TextView>(R.id.tvNewsObjectsTimestamp).text = news[position].timeStamp
            findViewById<TextView>(R.id.tvNewsObjectsTitle).text = news[position].title
            findViewById<TextView>(R.id.tvNewsArticles).text = news[position].newsBody
            findViewById<ImageView>(R.id.ivNewsArticleCover).setImageURI(Uri.parse(news[position].url))
        }
    }

    override fun getItemCount(): Int {
        return news.size
    }
}

data class NewsItems(
    val url: String,
    val title: String,
    val timeStamp: String,
    val newsBody: String
)