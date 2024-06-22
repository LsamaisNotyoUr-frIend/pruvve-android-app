package com.fluture.pruvve.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.fluture.pruvve.R
import com.fluture.pruvve.adapters.VideoPageItems
import com.fluture.pruvve.adapters.VideosPageAdapter1
import com.fluture.pruvve.databinding.FragmentVideoScreenFragmentsBinding

class VideoScreenFragments : Fragment(R.layout.fragment_video_screen_fragments) {
    private lateinit var binding: FragmentVideoScreenFragmentsBinding
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding = FragmentVideoScreenFragmentsBinding.bind(view)
        super.onViewCreated(view, savedInstanceState)
        val myUrl = "https://i.pinimg.com/236x/aa/4f/55/aa4f55ccb2674bd8ca76053f2b5bab60.jpg"
        val myUrl2 = "https://i.pinimg.com/236x/cc/0c/af/cc0caf4f332b9925d89f0a5d6f8f87e0.jpg"

        val videos1Recycler  = binding.rvVideos1
        binding.wvFunny.loadUrl(myUrl)
        binding.wvGoals.loadDataWithBaseURL(myUrl, "<style>html, body { width: 100%; height: 100%; margin: 0; padding: 0; }</style>",  "text/html", "UTF-8", null)
        binding.wvSkills.loadUrl(myUrl)
        Glide.with(requireContext())
            .load(myUrl)
            .into(binding.wvTopVideo)
        val videos1 = mutableListOf(
            VideoPageItems(myUrl),
            VideoPageItems(myUrl2),
            VideoPageItems(myUrl),
            VideoPageItems(myUrl),
            VideoPageItems(myUrl2),
            VideoPageItems(myUrl),
            VideoPageItems(myUrl2),
            VideoPageItems(myUrl),
            VideoPageItems(myUrl2),
            VideoPageItems(myUrl2),
            VideoPageItems(myUrl),
            VideoPageItems(myUrl2),
            VideoPageItems(myUrl),
            VideoPageItems(myUrl2),
            VideoPageItems(myUrl),
            VideoPageItems(myUrl),
            VideoPageItems(myUrl2),
            VideoPageItems(myUrl)
        )
        val adapter1 = VideosPageAdapter1(videos1)
        val layoutManager = GridLayoutManager(requireContext(), 2, GridLayoutManager.VERTICAL, false)
        videos1Recycler.adapter = adapter1
        videos1Recycler.layoutManager = layoutManager
    }
}