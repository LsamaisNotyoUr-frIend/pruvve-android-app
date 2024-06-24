package com.fluture.pruvve

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import com.fluture.pruvve.adapters.DayItems
import com.fluture.pruvve.adapters.PitchDatesAdapter
import com.fluture.pruvve.adapters.PitchTimesAdapter
import com.fluture.pruvve.adapters.TimeItems
import com.fluture.pruvve.auth.AuthInterceptor
import com.fluture.pruvve.auth.LoginManager
import com.fluture.pruvve.databinding.ActivityBookingPitchesBinding
import com.fluture.pruvve.retrofittcalls.GetPitchAvailability
import com.fluture.pruvve.retrofittcalls.UserService
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.Year
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Date
import java.util.Locale
import java.util.TimeZone
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
        LoginManager.init(this)
        setContentView(binding.root)
        val pitchId = intent.getIntExtra("Extra_id", 2)
        val leftArrow = binding.imvPitchDateLeft
        val rightArrow = binding.imvPitchDateRight
        val monthYearTextView = binding.tvPitchDate
        val recyclerViewDates = binding.rvPitchDateDays
        val recyclerViewTimes = binding.rvPitchDaysTime

        val token = LoginManager.getToken()
        Log.d("RetrofitToken", token.toString())

        val httpClient = OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor(token.toString()))
            .build()

        val service = Retrofit.Builder()
            .baseUrl("https://pruvve-backend-9a89de78d2a1.herokuapp.com/api/")
            .client(httpClient)
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
            .create(UserService::class.java)

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
        val dateString = PitchDatesAdapter(dataList).getSelectedDate().currentDate.format(
            DateTimeFormatter.ISO_DATE
        )

        service.checkAvailability(pitchId, dateString).enqueue(object : Callback<GetPitchAvailability>{
            override fun onResponse(call: Call<GetPitchAvailability>, response: Response<GetPitchAvailability>
            ) {
                if (response.isSuccessful){
                    val list =response.body()?.data?.slots
                    if (list != null){
                        for (timeItems in list){
                            val timesList = TimeItems(convertToTimeRange(timeItems.startTime, timeItems.endTime), false)
                            dataList2.add(timesList)
                            if (dataList2.size == (list.size)+1){
                                recyclerViewTimes.adapter = PitchTimesAdapter(dataList2)
                                recyclerViewTimes.layoutManager = LinearLayoutManager(this@BookingPitches, LinearLayoutManager.VERTICAL, false)
                            }
                        }
                    }
                }else{
                    Log.e("RetrofitError", "An error occurred ${response.errorBody().toString()}")
                }
            }
            override fun onFailure(call: Call<GetPitchAvailability>, t: Throwable) {
                Log.e("RetrofitFailure", "couldn't reach the server ${t.message.toString()}")
            }
        })
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

            val dayItem = DayItems(dayOfMonth, dayOfWeek, currentDate)
            dataList.add(dayItem)
        }
        return dataList
    }

    private fun updateMonthYearTextView(textView: TextView) {
        val formattedDate = currentMonthYear.format(DateTimeFormatter.ofPattern("MMMM yyyy"))
        textView.text = formattedDate
    }

    fun convertToTimeRange(dateStr1: String, dateStr2: String): String {

        val isoFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault()).apply {
            timeZone = TimeZone.getTimeZone("UTC")
        }
        val outputFormat = SimpleDateFormat("H:mm", Locale.getDefault())

        // Parse the input date strings to Date objects
        val date1: Date = isoFormat.parse(dateStr1) ?: throw IllegalArgumentException("Invalid date string: $dateStr1")
        val date2: Date = isoFormat.parse(dateStr2) ?: throw IllegalArgumentException("Invalid date string: $dateStr2")

        // Format the Date objects to the desired time format
        val time1: String = outputFormat.format(date1)
        val time2: String = outputFormat.format(date2)

        // Combine the formatted times into the desired range format
        return "$time1 to $time2"
    }
}