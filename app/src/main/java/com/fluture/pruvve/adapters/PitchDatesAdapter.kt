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
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class PitchDatesAdapter(private val days:List<DayItems>):RecyclerView.Adapter<PitchDatesAdapter.DaysViewHolder>() {
    private var selectedIndex: Int = RecyclerView.NO_POSITION
    private var currentDateItem: DayItems? = null

    init {
        val currentCalendar = Calendar.getInstance()
        val currentDate = currentCalendar[Calendar.DAY_OF_MONTH]
        val currentMonth = currentCalendar[Calendar.MONTH] + 1
        val currentYear = currentCalendar[Calendar.YEAR]

        for ((index, item) in days.withIndex()) {
            if (item.dayOfTheMonth == currentDate && item.month == currentMonth) {
                selectedIndex = index
                currentDateItem = item
                break
            }
        }
    }

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

    override fun onBindViewHolder(holder: DaysViewHolder, position: Int) {
        val currentItem = days[position]
        holder.itemView.apply {
            findViewById<TextView>(R.id.tvDaysOfMonth).text = currentItem.dayOfTheMonth.toString()
            findViewById<TextView>(R.id.tvDatesOfWeek).text = currentItem.dayOfTheWeek
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

    fun getCurrentDateString(): String? {
        currentDateItem?.let {
            return formatDate(it.year, it.month, it.dayOfTheMonth)
        }
        return null
    }

    fun getSelectedDateString(): String? {
        val selectedItem = getSelectedItem()
        selectedItem?.let {
            return formatDate(it.year, it.month, it.dayOfTheMonth)
        }
        return null
    }

    private fun formatDate(year: Int, month: Int, day: Int): String {
        val calendar = Calendar.getInstance()
        calendar.set(year, month - 1, day)
        val dateFormat = SimpleDateFormat("yy,MMdd", Locale.getDefault())
        return dateFormat.format(calendar.time)
    }

}
data class DayItems(
    val dayOfTheMonth:Int,
    val dayOfTheWeek: String,
    val year: Int,
    val month: Int,
)

class PitchTimesAdapter(private val times: List<TimeItems>):RecyclerView.Adapter<PitchTimesAdapter.TimesViewHolder>(){
    private val selectedItems = mutableListOf<TimeItems>()
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