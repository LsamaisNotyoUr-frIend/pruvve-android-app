package com.fluture.pruvve

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.fluture.pruvve.adapters.SearchAdapter
import com.fluture.pruvve.adapters.SearchItems
import com.fluture.pruvve.databinding.ActivitySearchPageBinding
import com.fluture.pruvve.essentials.TextManager
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SearchPage : AppCompatActivity(){
    private lateinit var binding: ActivitySearchPageBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        TextManager.init(this)
        binding = ActivitySearchPageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnBack.setOnClickListener {
            finish()
        }

        val searches = mutableListOf(
            SearchItems("Word", "what is english and what is gibberish"),
            SearchItems("Hilton Stadium", "in a time"),
            SearchItems("shit", "a word to display dismay"),
            SearchItems("Word", "astafrujallah"),
            SearchItems("with", "nothing is ever wonderful alone"),
            SearchItems("Historim Aduferus", "in hommine religious"),
            SearchItems("how", "time sure is something"),
            SearchItems("Future", "time has come"),
            SearchItems("Old trafford", "the joy of nations"),
            SearchItems("battle", "The moment is neigh"),
            SearchItems("realistic pitch", "the future is definetly one for exploration"),
            SearchItems("Question", "what in reality is a word"),
            SearchItems("Word", "shirweifhdffdufdgfhdfiudfjd"),
            SearchItems("vanacular", "a shrewd statement"),
            SearchItems("the Kings", "realigouos"),
            SearchItems("Stadium diffurus", "we differentiate ourselves from the rest through our skills")
        )
        val adapter = SearchAdapter(searches,this@SearchPage)
        val recycler = binding.rvSearches
        recycler.adapter = adapter
        recycler.layoutManager = LinearLayoutManager(this@SearchPage, LinearLayoutManager.VERTICAL, false)

        binding.etSearchBar.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Not used
            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val searchText = s.toString().lowercase()

                val filteredSearches = searches.filter { it.title.lowercase()
                    .contains(searchText) }

                val sortedFilteredSearches = filteredSearches.sortedByDescending { it.title.count { char -> char in searchText } }

                adapter.setData(sortedFilteredSearches)
            }

            override fun afterTextChanged(s: Editable?) {
                val searchText = s.toString().lowercase()
                TextManager.saveText(searchText)
            }
        })
    }
}