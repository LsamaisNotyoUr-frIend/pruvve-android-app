package com.fluture.pruvve

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.fluture.pruvve.auth.AuthInterceptor
import com.fluture.pruvve.auth.LoginManager
import com.fluture.pruvve.databinding.ActivityViewPitchItemBinding
import com.fluture.pruvve.retrofittcalls.GetPitch
import com.fluture.pruvve.retrofittcalls.UserService
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

class ViewPitchItem : AppCompatActivity() {
    private lateinit var binding: ActivityViewPitchItemBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityViewPitchItemBinding.inflate(layoutInflater)
        LoginManager.init(this@ViewPitchItem)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

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

        val pitchId = intent.getIntExtra("Extra_id", 2)
        binding.btnBackButoon.setOnClickListener {
            finish()
        }
        val id = intent.getIntExtra("Extra_id", 5)
        val descriptions = intent.getStringExtra("Extra_description") ?: "where the master make their name known"
        val title = intent.getStringExtra("Extra_title") ?: "proving grounds"
        val location = intent.getStringExtra("Extra_description") ?: "Off breach side avenue, right of olusegun road"

        service.getPitchById(id).enqueue(object: Callback<GetPitch> {
            override fun onResponse(call: Call<GetPitch>, response: Response<GetPitch>) {
                if (response.isSuccessful){
                    Log.d("RetrofitSuccess", "Pitch has been gotten from the server successfully")
                    val data = response.body()?.data
                    getRating(data?.rating!!)
                    returnAllFacilities(data.hasFreeParking, data.hasChangingRoom , data.hasFloodLights)
                    updateFeatures(data.surface , data.format)
                    binding.tvPrice.text = data.price.toString()
                }else{
                    Log.e("RetrofitError", "There was a problem getting the pitch from the server")
                }
            }
            override fun onFailure(call: Call<GetPitch>, t: Throwable) {
                Log.e("RetrofitFailure", "Could not reach the server")
            }
        })

        binding.tvDescription.text = descriptions
        binding.tvPitchName.text = title
        binding.tvPitchLocation.text = location


        var clickNumber = 0
        binding.wvViewPitches.loadUrl("https://i.pinimg.com/564x/10/63/58/106358e2a6f2b341468d494158dfe4cc.jpg")
        binding.btnPitchLetGo.setOnClickListener {
            clickNumber ++
            val params = binding.guideline289.layoutParams as ConstraintLayout.LayoutParams
            val params2 = binding.guideline290.layoutParams as ConstraintLayout.LayoutParams
            val params3 = binding.guideline291.layoutParams as ConstraintLayout.LayoutParams
            val params4 = binding.guideline294.layoutParams as ConstraintLayout.LayoutParams
            val params5 = binding.guideline295.layoutParams as ConstraintLayout.LayoutParams
            val params6 = binding.guideline299.layoutParams as ConstraintLayout.LayoutParams
            val params7 = binding.guideline300.layoutParams as ConstraintLayout.LayoutParams
            val params8 = binding.guideline292.layoutParams as ConstraintLayout.LayoutParams
            val params9 = binding.guideline302.layoutParams as ConstraintLayout.LayoutParams
            val params10 = binding.guideline305.layoutParams as ConstraintLayout.LayoutParams
            val params11 = binding.guideline306.layoutParams as ConstraintLayout.LayoutParams
            params.guidePercent = 0.15f
            params2.guidePercent = 0.08f
            params3.guidePercent = 0.05f
            params4.guidePercent = 0.09f
            params5.guidePercent = 0.13f
            params6.guidePercent = 0.18f
            params7.guidePercent = 0.23f
            params8.guidePercent = 0.35f
            params9.guidePercent = 0.905f
            params10.guidePercent = 0.73f
            params11.guidePercent = 0.78f
            binding.guideline289.layoutParams = params
            binding.guideline290.layoutParams = params2
            binding.guideline291.layoutParams = params3
            binding.guideline294.layoutParams = params4
            binding.guideline295.layoutParams = params5
            binding.guideline299.layoutParams = params6
            binding.guideline300.layoutParams = params7
            binding.guideline292.layoutParams = params8
            binding.guideline302.layoutParams = params9
            binding.guideline305.layoutParams = params10
            binding.guideline306.layoutParams = params11
            if (clickNumber == 2){
                clickNumber = 0
                Intent(this@ViewPitchItem, BookingPitches::class.java).also {
                    intent.putExtra("Extra_id", pitchId)
                    startActivity(it)
                }
            }
        }
    }
    private fun updateFeatures(surface: String, format:String){
        binding.tvPitchSurface.text = surface
        binding.tvPitchFormat.text = format
    }
    private fun returnAllFacilities(bool1: Boolean, bool2: Boolean, bool3:Boolean){
        returnFacilities1(bool1)
        returnFacilities2(bool2)
        returnFacilities3(bool3)
    }
    private fun returnFacilities1(boolean: Boolean){
        val stringToReturn:String = if (boolean) "Free Parking" else "empty"

        binding.tvPitchFeatures1.text = stringToReturn
    }

    private fun returnFacilities2(boolean: Boolean){
        val stringToReturn = if (boolean) "Changing room" else "empty"

        binding.tvPitchFeatures2.text = stringToReturn
    }

    private fun returnFacilities3(boolean: Boolean){
        val stringToReturn = if (boolean) "Floodlights" else "empty"
        binding.tvPitchFeatures3.text = stringToReturn
    }

    private fun getRating(rating: Int){
        val stars = listOf(binding.ivStar1, binding.ivStar2,
            binding.ivStar3, binding.ivStar4, binding.ivStar5)
        stars.forEachIndexed { index, imageView ->
            imageView.visibility = if (index < rating) View.VISIBLE else View.INVISIBLE
        }
    }
}