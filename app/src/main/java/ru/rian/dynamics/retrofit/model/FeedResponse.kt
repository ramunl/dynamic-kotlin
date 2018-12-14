package ru.rian.dynamics.retrofit.model

import com.google.gson.annotations.SerializedName

data class FeedResponse(
    @SerializedName("feeds") var feeds: List<Feed>? = null
)

data class Feed(
    @SerializedName("sid") var sid: String? = null,
    @SerializedName("lang") var lang: String? = null,
    @SerializedName("title") var title: String? = null,
    @SerializedName("subTitle") var subTitle: String? = null,
    @SerializedName("type") var type: String? = null,
    @SerializedName("notification") var notification: String? = null,
    @SerializedName("query") var query: String? = null,
    @SerializedName("match") var match: String? = null
)