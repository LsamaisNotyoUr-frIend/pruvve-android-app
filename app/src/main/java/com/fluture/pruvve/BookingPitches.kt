package com.fluture.pruvve

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import com.fluture.pruvve.adapters.DayItems
import com.fluture.pruvve.adapters.PitchDatesAdapter
import com.fluture.pruvve.adapters.PitchTimesAdapter
import com.fluture.pruvve.adapters.TimeItems
import com.fluture.pruvve.databinding.ActivityBookingPitchesBinding
import java.time.LocalDate
import java.time.Year
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale
import kotlin.random.Random

class BookingPitches : AppCompatActivity() {
    private lateinit var binding: ActivityBookingPitchesBinding
    private var currentMonthYear: LocalDate = LocalDate.now()
    private lateinit var pitchDatesAdapter: PitchDatesAdapter
    private var dataList = mutableListOf<DayItems>()
    private var dataList2 = mutableListOf<TimeItems>()

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityBookingPitchesBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val leftArrow = binding.imvPitchDateLeft
        val rightArrow = binding.imvPitchDateRight
        val monthYearTextView = binding.tvPitchDate
        val recyclerViewDates = binding.rvPitchDateDays
        val recyclerViewTimes = binding.rvPitchDaysTime

        pitchDatesAdapter = PitchDatesAdapter(dataList)
        recyclerViewDates.adapter = pitchDatesAdapter
        recyclerViewDates.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        updateMonthYearTextView(monthYearTextView)
        updateRecyclerViewForCurrentMonth()

        leftArrow.setOnClickListener {
            currentMonthYear = currentMonthYear.minusMonths(1)
            updateMonthYearTextView(monthYearTextView)
            updateRecyclerViewForCurrentMonth()
        }

        rightArrow.setOnClickListener {
            currentMonthYear = currentMonthYear.plusMonths(1)
            updateMonthYearTextView(monthYearTextView)
            updateRecyclerViewForCurrentMonth()
        }
        val timeIndex = PitchDatesAdapter(dataList).getSelectedIndex()
        for (i in 1..timeIndex){
            val randomBoolean = Random.nextBoolean()
            val placeholderList = listOf(
                TimeItems("9:00 - 10:00", randomBoolean), TimeItems("10:00 - 11:00", randomBoolean),TimeItems("11:00 - 12:00", randomBoolean),
                TimeItems("12:00 - 13:00", randomBoolean), TimeItems("13:00 - 14:00", randomBoolean), TimeItems("14:00 - 15:00", randomBoolean),
                TimeItems("15:00 - 16:00", randomBoolean), TimeItems("16:00 - 17:00", randomBoolean), TimeItems("17:00 - 18:00", randomBoolean)
            )
            dataList2.addAll(placeholderList)
        }

        recyclerViewTimes.adapter = PitchTimesAdapter(dataList2)
        recyclerViewTimes.layoutManager = LinearLayoutManager(this@BookingPitches, LinearLayoutManager.VERTICAL, false)
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun updateRecyclerViewForCurrentMonth() {
        dataList.clear()
        dataList.addAll(updateDateListForCurrentMonth())
        pitchDatesAdapter.notifyDataSetChanged()
    }

    private fun updateDateListForCurrentMonth(): List<DayItems> {
        val currentMonth = currentMonthYear.month
        val currentYear = currentMonthYear.year
        val daysInMonth = currentMonth.length(Year.isLeap(currentYear.toLong()))
        val dataList = mutableListOf<DayItems>()
        for (dayOfMonth in 1..daysInMonth) {
            val currentDate = LocalDate.of(currentYear, currentMonth, dayOfMonth)
            val dayOfWeek = currentDate.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault())

            val dayItem = DayItems(dayOfMonth, dayOfWeek)
            dataList.add(dayItem)
        }
        return dataList
    }

    private fun updateMonthYearTextView(textView: TextView) {
        val formattedDate = currentMonthYear.format(DateTimeFormatter.ofPattern("MMMM yyyy"))
        textView.text = formattedDate
    }
}