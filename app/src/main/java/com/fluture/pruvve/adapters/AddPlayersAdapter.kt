package com.fluture.pruvve.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.fluture.pruvve.R

class AddPlayersAdapter(private val playerList: MutableList<AddPlayerItems>):RecyclerView.Adapter<AddPlayersAdapter.AddPlayerViewHolder>() {
    inner class AddPlayerViewHolder(itemView: View):RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddPlayerViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_add_player, parent, false)
        return AddPlayerViewHolder(view)
    }

    override fun onBindViewHolder(holder: AddPlayerViewHolder, position: Int) {
        holder.itemView.apply {
            val currentItem = playerList[position]
            findViewById<TextView>(R.id.tvPlayerNames).text = currentItem.userName
            findViewById<TextView>(R.id.tvPlayersPosition).text = currentItem.position
            findViewById<TextView>(R.id.tvPlayersLeg).text = currentItem.preferredFeet
            val imageView = findViewById<ImageView>(R.id.imvPlayers)
            Glide.with(context)
                .load(currentItem.url)
                .into(imageView)
            findViewById<ImageView>(R.id.imvSendALink).setOnClickListener {
                findViewById<ImageView>(R.id.imvSendALink).setImageResource(R.drawable.sent_link)
            }
        }
    }

    override fun getItemCount(): Int {
        return playerList.size
    }
}
data class AddPlayerItems(
    val url :String,
    val userName: String,
    val preferredFeet: String,
    val position: String,
)