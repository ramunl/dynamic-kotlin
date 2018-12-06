package ru.rian.dynamics.retrofit.model

import com.google.gson.annotations.SerializedName

data class HSResult(
        @SerializedName("application") var application: String? = null,
        @SerializedName("apiRequests") var apiRequestArray: ApiRequests? = null
)
data class ApiRequests (
        @SerializedName("getHandshake") var getHandshake: Source? = null,
        @SerializedName("getArticles") var getArticles: Source? = null,
        @SerializedName("login") var login: Source? = null,
        @SerializedName("logout") var logout: Source? = null,
        @SerializedName("getFeeds") var getFeeds: Source? = null,
        @SerializedName("createFeed") var createFeed: Source? = null,
        @SerializedName("deleteFeed") var deleteFeed: Source? = null,
        @SerializedName("synchronizeFeeds") var synchronizeFeeds: Source? = null,
        @SerializedName("upsertSubscription") var upsertSubscription: Source? = null,
        @SerializedName("getNotifications") var getNotifications: Source? = null
)

data class Source(
        @SerializedName("url") var url: String? = null,
        @SerializedName("method") var method: String? = null
)