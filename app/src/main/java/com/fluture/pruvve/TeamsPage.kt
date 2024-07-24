package com.fluture.pruvve

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fluture.pruvve.adapters.TeamMates
import com.fluture.pruvve.adapters.TeamsAdapter
import com.fluture.pruvve.adapters.VideoPageItems
import com.fluture.pruvve.adapters.VideosPageAdapter1
import com.fluture.pruvve.databinding.ActivityTeamsPageBinding
import dagger.hilt.android.AndroidEntryPoint
import java.util.Random

@AndroidEntryPoint
class TeamsPage : AppCompatActivity() {
    private lateinit var binding:ActivityTeamsPageBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityTeamsPageBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        binding.imvBackButton.setOnClickListener {
            finish()
        }
        val myUrl = "https://i.pinimg.com/236x/4e/80/50/4e80508b0f22dfc42ce98bb8d0acb563.jpg"
        val myUrl2 = "https://i.pinimg.com/236x/0e/88/20/0e8820df856a51cdcb7a791396846421.jpg"
        val thisURL = "https://i.pinimg.com/236x/c8/71/8e/c8718e9e41758a502a709765792b7de3.jpg"
        binding.wvTeamMembersPfp.loadUrl(myUrl)
        val profilePosts = binding.tvTeamMedia
        val team = binding.tvTeamMates
        val goalkeepers = binding.rvGoallees
        val defenders = binding.rvDefenders
        val midFielders = binding.rvMidfielders
        val strikers = binding.rvStrikes
        val recycler1 = binding.rvTeams
        val recycler2 = binding.rvTeams2

        val teamName = intent.getStringExtra("ExtraTeamName")!!
        binding.tvTeamAssigned.text = teamName

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
        binding.nsvTeamMates.visibility = View.GONE
        profilePosts.setBackgroundResource(R.drawable.primary_button)
        team.setBackgroundResource(R.color.dark_gray)
        binding.nsvTeamMates.visibility = View.GONE
        binding.nsvTeamMates.isClickable = false
        binding.nsvTeamMates.isNestedScrollingEnabled = false
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
        recycler1.layoutManager = LinearLayoutManager(this@TeamsPage)
        recycler2.layoutManager = LinearLayoutManager(this@TeamsPage)

        profilePosts.setOnClickListener {
            recycler1.visibility = View.VISIBLE
            recycler2.visibility = View.VISIBLE
            recycler1.isClickable = true
            recycler2.isClickable = true
            profilePosts.setBackgroundResource(R.drawable.primary_button)
            team.setBackgroundResource(R.color.dark_gray)
            binding.nsvTeamMates.visibility = View.GONE
            binding.nsvTeamMates.isClickable = false
            binding.nsvTeamMates.isNestedScrollingEnabled = false
            recycler2.adapter = adapter2
            recycler1.adapter = adapter1
            recycler1.layoutManager = LinearLayoutManager(this@TeamsPage)
            recycler2.layoutManager = LinearLayoutManager(this@TeamsPage)
        }

        team.setOnClickListener {
            recycler1.visibility = View.GONE
            recycler2.visibility = View.GONE
            recycler1.isClickable = false
            recycler2.isClickable = false
            binding.nsvTeamMates.visibility = View.VISIBLE
            binding.nsvTeamMates.isClickable = true
            binding.nsvTeamMates.isNestedScrollingEnabled = true
            profilePosts.setBackgroundResource(R.color.dark_gray)
            team.setBackgroundResource(R.drawable.primary_button)
            val goalkeeppers = "GoalKeeper"
            val striker = getRandom(listOf("Centre forward", "Supporting forward", "Right winger", "Left winger")).toString()
            val defender = getRandom(listOf("Centre Back", "Full back")).toString()
            val midfield = getRandom(listOf("Defensive Midfielder", "Central Midfielder ", "Attacking Midfielder")).toString()
            val legs = getRandom(listOf("Left leg", "Right leg")).toString()
            val goalees = mutableListOf(
                TeamMates(thisURL, "Mahmud", goalkeeppers, legs),TeamMates(thisURL, "Mnar", goalkeeppers, legs),
                TeamMates(thisURL, "wint", goalkeeppers, legs),TeamMates(thisURL, "kyle", goalkeeppers, legs),
                TeamMates(thisURL, "ron", goalkeeppers, legs)
            )
            val goaleeAdapter = TeamsAdapter(goalees)
            goalkeepers.adapter = goaleeAdapter
            goalkeepers.layoutManager = LinearLayoutManager(this@TeamsPage)

            val defenderss = mutableListOf(
                TeamMates(myUrl,"wiz",defender, legs),TeamMates(myUrl,"Mike",defender, legs),TeamMates(myUrl,"daryl",defender, legs),
                TeamMates(myUrl,"joshua",defender, legs)
            )
            val defenderAdapter = TeamsAdapter(defenderss)
            defenders.adapter = defenderAdapter
            defenders.layoutManager = LinearLayoutManager(this@TeamsPage)

            val midfieldersList = mutableListOf(
                TeamMates(myUrl2, "Bruno",midfield, legs),TeamMates(myUrl2, "Cuanto",midfield, legs),TeamMates(myUrl2, "Davidson",midfield, legs),
                TeamMates(myUrl2, "Maguire",midfield, legs),TeamMates(myUrl2, "Ceaser",midfield, legs),TeamMates(myUrl2, "Casemiro",midfield, legs),
                TeamMates(myUrl2, "Paul",midfield, legs),
            )
            val midfielderAdapter = TeamsAdapter(midfieldersList)
            midFielders.adapter = midfielderAdapter
            midFielders.layoutManager = LinearLayoutManager(this@TeamsPage)

            val strikerList = mutableListOf(
                TeamMates(thisURL, "Harry kane", striker, legs),TeamMates(thisURL, "Rashford", striker, legs),
                TeamMates(thisURL, "Hazard", striker, legs),TeamMates(thisURL, "Haaland", striker, legs),TeamMates(thisURL, "Messi", striker, legs),
                TeamMates(thisURL, "Ronaldo", striker, legs),TeamMates(thisURL, "Pogba", striker, legs),
            )
            val strikerAdapter = TeamsAdapter(strikerList)
            strikers.adapter = strikerAdapter
            strikers.layoutManager = LinearLayoutManager(this@TeamsPage)
        }
    }
    private fun getRandom(list: List<String>): String? {
        if (list.isNotEmpty()) {
            val random = Random()
            val randomIndex = random.nextInt(list.size)
            return list[randomIndex]
        }
        return null
    }
}