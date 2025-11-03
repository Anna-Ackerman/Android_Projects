package com.example.spacexlaunches.data.db

import androidx.room.TypeConverter
import com.example.spacexlaunches.data.model.LaunchSite
import com.example.spacexlaunches.data.model.Links
import com.example.spacexlaunches.data.model.Rocket
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

class Converters {
    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    @TypeConverter
    fun fromRocket(rocket: Rocket?): String? {
        return rocket?.let {
            moshi.adapter(Rocket::class.java).toJson(it)
        }
    }

    @TypeConverter
    fun toRocket(json: String?): Rocket? {
        return json?.let {
            moshi.adapter(Rocket::class.java).fromJson(it)
        }
    }

    @TypeConverter
    fun fromLinks(links: Links?): String? {
        return links?.let {
            moshi.adapter(Links::class.java).toJson(it)
        }
    }

    @TypeConverter
    fun toLinks(json: String?): Links? {
        return json?.let {
            moshi.adapter(Links::class.java).fromJson(it)
        }
    }

    @TypeConverter
    fun fromLaunchSite(launchSite: LaunchSite?): String? {
        return launchSite?.let {
            moshi.adapter(LaunchSite::class.java).toJson(it)
        }
    }

    @TypeConverter
    fun toLaunchSite(json: String?): LaunchSite? {
        return json?.let {
            moshi.adapter(LaunchSite::class.java).fromJson(it)
        }
    }
}