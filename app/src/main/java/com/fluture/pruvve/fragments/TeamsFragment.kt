package com.fluture.pruvve.fragments

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.fluture.pruvve.AddPlayer
import com.fluture.pruvve.R
import com.fluture.pruvve.adapters.TeamMates
import com.fluture.pruvve.adapters.TeamsAdapter
import com.fluture.pruvve.auth.AuthInterceptor
import com.fluture.pruvve.auth.LoginManager
import com.fluture.pruvve.databinding.FragmentTeamsBinding
import com.fluture.pruvve.retrofittcalls.GetSpecificTeam
import com.fluture.pruvve.retrofittcalls.UploadImage
import com.fluture.pruvve.retrofittcalls.UploadResponse
import com.fluture.pruvve.retrofittcalls.UserService
import com.fluture.pruvve.retrofittcalls.teamMembers
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

class TeamsFragment : Fragment(R.layout.fragment_teams) {
    private lateinit var binding: FragmentTeamsBinding
    @SuppressLint("NotifyDataSetChanged")
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

        val teamId = arguments?.getInt("teamId", 5) ?: 5

        service.getTeamById(teamId).enqueue(object : Callback<GetSpecificTeam>{
            override fun onResponse(call: Call<GetSpecificTeam>, response: Response<GetSpecificTeam>
            ) {
                if (response.isSuccessful){
                    binding.tvTeamAssigned.text = response.body()?.data?.name
                    val signedUrl = response.body()?.data?.profilePictureUrl ?: "https://i.pinimg.com/236x/4e/80/50/4e80508b0f22dfc42ce98bb8d0acb563.jpg"
                    val uploadPic =  UploadImage(signedUrl, "DOWNLOAD")
                    service.uploadPicture(uploadPic).enqueue(object : Callback<UploadResponse>{
                        override fun onResponse(
                            call: Call<UploadResponse>,
                            response: Response<UploadResponse>
                        ) {
                            if (response.isSuccessful){
                                Glide.with(requireContext())
                                    .load(response.body()?.data)
                                    .apply(RequestOptions().centerCrop())
                                    .into(binding.wvTeamMembersPfp)
                            }else{
                                Log.e("RetrofitError", "There was a problem fetching your url")
                            }
                        }

                        override fun onFailure(call: Call<UploadResponse>, t: Throwable) {
                            Log.e("RetrofitFailure", "could no reach the server")
                        }
                    })
                    for (teammate in response.body()?.data?.teamMembers!!) {
                        when (teammate.position) {
                            "Goalkeeper(GK)" -> setUpGoaleeRecycler(service, teammate)
                            "Center-back(CB)", "Left-back(LB)", "Right-back(RB)" -> setUpDefenderRecycler(service, teammate)
                            "Defensive midfielder (DM)", "Central midfielder(CM)", "Attacking midfielder(AM)" -> setUpMidfielderRecycler(service, teammate)
                            "Striker(ST)", "Left-winger(LW)", "Right-winger(RW)" -> setUpStrikerRecycler(service, teammate)
                        }
                    }
                }else{
                    Log.e("RetrofitError", "couldn't find team name ${response.errorBody()?.toString()}")
                }
            }
            override fun onFailure(call: Call<GetSpecificTeam>, t: Throwable) {
                Log.e("RetrofitFailure", "Couldn't reach the server ${t.message.toString()}")
            }
        })

        binding.nsvTeamMates.visibility = View.VISIBLE
        binding.nsvTeamMates.isClickable = true
        binding.nsvTeamMates.isNestedScrollingEnabled = true

