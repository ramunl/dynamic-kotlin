package ru.rian.dynamics.retrofit

import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import ru.rian.dynamics.retrofit.model.ArticleResponse
import ru.rian.dynamics.retrofit.model.FeedResponse
import ru.rian.dynamics.retrofit.model.HSResult


interface ApiInterface {

    @GET("{feedsPath}")
    fun requestFeed(
        @Path("feedsPath") feedsPath: String?,
        @Query("appId") appId: String,
        @Query("deviceId") deviceId: String?,
        @Query("lang") lang: String?
    ): Observable<FeedResponse>

    @GET("{articlesPath}")
    fun requestArticles(
        @Path("articlesPath") articlesPath: String?,
        @Query("appId") appId: String,
        @Query("deviceId") deviceId: String?,
        @Query("feed") feed: String,
        @Query("offset") offset: String,
        @Query("limit") limit: String?,
        @Query("query") query: String?
    ): Observable<ArticleResponse>

    @GET("{hsPath}")
    fun requestHS(
        @Path("hsPath") hsPath: String?,
        @Query("appId") appId: String,
        @Query("deviceId") deviceId: String?
    ): Observable<HSResult>

}