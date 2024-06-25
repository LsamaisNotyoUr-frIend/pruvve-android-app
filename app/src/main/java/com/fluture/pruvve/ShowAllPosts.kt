package com.fluture.pruvve

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.ui.AppBarConfiguration
import androidx.recyclerview.widget.GridLayoutManager
import com.fluture.pruvve.adapters.PitchAdapter
import com.fluture.pruvve.adapters.Pitches
import com.fluture.pruvve.auth.AuthInterceptor
import com.fluture.pruvve.auth.LoginManager
import com.fluture.pruvve.databinding.ActivityShowAllPostsBinding
import com.fluture.pruvve.retrofittcalls.GetPitches
import com.fluture.pruvve.retrofittcalls.RequestObjects
import com.fluture.pruvve.retrofittcalls.UserService
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

class ShowAllPosts : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityShowAllPostsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        LoginManager.init(this)
        binding = ActivityShowAllPostsBinding.inflate(layoutInflater)
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
    }
    private fun getPitches(service: UserService){
        val requestObject = RequestObjects(1, 10)
        val pitchList = ArrayList<Pitches>()
        service.getPitches(requestObject).enqueue(object : Callback<GetPitches> {
            override fun onResponse(call: Call<GetPitches>, response: Response<GetPitches>) {
                if (response.isSuccessful){
                    val list = response.body()?.data?.list
                    if (list != null){
                        for (pitchItems in list){
                            val profUrl = "https://i.pinimg.com/236x/b4/ab/a3/b4aba328a23ff375814d694409a52fa7.jpg"
                            val format = pitchItems.format
                            val surface = pitchItems.surface
                            val facilities = returnFacilities1(pitchItems.hasFreeParking)
                            val facilities2 = returnFacilities2(pitchItems.hasChangingRoom)
                            val facilities3 = returnFacilities3(pitchItems.hasFloodLights)
                            val title = pitchItems.title
                            val address = pitchItems.address
                            val pitches = Pitches(title, address, profUrl, 2, format, surface, facilities, facilities2, facilities3, pitchItems.id, pitchItems.description)
                            pitchList.add(pitches)
                            if (pitchList.size == list.size){
                                val adapter = PitchAdapter(pitchList)
                                val recycler = binding.rvPitches
                                recycler.adapter = adapter
                                recycler.layoutManager = GridLayoutManager(this@ShowAllPosts, 2)

                                binding.etSearchBar.addTextChangedListener(object : TextWatcher {
                                    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                                        TODO("Not yet implemented")
                                    }

                                    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                                        val searchText = s.toString().lowercase()

                                        val filteredSearches = pitchList.filter { it.title.lowercase().contains(searchText) }
                                        val sortedFilteredSearches = ArrayList(filteredSearches.sortedByDescending { it.title.count { char -> char in searchText } })

                                        adapter.setData(sortedFilteredSearches)
                                    }

                                    override fun afterTextChanged(s: Editable?) {
                                        TODO("Not yet implemented")
                                    }
                                })
                            }
                        }
                    }else{
                        Log.e("RetrofitListError", "your list is empty")
                    }
                }else{
                    Log.e("RetrofitError", "there was an error ${response.errorBody().toString()}")
                }
            }

            override fun onFailure(call: Call<GetPitches>, t: Throwable) {
                Log.e("RetrofitFailure", "couldn't reach the server ${t.message.toString()}")
            }
        })
    }
    private fun returnFacilities1(boolean: Boolean):String{
        val stringToReturn:String = if (boolean){
            "Free Parking"
        }else{
            "empty"
        }
        return stringToReturn
    }
    private fun returnFacilities2(boolean: Boolean):String{
        val stringToReturn = if (boolean){
            "Changing room"
        }else{
            "empty"
        }
        return stringToReturn
    }
    private fun returnFacilities3(boolean: Boolean):String{
        val stringToReturn = if (boolean) "Floodlights" else "empty"
        return stringToReturn
    }
}