package com.fluture.pruvve

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.View
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.fluture.pruvve.adapters.DayItems
import com.fluture.pruvve.adapters.GetUserResponse
import com.fluture.pruvve.adapters.PitchDatesAdapter
import com.fluture.pruvve.adapters.PitchTimesAdapter
import com.fluture.pruvve.adapters.TimeItems
import com.fluture.pruvve.auth.AuthInterceptor
import com.fluture.pruvve.auth.LoginManager
import com.fluture.pruvve.databinding.ActivityBookingPitchesBinding
import com.fluture.pruvve.retrofittcalls.GetPitch
import com.fluture.pruvve.retrofittcalls.GetPitchAvailability
import com.fluture.pruvve.retrofittcalls.PitchRequestObjects
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
        val webView = binding.wvMakePayment
        webView.visibility =View.GONE
        val pitchId = intent.getIntExtra("Extra_id", 2)
        val leftArrow = binding.imvPitchDateLeft
        val rightArrow = binding.imvPitchDateRight
        val monthYearTextView = binding.tvPitchDate
        val recyclerViewDates = binding.rvPitchDateDays
        val recyclerViewTimes = binding.rvPitchDaysTime

        val pitchName = intent.getStringExtra("Extra_pitch_name") ?: "provingGrounds"

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

        var userId = 3
        service.getUserCredentials().enqueue(object : Callback<GetUserResponse>{
            override fun onResponse(call: Call<GetUserResponse>, response: Response<GetUserResponse>) {
                if (response.isSuccessful){
                    userId = response.body()?.data?.id!!
                }else{
                    logError(response.errorBody()?.toString(), "Couldn't get user")
                }
            }
            override fun onFailure(call: Call<GetUserResponse>, t: Throwable) {
                logFailure(t, "Couldn't reach the server")
            }
        })

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
        val timeIndex = PitchDatesAdapter(dataList).getSelectedDateString()

        service.checkAvailability(pitchId, timeIndex!!).enqueue(object : Callback<GetPitchAvailability>{
            override fun onResponse(call: Call<GetPitchAvailability>, response: Response<GetPitchAvailability>
            ) {
                if (response.isSuccessful){
                    val list =response.body()?.data?.slots
                    if (list != null){
                        for (timeItems in list){
                            val slotsLeft = timeItems.slotsLeft
                            val timesList = TimeItems(timeItems.startTime, timeItems.endTime, getStatus(slotsLeft), pitchId)
                            dataList2.add(timesList)
                        }
                        val adapter = PitchTimesAdapter(dataList2)
                        recyclerViewTimes.adapter = adapter
                        recyclerViewTimes.layoutManager = LinearLayoutManager(this@BookingPitches, LinearLayoutManager.VERTICAL, false)
                        binding.btnPitchDays.setOnClickListener{
                            val selectedTimes = adapter.getSelectedTimes()
                            val bookingReference = createBookingReference(pitchName, userId)
                            val pitchRequestObjects = PitchRequestObjects(
                                selectedTimes, bookingReference)
                            service.bookPitch(pitchId, pitchRequestObjects).enqueue(object: Callback<GetPitch>{
                                @SuppressLint("SetJavaScriptEnabled")
                                override fun onResponse(
                                    call: Call<GetPitch>,
                                    response: Response<GetPitch>
                                ) {
                                    if (response.isSuccessful){
                                       webView.visibility = View.VISIBLE
                                        val url = "https://pruvve-pay-ab34e3cd9a37.herokuapp.com/?token=$token&bookingReference=$bookingReference"
                                        val webSettings: WebSettings = webView.settings
                                        webSettings.javaScriptEnabled = true
                                        webSettings.domStorageEnabled = true
                                        webSettings.useWideViewPort = true
                                        webSettings.loadWithOverviewMode = true
                                        WebView.setWebContentsDebuggingEnabled(true)
                                        webView.webViewClient = object : WebViewClient() {
                                            override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean {
                                                view.loadUrl(request.url.toString())
                                                return true
                                            }
                                        }
                                        webView.loadUrl(url)
                                    }else{
                                        logError(response.errorBody().toString(), "couldn't book the pitch")
                                    }
                                }
                                override fun onFailure(call: Call<GetPitch>, t: Throwable) {
                                    logFailure(t, "Unable to reach the server at this moment")
                                }
                            })
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

    private fun getStatus(availableSlots: Int):Boolean{
        return availableSlots == 0
    }
        private fun createBookingReference(pitchName: String, userId: Int):String{
        val currentTimeMillis = System.currentTimeMillis()
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(
            Date(currentTimeMillis)
        )
        return "${pitchName}_${timeStamp}_${userId}"
    }

    private fun logError(errorBody: String?, message: String) {
        Log.e("Error", "$message: $errorBody")
    }

    private fun logFailure(t: Throwable, message: String) {
        Log.e("Failure", "$message: ${t.message}")
    }
}