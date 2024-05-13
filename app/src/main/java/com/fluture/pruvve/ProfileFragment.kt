package com.fluture.pruvve

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fluture.pruvve.adapters.VideoPageItems
import com.fluture.pruvve.adapters.VideosPageAdapter1
import com.fluture.pruvve.databinding.FragmentProfileBinding

class ProfileFragment : Fragment(R.layout.fragment_profile) {
    private lateinit var binding: FragmentProfileBinding
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding = FragmentProfileBinding.bind(view)
        super.onViewCreated(view, savedInstanceState)
        val myUrl = "https://i.pinimg.com/236x/5a/6b/ea/5a6beaca00190835c3ba144424156afb.jpg"
        val myUrl2 = "https://i.pinimg.com/236x/6b/38/ef/6b38ef66e69c53fc92a56766ff56adff.jpg"

        var isClicked = false
        var anyViewClicked = false
        binding.tvProfileAge.setOnClickListener { anyViewClicked = true }
        binding.tvProfileGender.setOnClickListener { anyViewClicked = true }
        binding.tvProfileUserName1.setOnClickListener { anyViewClicked = true }

        val teamImage = "https://i.pinimg.com/236x/9c/bf/d3/9cbfd3f0540ecfda99178f81cb932299.jpg"
        val thisURL = "https://i.pinimg.com/236x/8f/9d/94/8f9d94ba59ecfc35bd129af0ca8a7f70.jpg"
        val profilePic = "https://i.pinimg.com/236x/bf/6e/b1/bf6eb11bb813a9cd90e6cfb6eff8515a.jpg"
        binding.wvTeamPfp.loadUrl(teamImage)
        binding.wvProfileStartVideo.loadUrl(thisURL)
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
        val recycler2 = binding.rvPrpfileRecycler2

        val scrollListener = object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (recyclerView == recycler1) {
                    recycler2.scrollBy(dx, dy)
                } else if (recyclerView == recycler2) {
                    recycler1.scrollBy(dx, dy)
                }
            }
        }

        recycler1.addOnScrollListener(scrollListener)
        recycler2.addOnScrollListener(scrollListener)

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
            val videos1 = mutableListOf(
                VideoPageItems(myUrl),
                VideoPageItems(myUrl2),
                VideoPageItems(thisURL),
                VideoPageItems(myUrl),
                VideoPageItems(myUrl2),
                VideoPageItems(myUrl),
                VideoPageItems(myUrl2),
                VideoPageItems(thisURL),
                VideoPageItems(myUrl2)
            )
            val videos2 = mutableListOf(
                VideoPageItems(thisURL),
                VideoPageItems(myUrl),
                VideoPageItems(myUrl2),
                VideoPageItems(myUrl),
                VideoPageItems(thisURL),
                VideoPageItems(myUrl),
                VideoPageItems(myUrl),
                VideoPageItems(myUrl2),
                VideoPageItems(myUrl)
            )
            val adapter1 = VideosPageAdapter1(videos1)
            val adapter2 = VideosPageAdapter1(videos2)
            recycler2.adapter = adapter2
            recycler1.adapter = adapter1
            recycler1.layoutManager = LinearLayoutManager(requireContext())
            recycler2.layoutManager = LinearLayoutManager(requireContext())
        }

        mediaPosts.setOnClickListener {
            profilePosts.setBackgroundResource(R.color.dark_gray)
            mediaPosts.setBackgroundResource(R.drawable.primary_button)

            val videos1 = mutableListOf(
                VideoPageItems(myUrl),
                VideoPageItems(myUrl),
                VideoPageItems(myUrl),
                VideoPageItems(myUrl),
                VideoPageItems(myUrl),
                VideoPageItems(myUrl),
                VideoPageItems(myUrl),
                VideoPageItems(myUrl),
                VideoPageItems(myUrl)
            )
            val videos2 = mutableListOf(
                VideoPageItems(myUrl2),
                VideoPageItems(myUrl2),
                VideoPageItems(myUrl2),
                VideoPageItems(myUrl2),
                VideoPageItems(myUrl2),
                VideoPageItems(myUrl2),
                VideoPageItems(myUrl2),
                VideoPageItems(myUrl2),
                VideoPageItems(myUrl2)
            )
            val adapter1 = VideosPageAdapter1(videos1)
            val adapter2 = VideosPageAdapter1(videos2)
            recycler2.adapter = adapter1
            recycler1.adapter = adapter2
        }

        val videos1 = mutableListOf(
            VideoPageItems(myUrl),
            VideoPageItems(myUrl2),
            VideoPageItems(thisURL),
            VideoPageItems(myUrl),
            VideoPageItems(myUrl2),
            VideoPageItems(myUrl),
            VideoPageItems(myUrl2),
            VideoPageItems(thisURL),
            VideoPageItems(myUrl2)
        )
        val videos2 = mutableListOf(
            VideoPageItems(thisURL),
            VideoPageItems(myUrl),
            VideoPageItems(myUrl2),
            VideoPageItems(myUrl),
            VideoPageItems(thisURL),
            VideoPageItems(myUrl),
            VideoPageItems(myUrl),
            VideoPageItems(myUrl2),
            VideoPageItems(myUrl)
        )
        val adapter1 = VideosPageAdapter1(videos1)
        val adapter2 = VideosPageAdapter1(videos2)
        recycler2.adapter = adapter2
        recycler1.adapter = adapter1
        recycler1.layoutManager = LinearLayoutManager(requireContext())
        recycler2.layoutManager = LinearLayoutManager(requireContext())
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
}