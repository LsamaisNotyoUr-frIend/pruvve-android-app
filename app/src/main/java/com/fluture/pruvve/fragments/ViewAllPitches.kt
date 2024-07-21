package com.fluture.pruvve.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.fluture.pruvve.R
import com.fluture.pruvve.databinding.FragmentViewAllPitchesBinding

class ViewAllPitches : Fragment(R.layout.fragment_view_all_pitches) {
    private lateinit var binding: FragmentViewAllPitchesBinding
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding = FragmentViewAllPitchesBinding.bind(view)
        super.onViewCreated(view, savedInstanceState)

    }
}