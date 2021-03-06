package ru.rian.dynamics.retrofit.model

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class FeedResponse(
    @SerializedName("feeds") var feeds: List<Feed>? = null
)

@Entity(tableName = "Feed")
data class Feed(
    @PrimaryKey
    @SerializedName("sid") var sid: String = "",
    @SerializedName("lang") var lang: String? = null,
    @SerializedName("title") var title: String? = null,
    @SerializedName("subTitle") var subTitle: String? = null,
    @SerializedName("type") var type: String? = null,
    @SerializedName("notification") var notification: String? = null,
    @SerializedName("query") var query: String? = null,
    @SerializedName("match") var match: String? = null
): Serializable