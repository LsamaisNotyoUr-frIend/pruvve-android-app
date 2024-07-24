package com.fluture.pruvve.fragments

import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import com.fluture.pruvve.R
import com.fluture.pruvve.databinding.FragmentAddStoryBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddStoryFragment : Fragment(R.layout.fragment_add_story) {
    private lateinit var binding: FragmentAddStoryBinding
    private val REQUEST_IMAGE_CAPTURE = 1
    private var imageUri: Uri? = null
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding = FragmentAddStoryBinding.bind(view)
        super.onViewCreated(view, savedInstanceState)
        binding.VDismiss.setOnClickListener {
            val fragmentManager = requireActivity().supportFragmentManager
            val currentFragment = fragmentManager.findFragmentByTag("AddStoryFragment")
            currentFragment?.let {
                fragmentManager.beginTransaction().remove(it).commit()
            }
        }
        binding.wvPosts.loadUrl("https://www.pinterest.com/pin/13299761394513929/")
    }
}