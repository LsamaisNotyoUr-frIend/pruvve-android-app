package com.fluture.pruvve.adapters

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.fluture.pruvve.R
import com.fluture.pruvve.ViewPitchItem

class PitchAdapter(private val pitches: List<Pitches>):RecyclerView.Adapter<PitchAdapter.PitchViewHolder>() {
    inner class PitchViewHolder(itemView: View): RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PitchViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_pitches, parent, false)
        return PitchViewHolder(view)
    }

    override fun getItemCount(): Int {
        return pitches.size
    }

    override fun onBindViewHolder(holder: PitchViewHolder, position: Int) {
        val currentItem = pitches[position]
        holder.itemView.apply {
            findViewById<TextView>(R.id.tvLocationTitle).text = currentItem.title
            findViewById<TextView>(R.id.tvLocations).text = currentItem.location
            findViewById<WebView>(R.id.wvPitches).loadUrl(currentItem.backgroundUrl)
            val rating = currentItem.rating
            if (rating in 1..5) {
                for (i in 1..5) {
                    val starImageView = holder.itemView.findViewById<ImageView>(getStarImageViewId(i))
                    if (i <= rating) {
                        starImageView.visibility = View.VISIBLE
                    } else {
                        starImageView.visibility = View.INVISIBLE
                    }
                }
            }
            findViewById<TextView>(R.id.tvFormat).text = currentItem.format
            findViewById<TextView>(R.id.tvSurface).text = currentItem.surface
            findViewById<TextView>(R.id.tvFacilities1).text = currentItem.facilities
            findViewById<TextView>(R.id.tvFacilities2).text = currentItem.facilities2
            findViewById<TextView>(R.id.tvFacilities3).text = currentItem.facilities3
            findViewById<Button>(R.id.btnChoosePitch).setOnClickListener{
                val context = holder.itemView.context
                val intent = Intent(context, ViewPitchItem::class.java)
                intent.putExtra("Extra_title", currentItem.title)
                intent.putExtra("Extra_location", currentItem.location)
                context.startActivity(intent)
            }
        }
    }


    private fun getStarImageViewId(position: Int): Int {
        return when (position) {
            1 -> R.id.ivStar1
            2 -> R.id.ivStar2
            3 -> R.id.ivStar3
            4 -> R.id.ivStar4
            5 -> R.id.ivStar5
            else -> throw IllegalArgumentException("Invalid position: $position")
        }
    }
}
data class Pitches(
    val title: String,
    val location: String,
    val backgroundUrl: String,
    val rating: Int,
    val format: String,
    val surface:String,
    val facilities: String,
    val facilities2: String,
    val facilities3: String
)