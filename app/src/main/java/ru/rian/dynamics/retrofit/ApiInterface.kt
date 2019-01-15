package ru.rian.dynamics.retrofit

import io.reactivex.Observable
import okhttp3.MultipartBody
import retrofit2.http.*
import ru.rian.dynamics.retrofit.model.ArticleResponse
import ru.rian.dynamics.retrofit.model.FeedResponse
import ru.rian.dynamics.retrofit.model.HSResult
import ru.rian.dynamics.retrofit.model.TerminalLoginModel


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
        @Query("feed") feed: String?,
        @Query("limit") limit: String?,
        @Query("offset") offset: String?,
        @Query("query") query: String?
    ): Observable<ArticleResponse>

    @GET("{hsPath}")
    fun requestHS(
        @Path("hsPath") hsPath: String?,
        @Query("appId") appId: String,
        @Query("deviceId") deviceId: String?
    ): Observable<HSResult>

    @POST("{loginPath}")
    @Multipart
    fun terminalLogin(
        @Path("loginPath") loginPath: String,
        @Part("deviceId") deviceId: String,
        @Part("username") username: String,
        @Part("password") password: String
    ): Observable<TerminalLoginModel>
}