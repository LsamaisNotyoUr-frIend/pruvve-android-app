package com.fluture.pruvve.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.fluture.pruvve.R

class TeamsAdapter(private val teamMates: List<TeamMates>):RecyclerView.Adapter<TeamsAdapter.TeamsViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TeamsViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_team_members, parent, false)
        return TeamsViewHolder(view)
    }

    override fun onBindViewHolder(holder: TeamsViewHolder, position: Int) {
        val currentItem = teamMates[position]
        holder.itemView.apply {
            findViewById<TextView>(R.id.tvTeamMatesNames).text = currentItem.name
            findViewById<TextView>(R.id.tvTeamMemberPosition).text = currentItem.position
            findViewById<TextView>(R.id.tvTeamMemberLeg).text = currentItem.position
            Glide.with(context)
                .load(currentItem.url)
                .apply(RequestOptions().circleCrop())
                .into(findViewById(R.id.imvTeamMembers))
        }
        if (position == 0 || position == teamMates.size - 1) {
            holder.itemView.setBackgroundResource(R.drawable.team_background2)
        } else {
            holder.itemView.setBackgroundResource(R.drawable.team_background)
        }
    }

    override fun getItemCount(): Int {
        return  teamMates.size
    }

    inner class TeamsViewHolder(itemView: View): RecyclerView.ViewHolder(itemView)

}
data class TeamMates(
    val url:String,
    val name: String,
    val position: String,
    val leg: String
)