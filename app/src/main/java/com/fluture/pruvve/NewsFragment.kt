package com.fluture.pruvve

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.fluture.pruvve.databinding.FragmentNewsBinding

class NewsFragment : Fragment(R.layout.fragment_news) {
    private lateinit var binding: FragmentNewsBinding
    @SuppressLint("NotifyDataSetChanged")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding = FragmentNewsBinding.bind(view)
        super.onViewCreated(view, savedInstanceState)
        val url = "https://archive.org/details/my-character-design-references-for-you"

        val newsArticles = mutableListOf(
            NewsItems(url, "Anxiety Spreads Will the Pacers Lose their Titles", "5seconds ago", "After the Pacers crushing defeat " +
                    "against the Eaters it is unknown whether they shall be able to rise again stay tuned for more news like these ones"),
            NewsItems(url, "Is the former sports prodigy retiring", "1 days ago", "A 12 year career and now a retirement notice" +
                    "is this the end for Luis Santiago"),
            NewsItems(url, "A heart breaking injury", "1 weeks ago", "One of the rising champs of the younger generations" +
                    "Joao Cancelo has one of the greatest injuries of his career after his match with Real Madrid"),
            NewsItems(url, "A New Star?", "25 minutes ago", "Manchester city is setting its sites on some new players as they" +
                    "attempt to sign Josko Gvardiol, Matheus Nunes, jeremy Doku, And Mateo Kovacic, will this be a new beginning or a waste of funds"),
            NewsItems(url, "The end of the power struggle", "2 hours ago", "Manchester united has finally bested city and there no more" +
                    "debate about which is the better club"),
            NewsItems(url, "A new prodigy", "1 months ago", "After the retirement of messi and ronaldo outside the premier league" +
                    "and laliga the question remains who shall be the next big name is it Mbabe? is it Neymar some even say it's Haaland "),
        )

        val newsRecycler = binding.rvNews
        val adapter = NewsAdapter(newsArticles)
        newsRecycler.adapter = adapter
        newsRecycler.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        adapter.notifyDataSetChanged()

        binding.button1.setOnClickListener {
            requireActivity().finish()
        }

    }
}