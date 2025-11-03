package com.example.spacexlaunches.data.repository

import android.util.Log
import androidx.lifecycle.LiveData
import com.example.spacexlaunches.data.api.SpaceXApi
import com.example.spacexlaunches.data.db.LaunchDao
import com.example.spacexlaunches.data.model.Launch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Calendar

class LaunchRepository(
    private val launchDao: LaunchDao,
    private val spaceXApi: SpaceXApi
) {

    val allLaunches: LiveData<List<Launch>> = launchDao.getAllLaunches()

    suspend fun refreshLaunches(): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            // Отримуємо дату 5 років тому
            val calendar = Calendar.getInstance()
            calendar.add(Calendar.YEAR, -5)
            val fiveYearsAgo = calendar.get(Calendar.YEAR).toString()

            Log.d("LaunchRepository", "Fetching launches from year: $fiveYearsAgo")

            val response = spaceXApi.getPastLaunches()

            if (response.isSuccessful) {
                val launches = response.body() ?: emptyList()

                // Фільтруємо запуски за останні 5 років
                val filteredLaunches = launches.filter { launch ->
                    launch.launchYear.toIntOrNull()?.let { year ->
                        year >= fiveYearsAgo.toInt()
                    } ?: false
                }

                Log.d("LaunchRepository", "Total launches: ${launches.size}, Filtered: ${filteredLaunches.size}")

                // Оновлюємо базу даних
                launchDao.updateLaunches(filteredLaunches)

                Result.success(Unit)
            } else {
                Log.e("LaunchRepository", "Error: ${response.code()} - ${response.message()}")
                Result.failure(Exception("HTTP ${response.code()}: ${response.message()}"))
            }
        } catch (e: Exception) {
            Log.e("LaunchRepository", "Exception during refresh", e)
            Result.failure(e)
        }
    }

    suspend fun getLaunchByFlightNumber(flightNumber: Int): Launch? {
        return withContext(Dispatchers.IO) {
            launchDao.getLaunchByFlightNumber(flightNumber)
        }
    }

    suspend fun hasLaunches(): Boolean {
        return withContext(Dispatchers.IO) {
            launchDao.getCount() > 0
        }
    }
}