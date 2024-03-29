package com.fluture.pruvve

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.fluture.pruvve.databinding.ActivitySearchPageBinding

class SearchPage : AppCompatActivity() {
    private lateinit var binding: ActivitySearchPageBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchPageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnBack.setOnClickListener {
            finish()
        }

        val searches = mutableListOf(
            SearchItems("Word", "what is english and what is gibberish"),SearchItems("Hilton Stadium", "in a time"),
            SearchItems("shit", "a word to display dismay"),SearchItems("Word", "astafrujallah"),
            SearchItems("with", "nothing is ever wonderful alone"),SearchItems("Historim Aduferus", "in hommine religious"),
            SearchItems("how", "time sure is something"),SearchItems("Future", "time has come"),
            SearchItems("Old trafford", "the joy of nations"),SearchItems("battle", "The moment is neigh"),
            SearchItems("realistic pitch", "the future is definetly one for exploration"),SearchItems("Question", "what in reality is a word"),
            SearchItems("Word", "shirweifhdffdufdgfhdfiudfjd"),SearchItems("vanacular", "a shrewd statement"),
            SearchItems("the Kings", "realigouos"),SearchItems("Stadium diffurus", "we differentiate ourselves from the rest through our skills"),

        )
        val adapter = SearchAdapter(searches)
        val recycler = binding.rvSearches
        recycler.adapter = adapter
        recycler.layoutManager = LinearLayoutManager(this@SearchPage, LinearLayoutManager.VERTICAL, false)
    }
}