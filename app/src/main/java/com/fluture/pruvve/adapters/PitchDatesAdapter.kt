package com.fluture.pruvve.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.fluture.pruvve.R
import java.util.Calendar

class PitchDatesAdapter(private val days:List<DayItems>):RecyclerView.Adapter<PitchDatesAdapter.DaysViewHolder>() {
    private var selectedIndex: Int = RecyclerView.NO_POSITION
    init {
        val currentDate = Calendar.getInstance()[Calendar.DAY_OF_MONTH]
        for ((index, item) in days.withIndex()) {
            if (item.dayOfTheMonth == currentDate) {
                selectedIndex = index
                break
            }
        }
    }
    inner class DaysViewHolder(itemView: View):RecyclerView.ViewHolder(itemView){
        init {
            itemView.setOnClickListener {
                val previousSelectedIndex = selectedIndex
                selectedIndex = adapterPosition
                notifyItemChanged(previousSelectedIndex)
                notifyItemChanged(selectedIndex)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DaysViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_days, parent, false)
        return DaysViewHolder(view)
    }

    override fun getItemCount(): Int {
        return days.size
    }

    override fun onBindViewHolder(holder: DaysViewHolder, position: Int) {
        val currentItem = days[position]
        holder.itemView.apply {
            findViewById<TextView>(R.id.tvDaysOfMonth).text = currentItem.dayOfTheMonth.toString()
            findViewById<TextView>(R.id.tvDatesOfWeek).text  =currentItem.dayOfTheWeek
            findViewById<View>(R.id.llDates).setBackgroundResource(
                if (position == selectedIndex) {
                    R.drawable.dates_backgrounds
                } else R.drawable.dates_backgrounds2
            )
        }
    }
    fun getSelectedIndex(): Int {
        return selectedIndex
    }

    fun getSelectedItem(): DayItems? {
        if (selectedIndex != RecyclerView.NO_POSITION) {
            return days.getOrNull(selectedIndex)
        }
        return null
    }
}
data class DayItems(
    val dayOfTheMonth:Int,
    val dayOfTheWeek: String
)

class PitchTimesAdapter(private val times: List<TimeItems>):RecyclerView.Adapter<PitchTimesAdapter.TimesViewHolder>(){
    inner class TimesViewHolder(itemView: View):RecyclerView.ViewHolder(itemView)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TimesViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_pitch_times, parent, false)
        return TimesViewHolder(view)
    }
    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: TimesViewHolder, position: Int) {
        val currentItem = times[position]
        holder.itemView.apply {
            findViewById<TextView>(R.id.tvDatesTimes).text = currentItem.time
            val bookedStatus = findViewById<TextView>(R.id.tvDatesBookedStatus)
            if (currentItem.status){
                bookedStatus.text = "Booked"
                bookedStatus.setTextColor(ContextCompat.getColor(context, R.color.red))
            }else{
                bookedStatus.text = "Available"
                bookedStatus.setTextColor(ContextCompat.getColor(context, R.color.white))
            }
        }
    }
    override fun getItemCount(): Int {
        return times.size
    }
}

data class TimeItems(
    val time: String,
    val status: Boolean
)