        binding.imvAddPlayer.setOnClickListener {
            Intent(requireContext(), AddPlayer::class.java).also {
                startActivity(it)
            }
        }
    }

    private fun setUpGoaleeRecycler(service:UserService, teammate: teamMembers){
        val uploadPic =  UploadImage(teammate.profilePictureUrl, "DOWNLOAD")
        val goalkeepers = mutableListOf<TeamMates>()

        val goalieRecycler = binding.rvGoallees

        val goalieAdapter = TeamsAdapter(goalkeepers)

        val linearLayout = LinearLayoutManager(requireContext())
        service.uploadPicture(uploadPic).enqueue(object: Callback<UploadResponse>{
            @SuppressLint("NotifyDataSetChanged")
            override fun onResponse(
                call: Call<UploadResponse>,
                response: Response<UploadResponse>
            ) {
                if (response.isSuccessful){
                    goalkeepers.add(TeamMates(teammate.profilePictureUrl, teammate.username, teammate.position, teammate.preferredFoot))
                    goalieRecycler.adapter = goalieAdapter
                    goalieRecycler.layoutManager = linearLayout
                    goalieAdapter.notifyDataSetChanged()

                }else{
                    Log.e("RetrofitError", "could not make the request ${response.errorBody().toString()}")
                }
            }
            override fun onFailure(call: Call<UploadResponse>, t: Throwable) {
                Log.e("RetrofitFailure", "could not make the request ${t.message.toString()}")
            }
        })
    }

    private fun setUpMidfielderRecycler(service:UserService, teammate: teamMembers){
        val uploadPic =  UploadImage(teammate.profilePictureUrl, "DOWNLOAD")
        val midfielders = mutableListOf<TeamMates>()
        val midfielderRecycler = binding.rvMidfielders
        val midfielderAdapter= TeamsAdapter(midfielders)
        val linearLayout = LinearLayoutManager(requireContext())
        service.uploadPicture(uploadPic).enqueue(object: Callback<UploadResponse>{
            @SuppressLint("NotifyDataSetChanged")
            override fun onResponse(
                call: Call<UploadResponse>,
                response: Response<UploadResponse>
            ) {
                if (response.isSuccessful){
                    midfielders.add(TeamMates(teammate.profilePictureUrl, teammate.username, teammate.position, teammate.preferredFoot))
                    midfielderRecycler.adapter = midfielderAdapter
                    midfielderRecycler.layoutManager = linearLayout
                    midfielderAdapter.notifyDataSetChanged()
                }else{
                    Log.e("RetrofitError", "could not make the request ${response.errorBody().toString()}")
                }
            }
            override fun onFailure(call: Call<UploadResponse>, t: Throwable) {
                Log.e("RetrofitFailure", "could not make the request ${t.message.toString()}")
            }
        })
    }

    private fun setUpStrikerRecycler(service:UserService, teammate: teamMembers){
        val uploadPic =  UploadImage(teammate.profilePictureUrl, "DOWNLOAD")
        val strikers = mutableListOf<TeamMates>()
        val strikerRecycler = binding.rvStrikes
        val strikerAdapter = TeamsAdapter(strikers)
        val linearLayout = LinearLayoutManager(requireContext())
        service.uploadPicture(uploadPic).enqueue(object: Callback<UploadResponse>{
            @SuppressLint("NotifyDataSetChanged")
            override fun onResponse(
                call: Call<UploadResponse>,
                response: Response<UploadResponse>
            ) {
                if (response.isSuccessful){
                    strikers.add(TeamMates(teammate.profilePictureUrl, teammate.username, teammate.position, teammate.preferredFoot))
                    strikerRecycler.adapter = strikerAdapter
                    strikerRecycler.layoutManager = linearLayout
                    strikerAdapter.notifyDataSetChanged()
                }else{
                    Log.e("RetrofitError", "could not make the request ${response.errorBody().toString()}")
                }
            }
            override fun onFailure(call: Call<UploadResponse>, t: Throwable) {
                Log.e("RetrofitFailure", "could not make the request ${t.message.toString()}")
            }
        })
    }

    private fun setUpDefenderRecycler(service:UserService, teammate: teamMembers){
        val uploadPic =  UploadImage(teammate.profilePictureUrl, "DOWNLOAD")
        val defenders = mutableListOf<TeamMates>()
        val defenderRecycler= binding.rvDefenders
        val defenderAdapter =TeamsAdapter(defenders)
        val linearLayout = LinearLayoutManager(requireContext())
        service.uploadPicture(uploadPic).enqueue(object: Callback<UploadResponse>{
            @SuppressLint("NotifyDataSetChanged")
            override fun onResponse(
                call: Call<UploadResponse>,
                response: Response<UploadResponse>
            ) {
                if (response.isSuccessful){
                    defenders.add(TeamMates(teammate.profilePictureUrl, teammate.username, teammate.position, teammate.preferredFoot))
                    defenderRecycler.adapter = defenderAdapter
                    defenderRecycler.layoutManager = linearLayout
                    defenderAdapter.notifyDataSetChanged()

                }else{
                    Log.e("RetrofitError", "could not make the request ${response.errorBody().toString()}")
                }
            }
            override fun onFailure(call: Call<UploadResponse>, t: Throwable) {
                Log.e("RetrofitFailure", "could not make the request ${t.message.toString()}")
            }
        })
    }
}