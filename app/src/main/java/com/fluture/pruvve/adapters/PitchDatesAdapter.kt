package com.fluture.pruvve.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.fluture.pruvve.R
import com.fluture.pruvve.retrofittcalls.PitchTimes
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class PitchDatesAdapter(private var days:List<DayItems>):RecyclerView.Adapter<PitchDatesAdapter.DaysViewHolder>() {
    private var selectedIndex: Int = RecyclerView.NO_POSITION

    inner class DaysViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
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

    override fun onBindViewHolder(holder: DaysViewHolder, @SuppressLint("RecyclerView") position: Int) {
        val currentItem = days[position]
        holder.itemView.apply {
            findViewById<TextView>(R.id.tvDaysOfMonth).text = currentItem.dayOfTheMonth.toString()
            findViewById<TextView>(R.id.tvDatesOfWeek).text = currentItem.dayOfTheWeek
            findViewById<View>(R.id.llDates).setBackgroundResource(
                if (holder.adapterPosition == selectedIndex) {
                    R.drawable.dates_backgrounds
                } else R.drawable.dates_backgrounds2
            )
            setOnClickListener {
                notifyItemChanged(selectedIndex)
                selectedIndex = position
                notifyItemChanged(selectedIndex)
            }
        }
    }

    fun getSelectedItem(): DayItems? {
        return if (selectedIndex != RecyclerView.NO_POSITION) {
            days.getOrNull(selectedIndex)
        } else null
    }

    fun getSelectedDateString(): String? {
        return getSelectedItem()?.let {
            formatDate(it.year, it.month, it.dayOfTheMonth)
        }
    }

    private fun formatDate(year: Int, month: Int, day: Int): String {
        return LocalDate.of(year, month, day).format(DateTimeFormatter.ISO_DATE)
    }

    fun updateDays(newDays: List<DayItems>) {
        days = newDays
        notifyDataSetChanged()
    }
}

// File path: DayItems.kt

data class DayItems(
    val dayOfTheMonth: Int,
    val dayOfTheWeek: String,
    val year: Int,
    val month: Int
)

class PitchTimesAdapter(private val times: List<TimeItems>):RecyclerView.Adapter<PitchTimesAdapter.TimesViewHolder>(){
    private val selectedItems = mutableListOf<TimeItems>()
    var clickedBoolean = false
    inner class TimesViewHolder(itemView: View):RecyclerView.ViewHolder(itemView)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TimesViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_pitch_times, parent, false)
        return TimesViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: TimesViewHolder, position: Int) {

        val currentItem = times[position]
        holder.itemView.apply {
            findViewById<TextView>(R.id.tvDatesTimes).text = currentItem.startTime
            val bookedStatus = findViewById<TextView>(R.id.tvDatesBookedStatus)

            if (currentItem.status) {
                bookedStatus.text = "Full Booked"
                bookedStatus.setTextColor(ContextCompat.getColor(context, R.color.red))
            } else {
                bookedStatus.text = "Available"
                bookedStatus.setTextColor(ContextCompat.getColor(context, R.color.white))
            }

            setOnClickListener {
                if (!currentItem.status) { // Only selectable if not booked
                    if (selectedItems.contains(currentItem)) {
                        selectedItems.remove(currentItem)
                        setBackgroundResource(R.drawable.profile_backgrounds2)
                    } else {
                        selectedItems.add(currentItem)
                        setBackgroundResource(R.drawable.primary_button)
                        clickedBoolean = true
                    }
                }
            }

            // Ensure correct background for selected items
            if (selectedItems.contains(currentItem)) {
                setBackgroundResource(R.drawable.primary_button)
            } else {
                setBackgroundResource(R.drawable.profile_backgrounds2)
            }
        }

    }
    override fun getItemCount(): Int {
        return times.size
    }

    fun getSelectedTimes(): List<PitchTimes> {
        return selectedItems.map { PitchTimes(it.startTime, it.endTime) }
    }
    fun ifClicked():Boolean {
        return clickedBoolean
    }

    fun getSelectedTimesCount(): Int {
        return selectedItems.size
    }
}

data class TimeItems(
    val startTime: String,
    val endTime: String,
    var status: Boolean,
    val id: Int,
    var isSelected: Boolean = false
)