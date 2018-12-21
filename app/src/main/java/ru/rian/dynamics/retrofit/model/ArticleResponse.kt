package ru.rian.dynamics.retrofit.model

import android.arch.persistence.room.PrimaryKey
import com.google.gson.annotations.SerializedName


data class ArticleResponse(
    @PrimaryKey
    @SerializedName("id") var id: Int? = null,
    @SerializedName("title") var title: String? = null,
    @SerializedName("body") var body: String? = null,
    @SerializedName("lang") var lang: String? = null,
    @SerializedName("createdAt") var createdAt: Int? = null,
    @SerializedName("priority") var priority: Int? = null,
    @SerializedName("feeds") var feeds: List<ArticleFeed>? = null
)

data class ArticleFeed(
    @SerializedName("sid") var sid: String = "",
    @SerializedName("title") var title: String? = null,
    @SerializedName("type") var type: String? = null
)