package com.fluture.pruvve

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.fluture.pruvve.adapters.TeamMates
import com.fluture.pruvve.adapters.TeamsAdapter
import com.fluture.pruvve.auth.AuthInterceptor
import com.fluture.pruvve.auth.LoginManager
import com.fluture.pruvve.databinding.FragmentTeamsBinding
import com.fluture.pruvve.retrofittcalls.GetSpecificTeam
import com.fluture.pruvve.retrofittcalls.UserService
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.Random

class TeamsFragment : Fragment(R.layout.fragment_teams) {
    private lateinit var binding: FragmentTeamsBinding
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding = FragmentTeamsBinding.bind(view)
        super.onViewCreated(view, savedInstanceState)
        LoginManager.init(requireContext())
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
        val myUrl = "https://i.pinimg.com/236x/4e/80/50/4e80508b0f22dfc42ce98bb8d0acb563.jpg"
        val myUrl2 = "https://i.pinimg.com/236x/0e/88/20/0e8820df856a51cdcb7a791396846421.jpg"
        val thisURL = "https://i.pinimg.com/236x/c8/71/8e/c8718e9e41758a502a709765792b7de3.jpg"
        val teamId = arguments?.getInt("teamId", 5) ?: 5

        service.getTeamById(teamId).enqueue(object : Callback<GetSpecificTeam>{
            override fun onResponse(call: Call<GetSpecificTeam>, response: Response<GetSpecificTeam>
            ) {
                if (response.isSuccessful){
                    binding.tvTeamAssigned.text = response.body()?.data?.name
                }else{
                    Log.e("RetrofitError", "couldn't find team name ${response.errorBody()?.toString()}")
                }
            }
            override fun onFailure(call: Call<GetSpecificTeam>, t: Throwable) {
                Log.e("RetrofitFailure", "Couldn't reach the server ${t.message.toString()}")
            }
        })
        binding.wvTeamMembersPfp.loadUrl(myUrl)
        val goalkeepers = binding.rvGoallees
        val defenders = binding.rvDefenders
        val midFielders = binding.rvMidfielders
        val strikers = binding.rvStrikes



        binding.nsvTeamMates.visibility = View.VISIBLE
        binding.nsvTeamMates.isClickable = true
        binding.nsvTeamMates.isNestedScrollingEnabled = true

        binding.imvAddPlayer.setOnClickListener {
            Intent(requireContext(), AddPlayer::class.java).also {
                startActivity(it)
            }
        }
        val goalkeeppers = "GoalKeeper"
        val striker = getRandom(listOf("Centre forward", "Supporting forward", "Right winger", "Left winger")).toString()
        val defender = getRandom(listOf("Centre Back", "Full back")).toString()
        val midfield = getRandom(listOf("Defensive Midfielder", "Central Midfielder ", "Attacking Midfielder")).toString()
        val legs = getRandom(listOf("Left leg", "Right leg")).toString()
        val goalees = mutableListOf(
            TeamMates(thisURL, "Mahmud", goalkeeppers, legs),
            TeamMates(thisURL, "Mnar", goalkeeppers, legs),
            TeamMates(thisURL, "wint", goalkeeppers, legs),
            TeamMates(thisURL, "kyle", goalkeeppers, legs),
            TeamMates(thisURL, "ron", goalkeeppers, legs)
        )
        val goaleeAdapter = TeamsAdapter(goalees)
        goalkeepers.adapter = goaleeAdapter
        goalkeepers.layoutManager = LinearLayoutManager(requireContext())

        val defenderss = mutableListOf(
            TeamMates(myUrl,"wiz",defender, legs),
            TeamMates(myUrl,"Mike",defender, legs),
            TeamMates(myUrl,"daryl",defender, legs),
            TeamMates(myUrl,"joshua",defender, legs)
        )
        val defenderAdapter = TeamsAdapter(defenderss)
        defenders.adapter = defenderAdapter
        defenders.layoutManager = LinearLayoutManager(requireContext())

        val midfieldersList = mutableListOf(
            TeamMates(myUrl2, "Bruno",midfield, legs),
            TeamMates(myUrl2, "Cuanto",midfield, legs),
            TeamMates(myUrl2, "Davidson",midfield, legs),
            TeamMates(myUrl2, "Maguire",midfield, legs),
            TeamMates(myUrl2, "Ceaser",midfield, legs),
            TeamMates(myUrl2, "Casemiro",midfield, legs),
            TeamMates(myUrl2, "Paul",midfield, legs),
        )
        val midfielderAdapter = TeamsAdapter(midfieldersList)
        midFielders.adapter = midfielderAdapter
        midFielders.layoutManager = LinearLayoutManager(requireContext())

        val strikerList = mutableListOf(
            TeamMates(thisURL, "Harry kane", striker, legs),
            TeamMates(thisURL, "Rashford", striker, legs),
            TeamMates(thisURL, "Hazard", striker, legs),
            TeamMates(thisURL, "Haaland", striker, legs),
            TeamMates(thisURL, "Messi", striker, legs),
            TeamMates(thisURL, "Ronaldo", striker, legs),
            TeamMates(thisURL, "Pogba", striker, legs),
        )
        val strikerAdapter = TeamsAdapter(strikerList)
        strikers.adapter = strikerAdapter
        strikers.layoutManager = LinearLayoutManager(requireContext())
    }
    private fun getRandom(list: List<String>): String? {
        if (list.isNotEmpty()) {
            val random = Random()
            val randomIndex = random.nextInt(list.size)
            return list[randomIndex]
        }
        return null
    }
}