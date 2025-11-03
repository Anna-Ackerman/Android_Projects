package com.example.spacexlaunches.data.db

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.spacexlaunches.data.model.Launch

@Dao
interface LaunchDao {

    @Query("SELECT * FROM launches ORDER BY launch_date_unix DESC")
    fun getAllLaunches(): LiveData<List<Launch>>

    @Query("SELECT * FROM launches WHERE flight_number = :flightNumber")
    suspend fun getLaunchByFlightNumber(flightNumber: Int): Launch?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLaunches(launches: List<Launch>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLaunch(launch: Launch)

    @Query("DELETE FROM launches")
    suspend fun deleteAll()

    @Transaction
    suspend fun updateLaunches(launches: List<Launch>) {
        deleteAll()
        insertLaunches(launches)
    }

    @Query("SELECT COUNT(*) FROM launches")
    suspend fun getCount(): Int

    @Query("SELECT * FROM launches WHERE launch_year >= :startYear ORDER BY launch_date_unix DESC")
    fun getLaunchesFromYear(startYear: String): LiveData<List<Launch>>
}