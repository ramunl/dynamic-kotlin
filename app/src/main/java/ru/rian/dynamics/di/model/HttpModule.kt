package ru.rian.dynamics.di.model

import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import ru.rian.dynamics.BuildConfig
import ru.rian.dynamics.retrofit.ApiInterface
import ru.rian.dynamics.ui.helpers.NetworkHelper.getCheckedUserAgentValueImpl
import ru.rian.dynamics.utils.*
import ru.rian.dynamics.utils.PreferenceHelper.get
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
class HttpModule {
    @Provides
    @Singleton
    fun providesRetrofit(): Retrofit {
        val prefs = PreferenceHelper.prefs()
        val client = OkHttpClient().newBuilder()
            .connectTimeout(HTTP_TIMEOUT_CONNECTION.toLong(), TimeUnit.MILLISECONDS)
            .writeTimeout(HTTP_TIMEOUT_REQUEST.toLong(), TimeUnit.MILLISECONDS)
            .readTimeout(HTTP_TIMEOUT_REQUEST.toLong(), TimeUnit.MILLISECONDS)
            .followRedirects(true)
            .followSslRedirects(true)
            .addInterceptor { chain ->
                val original = chain.request()
                val requestBuilder = original.newBuilder()
                requestBuilder.addHeader(HEADER_ACCEPT, HEADER_ACCEPT_VALUE_APP_JSON)
                requestBuilder.addHeader(HEADER_USER_AGENT, getCheckedUserAgentValueImpl())
                var token: String? = prefs[TOKEN_STRING_KEY]
                if (token != null) {
                    requestBuilder.addHeader(HEADER_TOKEN, token)
                }
                requestBuilder.method(original.method(), original.body())
                val request = requestBuilder.build()
                chain.proceed(request)

            }
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.NONE
            }).build()

        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideApiInterface(retrofit: Retrofit): ApiInterface {
        return retrofit.create(ApiInterface::class.java)
    }

    /*
    * if (sClient == null) {
            val builder = OkHttpClient.Builder()
            if (BuildConfig.DEBUG) {
                val interceptor = HttpLoggingInterceptor()
                interceptor.level = HttpLoggingInterceptor.Level.HEADERS
                builder.addInterceptor(interceptor)
            }
            sClient = builder
                .connectTimeout(HTTP_TIMEOUT_CONNECTION.toLong(), TimeUnit.MILLISECONDS)
                .writeTimeout(HTTP_TIMEOUT_REQUEST.toLong(), TimeUnit.MILLISECONDS)
                .readTimeout(HTTP_TIMEOUT_REQUEST.toLong(), TimeUnit.MILLISECONDS)
                .followRedirects(true)
                .followSslRedirects(true)
                .build()
        }
    * */
}