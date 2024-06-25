package com.fluture.pruvve

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
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
import java.time.LocalDate
import java.time.Year
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

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
        val timeIndex = PitchDatesAdapter(dataList).getSelectedIndex()

        service.checkAvailability(pitchId, "2024-06-26").enqueue(object : Callback<GetPitchAvailability>{
            override fun onResponse(call: Call<GetPitchAvailability>, response: Response<GetPitchAvailability>
            ) {
                if (response.isSuccessful){
                    val list =response.body()?.data?.slots
                    if (list != null){
                        for (timeItems in list){
                            val timesList = TimeItems(timeItems.startTime, timeItems.endTime, false)
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