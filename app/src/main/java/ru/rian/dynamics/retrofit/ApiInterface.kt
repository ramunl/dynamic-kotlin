package ru.rian.dynamics.retrofit

import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import ru.rian.dynamics.retrofit.model.FeedResponse
import ru.rian.dynamics.retrofit.model.HSResult


interface ApiInterface {
    /*
    @GET("{feedsPath}")
    fun <T> requestGet(
        @Path("feedsPath") feedsPath: String?,
        @Query("appId") appId: String,
        @Query("deviceId") deviceId: String?,
        @Query("lang") lang: String?
    ): Observable<T>
    */

    @GET("{feedsPath}")
    fun requestFeed(
        @Path("feedsPath") feedsPath: String?,
        @Query("appId") appId: String,
        @Query("deviceId") deviceId: String?,
        @Query("lang") lang: String?
    ): Observable<FeedResponse>

    @GET("{hsPath}")
    fun requestHS(
        @Path("hsPath") hsPath: String?,
        @Query("appId") appId: String,
        @Query("deviceId") deviceId: String?
    ): Observable<HSResult>

}