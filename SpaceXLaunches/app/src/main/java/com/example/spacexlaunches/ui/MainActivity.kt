package com.example.spacexlaunches.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.spacexlaunches.databinding.ActivityMainBinding
import com.example.spacexlaunches.ui.adapter.LaunchAdapter
import com.example.spacexlaunches.ui.viewmodel.LaunchViewModel
import com.google.android.material.snackbar.Snackbar

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: LaunchViewModel
    private lateinit var adapter: LaunchAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        setupRecyclerView()
        setupViewModel()
        setupSwipeRefresh()
    }

    private fun setupRecyclerView() {
        adapter = LaunchAdapter { launch ->
            val intent = Intent(this, LaunchDetailActivity::class.java).apply {
                putExtra("FLIGHT_NUMBER", launch.flightNumber)
            }
            startActivity(intent)
        }

        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = this@MainActivity.adapter
        }
    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(this)[LaunchViewModel::class.java]

        viewModel.launches.observe(this) { launches ->
            adapter.submitList(launches)

            if (launches.isEmpty() && viewModel.isLoading.value != true) {
                binding.emptyView.visibility = View.VISIBLE
                binding.recyclerView.visibility = View.GONE
            } else {
                binding.emptyView.visibility = View.GONE
                binding.recyclerView.visibility = View.VISIBLE
            }
        }

        viewModel.isLoading.observe(this) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        viewModel.isRefreshing.observe(this) { isRefreshing ->
            binding.swipeRefreshLayout.isRefreshing = isRefreshing
        }

        viewModel.errorMessage.observe(this) { errorMessage ->
            errorMessage?.let {
                Snackbar.make(binding.root, it, Snackbar.LENGTH_LONG)
                    .setAction("OK") {
                        viewModel.clearError()
                    }
                    .show()
            }
        }
    }

    private fun setupSwipeRefresh() {
        binding.swipeRefreshLayout.setOnRefreshListener {
            viewModel.refreshLaunches()
        }
    }
}