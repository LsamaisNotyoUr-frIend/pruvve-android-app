package com.fluture.pruvve.fragments

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.NumberPicker
import android.widget.PopupWindow
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fluture.pruvve.R
import com.fluture.pruvve.TeamsPage
import com.fluture.pruvve.TeamsPage2
import com.fluture.pruvve.adapters.GetUserResponse
import com.fluture.pruvve.adapters.VideoPageItems
import com.fluture.pruvve.adapters.VideosPageAdapter1
import com.fluture.pruvve.auth.AuthInterceptor
import com.fluture.pruvve.auth.LoginManager
import com.fluture.pruvve.databinding.FragmentProfileBinding
import com.fluture.pruvve.retrofittcalls.GetAllPosts
import com.fluture.pruvve.retrofittcalls.GetAthleteProfile
import com.fluture.pruvve.retrofittcalls.GetFeedsMedia
import com.fluture.pruvve.retrofittcalls.UploadImage
import com.fluture.pruvve.retrofittcalls.UploadResponse
import com.fluture.pruvve.retrofittcalls.UserService
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.time.LocalDate
import java.time.Period
import java.time.format.DateTimeFormatter

class ProfileFragment : Fragment(R.layout.fragment_profile) {
    private lateinit var binding: FragmentProfileBinding
    private val defaultUrl = "https://i.pinimg.com/236x/8f/9d/94/8f9d94ba59ecfc35bd129af0ca8a7f70.jpg"
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding = FragmentProfileBinding.bind(view)
        LoginManager.init(requireContext())
        super.onViewCreated(view, savedInstanceState)
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
        service.getUserCredentials().enqueue(object :Callback<GetUserResponse>{
            override fun onResponse(call: Call<GetUserResponse>, response: Response<GetUserResponse>) {
                if (response.isSuccessful){
                    Log.d("RetrofitSuccess", "Successfully gotten the user's information")
                    val user = response.body()?.data
                    binding.tvProfileUserName1.text = user?.username
                    binding.tvProfileGender.text = user?.gender
                    binding.tvProfileGender.text = calculateAge(user?.dateOfBirth ?: "2004-05-20" ).toString()

                }else {
                    Log.e("RetrofitError","Couldn't get the users information${response.errorBody().toString()}")
                }
            }
            override fun onFailure(call: Call<GetUserResponse>, t: Throwable) {
                Log.e("RetrofitFailure", "Couldn't reach the server ${t.message.toString()}")
            }
        })
        service.getAthleteProfile().enqueue(object : Callback<GetAthleteProfile>{
            override fun onResponse(
                call: Call<GetAthleteProfile>,
                response: Response<GetAthleteProfile>
            ) {
                if (response.isSuccessful){
                    Log.d("RetrofitSuccess", "Successfully gotten the athlete's information")
                    val athlete = response.body()?.data
                    binding.tvPreferredLeg.text = athlete?.preferredFoot
                    binding.tvPosition.text = athlete?.position
                    binding.tvheight.text = athlete?.height
                    binding.tvBioText.text = athlete?.bio
                }else {
                    Log.e("RetrofitError","Couldn't get the athlete's information${response.errorBody().toString()}")
                }
            }

            override fun onFailure(call: Call<GetAthleteProfile>, t: Throwable) {
                Log.e("RetrofitFailure", "Couldn't reach the server${t.message.toString()}")
            }
        })
        val myUrl = "https://i.pinimg.com/236x/5a/6b/ea/5a6beaca00190835c3ba144424156afb.jpg"
        val myUrl2 = "https://i.pinimg.com/236x/6b/38/ef/6b38ef66e69c53fc92a56766ff56adff.jpg"
        val userId = arguments?.getInt("userId", 5) ?: 5

        val gridLayout = GridLayoutManager(requireContext(), 2, GridLayoutManager.VERTICAL, false)
        var isClicked = false
        var anyViewClicked = false
        binding.tvProfileAge.setOnClickListener { anyViewClicked = true }
        binding.tvProfileGender.setOnClickListener { anyViewClicked = true }
        binding.tvProfileUserName1.setOnClickListener { anyViewClicked = true }

