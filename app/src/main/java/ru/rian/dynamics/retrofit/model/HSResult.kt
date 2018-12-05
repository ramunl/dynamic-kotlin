package ru.rian.dynamics.retrofit.model

import com.google.gson.annotations.SerializedName

data class HSResult(
        @SerializedName("application") var application: String? = null,
        @SerializedName("apiRequests") var apiRequestArray: List<ApiRequest> = listOf()
)

data class ApiRequest(
        @SerializedName("getHandshake") var getHandshake: Source? = null,
        @SerializedName("getArticles") var getArticles: Source? = null,
        @SerializedName("login") var login: Source? = null,
        @SerializedName("logout") var logout: Source? = null,
        @SerializedName("getFeeds") var getFeeds: Source? = null,
        @SerializedName("createFeed") var createFeed: Source? = null,
        @SerializedName("deleteFeed") var deleteFeed: Source? = null,
        @SerializedName("logout") var synchronizeFeeds: Source? = null,
        @SerializedName("getFeeds") var upsertSubscription: Source? = null,
        @SerializedName("createFeed") var getNotifications: Source? = null
)
data class Source(
        @SerializedName("url") var url: String? = null,
        @SerializedName("method") var method: String? = null
)