package ru.rian.dynamics.retrofit.model

import android.arch.persistence.room.PrimaryKey
import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName


data class ArticleResponse(
    @SerializedName("articles") var articles: List<Article>? = null
)

data class Article(
    @PrimaryKey
    @SerializedName("id") var id: Int? = null,
    @SerializedName("title") var title: String? = null,
    @SerializedName("body") var body: String? = null,
    @SerializedName("lang") var lang: String? = null,
    @SerializedName("createdAt") var createdAt: Int? = null,
    @SerializedName("priority") var priority: Int? = null,
    @SerializedName("feeds") var feeds: List<ArticleFeed>? = null
) : Comparable<Article>, Parcelable {
    override fun compareTo(other: Article): Int {
        if (other.id == (this.id)) {
            return 0
        }
        return 1
    }

    constructor(source: Parcel) : this(
        source.readValue(Int::class.java.classLoader) as Int?,
        source.readString(),
        source.readString(),
        source.readString(),
        source.readValue(Int::class.java.classLoader) as Int?,
        source.readValue(Int::class.java.classLoader) as Int?,
        ArrayList<ArticleFeed>().apply { source.readList(this, ArticleFeed::class.java.classLoader) }
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeValue(id)
        writeString(title)
        writeString(body)
        writeString(lang)
        writeValue(createdAt)
        writeValue(priority)
        writeList(feeds)
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<Article> = object : Parcelable.Creator<Article> {
            override fun createFromParcel(source: Parcel): Article = Article(source)
            override fun newArray(size: Int): Array<Article?> = arrayOfNulls(size)
        }
    }
}

data class ArticleFeed(
    @SerializedName("sid") var sid: String = "",
    @SerializedName("title") var title: String? = null,
    @SerializedName("type") var type: String? = null
) : Parcelable {
    constructor(source: Parcel) : this(
        source.readString(),
        source.readString(),
        source.readString()
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeString(sid)
        writeString(title)
        writeString(type)
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<ArticleFeed> = object : Parcelable.Creator<ArticleFeed> {
            override fun createFromParcel(source: Parcel): ArticleFeed = ArticleFeed(source)
            override fun newArray(size: Int): Array<ArticleFeed?> = arrayOfNulls(size)
        }
    }
}