        val teamImage = "https://i.pinimg.com/236x/9c/bf/d3/9cbfd3f0540ecfda99178f81cb932299.jpg"
        val profilePic = "https://i.pinimg.com/236x/bf/6e/b1/bf6eb11bb813a9cd90e6cfb6eff8515a.jpg"
        binding.wvTeamPfp.loadUrl(teamImage)
        binding.wvProfileStartVideo.loadUrl(defaultUrl)
        binding.wvPfp.loadUrl(profilePic)
        binding.wvRandomPfp1.loadUrl(profilePic)
        binding.wvRandomPfp2.loadUrl(profilePic)
        binding.wvRandomPfp3.loadUrl(profilePic)

        binding.llMyteam.setOnClickListener{
           showDialogue()
        }
        binding.nsvProfile.isNestedScrollingEnabled = false
        binding.imvSettingIcon.setOnClickListener {
            showPopUpDialogue()
        }
        val profilePosts = binding.tvProfilePosts
        val mediaPosts = binding.tvProfileMedia
        val recycler1 = binding.rvPrpfileRecycler

        binding.tvMoreDetails.setOnClickListener {
            if(isClicked){
                val params = binding.guideline207.layoutParams as ConstraintLayout.LayoutParams
                val params2 = binding.guideline221.layoutParams as ConstraintLayout.LayoutParams
                val params3 = binding.guideline224.layoutParams as ConstraintLayout.LayoutParams
                params.guidePercent = 0.000f
                params2.guidePercent = 0.187f
                params3.guidePercent = 0.23f
                binding.guideline221.layoutParams = params2
                binding.guideline224.layoutParams = params3
                binding.guideline207.layoutParams = params
                binding.tvMoreDetails.isVisible = false
                binding.vMoreDetailsBackgrounder.setBackgroundResource(R.drawable.more_details_background)
                binding.nsvProfile.isNestedScrollingEnabled = true
            }else{
                if(anyViewClicked){
                    val params = binding.guideline207.layoutParams as ConstraintLayout.LayoutParams
                    val params2 = binding.guideline221.layoutParams as ConstraintLayout.LayoutParams
                    val params3 = binding.guideline224.layoutParams as ConstraintLayout.LayoutParams
                    params.guidePercent = 0.58f
                    params2.guidePercent = 0.195f
                    params3.guidePercent = 0.235f
                    binding.guideline221.layoutParams = params2
                    binding.guideline224.layoutParams = params3
                    binding.guideline207.layoutParams = params
                    binding.tvMoreDetails.isVisible = true
                    binding.vMoreDetailsBackgrounder.setBackgroundResource(R.drawable.profile_backgrounds2)
                    binding.nsvProfile.isNestedScrollingEnabled = false
                }
            }
            anyViewClicked = false
            isClicked = !isClicked
        }

        profilePosts.setOnClickListener {
            profilePosts.setBackgroundResource(R.drawable.primary_button)
            mediaPosts.setBackgroundResource(R.color.dark_gray)
            val videos = mutableListOf(VideoPageItems(myUrl), VideoPageItems(myUrl2), VideoPageItems(defaultUrl),
                VideoPageItems(myUrl), VideoPageItems(myUrl2), VideoPageItems(myUrl), VideoPageItems(myUrl2), VideoPageItems(defaultUrl),
                VideoPageItems(myUrl2), VideoPageItems(defaultUrl), VideoPageItems(myUrl), VideoPageItems(myUrl2), VideoPageItems(myUrl),
                VideoPageItems(defaultUrl), VideoPageItems(myUrl), VideoPageItems(myUrl), VideoPageItems(myUrl2), VideoPageItems(myUrl)
            )
            getUsersPosts(service, userId, recycler1, videos, gridLayout)
        }

        mediaPosts.setOnClickListener {
            profilePosts.setBackgroundResource(R.color.dark_gray)
            mediaPosts.setBackgroundResource(R.drawable.primary_button)
            val videos = mutableListOf(VideoPageItems(myUrl), VideoPageItems(myUrl), VideoPageItems(myUrl), VideoPageItems(myUrl),
                VideoPageItems(myUrl), VideoPageItems(myUrl), VideoPageItems(myUrl), VideoPageItems(myUrl), VideoPageItems(myUrl), VideoPageItems(myUrl2),
                VideoPageItems(myUrl2), VideoPageItems(myUrl2), VideoPageItems(myUrl2), VideoPageItems(myUrl2), VideoPageItems(myUrl2), VideoPageItems(myUrl2),
                VideoPageItems(myUrl2), VideoPageItems(myUrl2)
            )
            getMediaPosts(service, recycler1, videos, gridLayout)
        }

