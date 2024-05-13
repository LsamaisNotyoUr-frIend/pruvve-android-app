package com.fluture.pruvve

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.constraintlayout.widget.ConstraintLayout
import com.fluture.pruvve.databinding.ActivityViewPitchItemBinding

class ViewPitchItem : AppCompatActivity() {
    private lateinit var binding: ActivityViewPitchItemBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityViewPitchItemBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        binding.btnBackButoon.setOnClickListener {
            finish()
        }
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
                    startActivity(it)

                }
            }
        }
    }
}