package com.fluture.pruvve

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.fluture.pruvve.adapters.TeamMates
import com.fluture.pruvve.adapters.TeamsAdapter
import com.fluture.pruvve.adapters.VideoPageItems
import com.fluture.pruvve.adapters.VideosPageAdapter1
import com.fluture.pruvve.databinding.ActivityTeamsPage2Binding
import dagger.hilt.android.AndroidEntryPoint
import java.util.Random

@AndroidEntryPoint
class TeamsPage2 : AppCompatActivity() {
    private lateinit var binding: ActivityTeamsPage2Binding
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityTeamsPage2Binding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        binding.imvBackButton.setOnClickListener {
            finish()
        }
        val gridLayout = GridLayoutManager(this@TeamsPage2, 2, GridLayoutManager.VERTICAL, false)
        val myUrl = "https://i.pinimg.com/236x/be/e3/9e/bee39e2d9eee57acb2091fcadfd6d0d0.jpg"
        val myUrl2 = "https://i.pinimg.com/236x/ac/15/5f/ac155f3484a23ee2c057cce2eddbd349.jpg"
        val thisURL = "https://i.pinimg.com/236x/1f/0e/27/1f0e271f2a59bd426062578485a71b39.jpg"
        binding.wvTeamMembersPfp.loadUrl(myUrl)
        val profilePosts = binding.tvTeamMedia
        val team = binding.tvTeamMates
        val goalkeepers = binding.rvGoallees
        val defenders = binding.rvDefenders
        val midFielders = binding.rvMidfielders
        val strikers = binding.rvStrikes
        val recycler1 = binding.rvTeams

        val teamName = intent.getStringExtra("ExtraTeamName")!!
        binding.tvTeamAssigned.text = teamName
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
            VideoPageItems(myUrl2),
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
        recycler1.adapter = adapter1
        recycler1.layoutManager = gridLayout

        profilePosts.setOnClickListener {
            recycler1.visibility = View.VISIBLE
            recycler1.isClickable = true
            profilePosts.setBackgroundResource(R.drawable.primary_button)
            team.setBackgroundResource(R.color.dark_gray)
            binding.nsvTeamMates.visibility = View.GONE
            binding.nsvTeamMates.isClickable = false
            binding.nsvTeamMates.isNestedScrollingEnabled = false
            recycler1.adapter = adapter1
            recycler1.layoutManager = LinearLayoutManager(this@TeamsPage2)
        }

        team.setOnClickListener {
            recycler1.visibility = View.GONE
            recycler1.isClickable = false
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
                TeamMates(thisURL, "Mahmud", goalkeeppers, legs),
                TeamMates(thisURL, "Mnar", goalkeeppers, legs),
                TeamMates(thisURL, "wint", goalkeeppers, legs),
                TeamMates(thisURL, "kyle", goalkeeppers, legs),
                TeamMates(thisURL, "ron", goalkeeppers, legs)
            )
            val goaleeAdapter = TeamsAdapter(goalees)
            goalkeepers.adapter = goaleeAdapter
            goalkeepers.layoutManager = LinearLayoutManager(this@TeamsPage2)

            val defenderss = mutableListOf(
                TeamMates(myUrl,"wiz",defender, legs),
                TeamMates(myUrl,"Mike",defender, legs),
                TeamMates(myUrl,"daryl",defender, legs),
                TeamMates(myUrl,"joshua",defender, legs)
            )
            val defenderAdapter = TeamsAdapter(defenderss)
            defenders.adapter = defenderAdapter
            defenders.layoutManager = LinearLayoutManager(this@TeamsPage2)

            val midfieldersList = mutableListOf(
                TeamMates(myUrl2, "Bruno",midfield, legs),
                TeamMates(myUrl2, "Cuanto",midfield, legs),
                TeamMates(myUrl2, "Davidson",midfield, legs),
                TeamMates(myUrl2, "Maguire",midfield, legs),
                TeamMates(myUrl2, "Ceaser",midfield, legs),
                TeamMates(myUrl2, "Casemiro",midfield, legs),
                TeamMates(myUrl2, "Paul",midfield, legs),
            )
            val midfielderAdapter = TeamsAdapter(midfieldersList)
            midFielders.adapter = midfielderAdapter
            midFielders.layoutManager = LinearLayoutManager(this@TeamsPage2)

            val strikerList = mutableListOf(
                TeamMates(thisURL, "Harry kane", striker, legs),
                TeamMates(thisURL, "Rashford", striker, legs),
                TeamMates(thisURL, "Hazard", striker, legs),
                TeamMates(thisURL, "Haaland", striker, legs),
                TeamMates(thisURL, "Messi", striker, legs),
                TeamMates(thisURL, "Ronaldo", striker, legs),
                TeamMates(thisURL, "Pogba", striker, legs),
            )
            val strikerAdapter = TeamsAdapter(strikerList)
            strikers.adapter = strikerAdapter
            strikers.layoutManager = LinearLayoutManager(this@TeamsPage2)
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