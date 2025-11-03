package com.example.spacexlaunches.ui.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.example.spacexlaunches.R
import com.example.spacexlaunches.data.model.Launch
import com.example.spacexlaunches.databinding.ItemLaunchBinding
import java.text.SimpleDateFormat
import java.util.*

class LaunchAdapter(
    private val onItemClick: (Launch) -> Unit
) : ListAdapter<Launch, LaunchAdapter.LaunchViewHolder>(LaunchDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LaunchViewHolder {
        val binding = ItemLaunchBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return LaunchViewHolder(binding, onItemClick)
    }

    override fun onBindViewHolder(holder: LaunchViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class LaunchViewHolder(
        private val binding: ItemLaunchBinding,
        private val onItemClick: (Launch) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(launch: Launch) {
            binding.apply {
                missionNameText.text = launch.missionName
                flightNumberText.text = "Flight #${launch.flightNumber}"
                launchDateText.text = formatDate(launch.launchDateUnix)

                // Статус запуску
                when (launch.launchSuccess) {
                    true -> {
                        statusText.text = "Успішний запуск"
                        statusIndicator.setBackgroundResource(R.drawable.circle_shape)
                        statusIndicator.backgroundTintList =
                            android.content.res.ColorStateList.valueOf(Color.parseColor("#4CAF50"))
                    }
                    false -> {
                        statusText.text = "Невдалий запуск"
                        statusIndicator.setBackgroundResource(R.drawable.circle_shape)
                        statusIndicator.backgroundTintList =
                            android.content.res.ColorStateList.valueOf(Color.parseColor("#F44336"))
                    }
                    null -> {
                        statusText.text = "Статус невідомий"
                        statusIndicator.setBackgroundResource(R.drawable.circle_shape)
                        statusIndicator.backgroundTintList =
                            android.content.res.ColorStateList.valueOf(Color.parseColor("#9E9E9E"))
                    }
                }

                // Завантаження зображення місії
                val imageUrl = launch.links?.missionPatchSmall ?: launch.links?.missionPatch
                if (imageUrl != null) {
                    Glide.with(binding.root.context)
                        .load(imageUrl)
                        .transition(DrawableTransitionOptions.withCrossFade())
                        .placeholder(R.drawable.ic_rocket)
                        .error(R.drawable.ic_rocket)
                        .into(missionPatchImage)
                } else {
                    missionPatchImage.setImageResource(R.drawable.ic_rocket)
                }

                root.setOnClickListener {
                    onItemClick(launch)
                }
            }
        }

        private fun formatDate(timestamp: Long): String {
            val date = Date(timestamp * 1000)
            val format = SimpleDateFormat("dd MMMM yyyy, HH:mm", Locale("uk", "UA"))
            return format.format(date)
        }
    }

    private class LaunchDiffCallback : DiffUtil.ItemCallback<Launch>() {
        override fun areItemsTheSame(oldItem: Launch, newItem: Launch): Boolean {
            return oldItem.flightNumber == newItem.flightNumber
        }

        override fun areContentsTheSame(oldItem: Launch, newItem: Launch): Boolean {
            return oldItem == newItem
        }
    }
}