package com.fluture.pruvve.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.fluture.pruvve.R
import com.fluture.pruvve.adapters.NewsAdapter
import com.fluture.pruvve.adapters.NewsItems
import com.fluture.pruvve.databinding.FragmentNewsBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NewsFragment : Fragment(R.layout.fragment_news) {
    private lateinit var binding: FragmentNewsBinding
    @SuppressLint("NotifyDataSetChanged")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding = FragmentNewsBinding.bind(view)
        super.onViewCreated(view, savedInstanceState)
        val url = "https://i.pinimg.com/236x/a2/32/72/a232720babae9453035611dbd7b5603e.jpg"
        val url2 = "https://i.pinimg.com/236x/a4/83/12/a4831235241145e32470658539599ba5.jpg"
        val url3 = "https://i.pinimg.com/236x/49/1c/57/491c5746be59ac0872252fa6498191ce.jpg"
        binding.wvNewsMain.loadUrl(url)
        binding.wvSideNews1.loadUrl(url2)
        binding.wvSideNews2.loadUrl(url3)

        val newsArticles = mutableListOf(
            NewsItems(url, "Anxiety Spreads Will the Pacers Lose their Titles", "5seconds ago", "After the Pacers crushing defeat " +
                    "against the Eaters it is unknown whether they shall be able to rise again stay tuned for more news like these ones"),
            NewsItems(url2, "Is the former sports prodigy retiring", "1 days ago", "A 12 year career and now a retirement notice" +
                    "is this the end for Luis Santiago"),
            NewsItems(url3, "A heart breaking injury", "1 weeks ago", "One of the rising champs of the younger generations" +
                    "Joao Cancelo has one of the greatest injuries of his career after his match with Real Madrid"),
            NewsItems(url, "A New Star?", "25 minutes ago", "Manchester city is setting its sites on some new players as they" +
                    "attempt to sign Josko Gvardiol, Matheus Nunes, jeremy Doku, And Mateo Kovacic, will this be a new beginning or a waste of funds"),
            NewsItems(url, "The end of the power struggle", "2 hours ago", "Manchester united has finally bested city and there no more" +
                    "debate about which is the better club"),
            NewsItems(url3, "A new prodigy", "1 months ago", "After the retirement of messi and ronaldo outside the premier league" +
                    "and laliga the question remains who shall be the next big name is it Mbabe? is it Neymar some even say it's Haaland "),
        )

        val newsRecycler = binding.rvNews
        val adapter = NewsAdapter(newsArticles)
        newsRecycler.adapter = adapter
        newsRecycler.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        adapter.notifyDataSetChanged()

        binding.button1.setOnClickListener {
            val fragmentManager = parentFragmentManager
            val fragment = fragmentManager.findFragmentByTag("NewsFragment")
            if (fragment != null) {
                val fragmentTransaction = fragmentManager.beginTransaction()
                fragmentTransaction.remove(fragment)
                fragmentTransaction.commit()
            }
        }

    }
}