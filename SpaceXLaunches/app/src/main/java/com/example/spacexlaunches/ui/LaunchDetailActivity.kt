package com.example.spacexlaunches.ui

import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.example.spacexlaunches.R
import com.example.spacexlaunches.data.api.RetrofitClient
import com.example.spacexlaunches.data.db.LaunchDatabase
import com.example.spacexlaunches.data.model.Launch
import com.example.spacexlaunches.data.repository.LaunchRepository
import com.example.spacexlaunches.databinding.ActivityLaunchDetailBinding
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class LaunchDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLaunchDetailBinding
    private lateinit var repository: LaunchRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLaunchDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        binding.toolbar.setNavigationOnClickListener {
            onBackPressed()
        }

        val launchDao = LaunchDatabase.getDatabase(application).launchDao()
        repository = LaunchRepository(launchDao, RetrofitClient.spaceXApi)

        val flightNumber = intent.getIntExtra("FLIGHT_NUMBER", -1)
        if (flightNumber != -1) {
            loadLaunchDetails(flightNumber)
        }
    }

    private fun loadLaunchDetails(flightNumber: Int) {
        binding.progressBar.visibility = View.VISIBLE

        lifecycleScope.launch {
            try {
                val launch = repository.getLaunchByFlightNumber(flightNumber)
                launch?.let {
                    displayLaunchDetails(it)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                binding.progressBar.visibility = View.GONE
            }
        }
    }

    private fun displayLaunchDetails(launch: Launch) {
        binding.apply {
            missionNameDetail.text = launch.missionName
            flightNumberDetail.text = "Flight #${launch.flightNumber}"
            launchDateDetail.text = formatDate(launch.launchDateUnix)
            launchYearDetail.text = launch.launchYear

            // Статус запуску
            when (launch.launchSuccess) {
                true -> {
                    launchStatusDetail.text = "Успішний"
                    launchStatusDetail.setTextColor(Color.parseColor("#4CAF50"))
                }
                false -> {
                    launchStatusDetail.text = "Невдалий"
                    launchStatusDetail.setTextColor(Color.parseColor("#F44336"))
                }
                null -> {
                    launchStatusDetail.text = "Невідомо"
                    launchStatusDetail.setTextColor(Color.parseColor("#9E9E9E"))
                }
            }

            // Ракета
            if (launch.rocket != null) {
                rocketCard.visibility = View.VISIBLE
                rocketNameDetail.text = launch.rocket.rocketName ?: "Невідомо"
                rocketTypeDetail.text = launch.rocket.rocketType ?: "Невідомо"
            } else {
                rocketCard.visibility = View.GONE
            }

            // Місце запуску
            if (launch.launchSite != null) {
                launchSiteCard.visibility = View.VISIBLE
                launchSiteDetail.text = launch.launchSite.siteNameLong
                    ?: launch.launchSite.siteName
                            ?: "Невідомо"
            } else {
                launchSiteCard.visibility = View.GONE
            }

            // Деталі
            if (!launch.details.isNullOrEmpty()) {
                detailsCard.visibility = View.VISIBLE
                launchDetailsText.text = launch.details
            } else {
                detailsCard.visibility = View.GONE
            }

            // Зображення місії
            val imageUrl = launch.links?.missionPatch ?: launch.links?.missionPatchSmall
            if (imageUrl != null) {
                Glide.with(this@LaunchDetailActivity)
                    .load(imageUrl)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .placeholder(R.drawable.ic_rocket)
                    .error(R.drawable.ic_rocket)
                    .into(missionPatchImageLarge)
            } else {
                missionPatchImageLarge.setImageResource(R.drawable.ic_rocket)
            }
        }
    }

    private fun formatDate(timestamp: Long): String {
        val date = Date(timestamp * 1000)
        val format = SimpleDateFormat("dd MMMM yyyy, HH:mm", Locale("uk", "UA"))
        return format.format(date)
    }
}