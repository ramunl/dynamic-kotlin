package ru.rian.dynamics.retrofit

import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import ru.rian.dynamics.retrofit.model.FeedResponse
import ru.rian.dynamics.retrofit.model.HSResult


interface ApiInterface {

    @GET("v3/handshake")
    fun requestHS(
        @Query("appId") appId: String,
        @Query("deviceId") deviceId: String?,
        @Query("lang") lang: String?
    ): Observable<HSResult>

    @GET("{feedsPath}")
    fun requestFeeds(
        @Query("appId") appId: String,
        @Query("deviceId") deviceId: String,
        @Path("feedsPath") feedsPath: String?
    ): Observable<FeedResponse>

}