        val videos1 = mutableListOf(VideoPageItems(myUrl), VideoPageItems(myUrl2),
            VideoPageItems(defaultUrl), VideoPageItems(myUrl), VideoPageItems(myUrl2), VideoPageItems(myUrl),
            VideoPageItems(myUrl2), VideoPageItems(defaultUrl), VideoPageItems(myUrl2), VideoPageItems(defaultUrl),
            VideoPageItems(myUrl), VideoPageItems(myUrl2), VideoPageItems(myUrl), VideoPageItems(defaultUrl),
            VideoPageItems(myUrl), VideoPageItems(myUrl), VideoPageItems(myUrl2), VideoPageItems(myUrl))
        getMediaPosts(service, recycler1, videos1, gridLayout)
    }
    @SuppressLint("InflateParams")
    private fun showPopUpDialogue() {
        val inflater = LayoutInflater.from(requireContext())
        val popupView = inflater.inflate(R.layout.dialogue_messages_settings, null)

        val popupWindow = PopupWindow(popupView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true)
        val colorInt = 0xFFCDCDCD.toInt()
        val colorDrawable = ColorDrawable(colorInt)
        popupWindow.setBackgroundDrawable(colorDrawable)
        popupWindow.showAtLocation(popupView, Gravity.CENTER, 0, 0)
        popupView.findViewById<TextView>(R.id.tvSettingOfMs).setOnClickListener {
            popupView.findViewById<TextView>(R.id.tvSettingOfMs).setBackgroundResource(R.color.dark_gray)
            popupWindow.dismiss()
        }

        popupView.findViewById<TextView>(R.id.tvSAndMs).setOnClickListener {
            popupView.findViewById<TextView>(R.id.tvSAndMs).setBackgroundResource(R.color.dark_gray)
            popupWindow.dismiss()
        }

        val settingIcon = binding.imvSettingIcon
        val iconLocation = IntArray(2)
        settingIcon.getLocationInWindow(iconLocation)
        popupWindow.showAtLocation(settingIcon, Gravity.START and Gravity.TOP,0,0)
    }
    private fun showDialogue(){
        val dialogView = layoutInflater.inflate(R.layout.gender_picker, null)
        val builder = AlertDialog.Builder(requireContext())
        builder.setView(dialogView)
        val dialog = builder.create()

        val genderPicker: NumberPicker = dialogView.findViewById(R.id.genderPicker)
        val genderLabel: TextView = dialogView.findViewById(R.id.genderLabel)
        val gender = arrayOf("Teams1","Teams2")
        genderPicker.minValue = 0
        genderPicker.maxValue = 1
        genderPicker.displayedValues = gender
        genderPicker.setOnValueChangedListener { _, _, newVal ->
            genderLabel.text = gender[newVal]
        }

        dialog.setOnDismissListener {
            if(gender[genderPicker.value] == "Teams1"){
                Intent(requireContext(), TeamsPage::class.java).also {
                    it.putExtra("ExtraTeamName", genderLabel.text)
                    startActivity(it)
                }
            }else{
                Intent(requireContext(), TeamsPage2::class.java).also {
                    it.putExtra("ExtraTeamName", genderLabel.text)
                    startActivity(it)
                }
            }
        }
        val window = dialog.window
        window?.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT)
        window?.setGravity(Gravity.BOTTOM)
        dialog.show()
    }

    private fun getUsersPosts(service: UserService,  userId:Int,  recyclerView: RecyclerView,
    videoList: MutableList<VideoPageItems>, gridLayout: GridLayoutManager, defaultUrl:String = "https://i.pinimg.com/236x/8f/9d/94/8f9d94ba59ecfc35bd129af0ca8a7f70.jpg"){
        val listToUpload = mutableListOf(VideoPageItems(defaultUrl))
        service.getMyPosts(userId, GetFeedsMedia(1, 10)).enqueue(object : Callback<GetAllPosts>{
            override fun onResponse(call: Call<GetAllPosts>, response: Response<GetAllPosts>) {
                if (response.isSuccessful){
                    Log.d("RetrofitSuccess", "Successfully got the posts")
                    val listOfPosts = response.body()?.data?.list
                    if (listOfPosts != null){
                        for (usersPosts in listOfPosts){
                            val downloadImage = UploadImage(
                                fileName = usersPosts.mediaUrl,
                                purpose = "DOWNLOAD"
                            )
                            listToUpload.add(VideoPageItems(downloadMedia(service, downloadImage)))
                            if (listToUpload.size == listOfPosts.size + 1){
                                val adapter = VideosPageAdapter1(listToUpload)
                                recyclerView.adapter = adapter
                                recyclerView.layoutManager = gridLayout
                                adapter.notifyItemRangeInserted(0, listToUpload.size)
                            }
                        }
                    }else{
                        val adapter = VideosPageAdapter1(videoList)
                        recyclerView.adapter = adapter
                        recyclerView.layoutManager = gridLayout
                        adapter.notifyItemRangeInserted(0, videoList.size)
                    }
                }else{
                    Log.e("RetrofitError","Problem making request${response.errorBody().toString()}")
                }
            }
            override fun onFailure(call: Call<GetAllPosts>, t: Throwable) {
                Log.e("RetrofitFailure", "Could not reach the server ${t.message.toString()}")
            }
        })
    }

    private fun getMediaPosts(service: UserService,recyclerView: RecyclerView, videoList: MutableList<VideoPageItems>,gridLayout: GridLayoutManager){
        val listToUpload = mutableListOf(VideoPageItems(defaultUrl))
        service.getPosts(GetFeedsMedia(1, 10)).enqueue(object : Callback<GetAllPosts>{
            override fun onResponse(call: Call<GetAllPosts>, response: Response<GetAllPosts>) {
                if (response.isSuccessful){
                    Log.d("RetrofitSuccess", "Successfully got the posts")
                    val listOfPosts = response.body()?.data?.list
                    if (listOfPosts != null){
                        for (posts in listOfPosts){
                            val downloadImage = UploadImage(
                                fileName = posts.mediaUrl,
                                purpose = "DOWNLOAD"
                            )
                            listToUpload.add(VideoPageItems(downloadMedia(service, downloadImage)))
                            if (listToUpload.size == listOfPosts.size + 1){
                                val adapter = VideosPageAdapter1(listToUpload)
                                recyclerView.adapter = adapter
                                recyclerView.layoutManager = gridLayout
                                adapter.notifyItemRangeInserted(0, listToUpload.size)
                            }
                        }
                    }else{
                        val adapter = VideosPageAdapter1(videoList)
                        recyclerView.adapter = adapter
                        recyclerView.layoutManager = gridLayout
                        adapter.notifyItemRangeInserted(0, videoList.size)
                    }
                }else{
                    Log.e("RetrofitError","Problem making request${response.errorBody().toString()}")
                }
            }
            override fun onFailure(call: Call<GetAllPosts>, t: Throwable) {
                Log.e("RetrofitFailure", "Could not reach the server ${t.message.toString()}")
            }
        })
    }


    private fun downloadMedia(service: UserService, downloadMedia: UploadImage):String{
        var url = ""
        service.uploadPicture(downloadMedia).enqueue(object : Callback<UploadResponse>{
            override fun onResponse(
                call: Call<UploadResponse>,
                response: Response<UploadResponse>
            ) {
                if (response.isSuccessful){
                    Log.d("RetrofitSuccess", "Urls gotten successfully")
                    url = response.body()?.data ?: "https://i.pinimg.com/236x/9c/bf/d3/9cbfd3f0540ecfda99178f81cb932299.jpg"
                }else{
                    Log.e("RetrofitError","Problem making request for Urls${response.errorBody().toString()}")
                }
            }
            override fun onFailure(call: Call<UploadResponse>, t: Throwable) {
                Log.e("RetrofitFailure", "Could not reach the server ${t.message.toString()}")
            }
        })
        return url
    }

    private fun calculateAge(birthDate: String): Int {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val birth = LocalDate.parse(birthDate, formatter)
        val current = LocalDate.now()
        return Period.between(birth, current).years
    }
}