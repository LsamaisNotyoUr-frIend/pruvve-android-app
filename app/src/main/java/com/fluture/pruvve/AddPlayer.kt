package com.fluture.pruvve

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.fluture.pruvve.adapters.AddPlayerItems
import com.fluture.pruvve.adapters.AddPlayersAdapter
import com.fluture.pruvve.adapters.GetAllUserResponse
import com.fluture.pruvve.auth.AuthInterceptor
import com.fluture.pruvve.auth.LoginManager
import com.fluture.pruvve.databinding.ActivityAddPlayerBinding
import com.fluture.pruvve.retrofittcalls.UserRequestObjects
import com.fluture.pruvve.data.api.UserService
import dagger.hilt.android.AndroidEntryPoint
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

@AndroidEntryPoint
class AddPlayer : AppCompatActivity() {
    private lateinit var binding: ActivityAddPlayerBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityAddPlayerBinding.inflate(layoutInflater)
        LoginManager.init(this@AddPlayer)
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

        binding.button1.setOnClickListener {
            finish() }

        val requestObjects = UserRequestObjects(1, 10, "Admin79", "ATHLETE")
        service.getAllUsers(requestObjects).enqueue(object: Callback<GetAllUserResponse>{
            override fun onResponse(call: Call<GetAllUserResponse>, response: Response<GetAllUserResponse>) {
                if (response.isSuccessful){
                    val playerList= mutableListOf(AddPlayerItems("empty", "George", "Left", "Winger"))
                    val list = response.body()?.data?.list
                    if (list!= null){
                        for (users in list){
                            val playerListToAdd = AddPlayerItems(users.profilePictureUrl, users.username, "left", "Striker")
                            playerList.add(playerListToAdd)
                            if (playerList.size == list.size){
                                val adapter = AddPlayersAdapter(playerList)
                                binding.rvPlayerSearches.adapter = adapter
                                binding.rvPlayerSearches.layoutManager = LinearLayoutManager(this@AddPlayer)
                                adapter.notifyItemRangeInserted(0, playerList.size)
                            }
                        }
                    }
                }else{
                    Log.e("RetrofitError", "there was an error in the code ${response.errorBody().toString()}")
                }
            }
            override fun onFailure(call: Call<GetAllUserResponse>, t: Throwable) {
                Log.e("RetrofitFailure", "problem reaching the server ${t.message.toString()}")
            }
        })
    }
}