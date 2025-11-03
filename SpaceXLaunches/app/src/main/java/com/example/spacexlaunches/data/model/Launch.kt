package com.example.spacexlaunches.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
@Entity(tableName = "launches")
data class Launch(
    @PrimaryKey
    @Json(name = "flight_number")
    @ColumnInfo(name = "flight_number")
    val flightNumber: Int,

    @Json(name = "mission_name")
    @ColumnInfo(name = "mission_name")
    val missionName: String,

    @Json(name = "launch_date_unix")
    @ColumnInfo(name = "launch_date_unix")
    val launchDateUnix: Long,

    @Json(name = "launch_year")
    @ColumnInfo(name = "launch_year")
    val launchYear: String,

    @Json(name = "launch_success")
    @ColumnInfo(name = "launch_success")
    val launchSuccess: Boolean?,

    @Json(name = "details")
    @ColumnInfo(name = "details")
    val details: String?,

    @Json(name = "rocket")
    @ColumnInfo(name = "rocket_id")
    val rocket: Rocket?,

    @Json(name = "links")
    @ColumnInfo(name = "links")
    val links: Links?,

    @Json(name = "launch_site")
    @ColumnInfo(name = "launch_site")
    val launchSite: LaunchSite?
)

@JsonClass(generateAdapter = true)
data class Rocket(
    @Json(name = "rocket_id")
    val rocketId: String?,

    @Json(name = "rocket_name")
    val rocketName: String?,

    @Json(name = "rocket_type")
    val rocketType: String?
)

@JsonClass(generateAdapter = true)
data class Links(
    @Json(name = "mission_patch")
    val missionPatch: String?,

    @Json(name = "mission_patch_small")
    val missionPatchSmall: String?,

    @Json(name = "article_link")
    val articleLink: String?,

    @Json(name = "video_link")
    val videoLink: String?,

    @Json(name = "wikipedia")
    val wikipedia: String?
)

@JsonClass(generateAdapter = true)
data class LaunchSite(
    @Json(name = "site_id")
    val siteId: String?,

    @Json(name = "site_name")
    val siteName: String?,

    @Json(name = "site_name_long")
    val siteNameLong: String?
)