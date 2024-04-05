package com.wvt.wvento.models

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.wvt.wvento.util.Constants
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Entity(tableName = Constants.Explore_TABLE)
@Parcelize
data class Results(
    @PrimaryKey(autoGenerate = false)
    @SerializedName("event_id")
    val id: Int,
    @SerializedName("event_name")
    val title: String,
    @SerializedName("category")
    val category: String,
    @SerializedName("event_location")
    val location: String,
    @SerializedName("start_date")
    val start_date: String,
    @SerializedName("end_date")
    val end_date: String,
    @SerializedName("event_price")
    val event_price: String,
    @SerializedName("counter")
    val counter: Int,
    @SerializedName("event_str_time")
    val start_time: String,
    @SerializedName("event_end_time")
    val end_time: String,
    @SerializedName("event_desc")
    val event_desc: String,
    @SerializedName("event_user")
    val event_user: String,
    @SerializedName("event_userUrl")
    val event_userUrl: String,
    @SerializedName("event_poster")
    val event_poster: String,
    @SerializedName("event_video")
    val event_video: String
): Parcelable
