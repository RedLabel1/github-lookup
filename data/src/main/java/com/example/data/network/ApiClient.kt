package com.example.data.network

import android.content.Context
import com.example.data.BuildConfig.BASE_URL
import dagger.hilt.android.qualifiers.ApplicationContext
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import javax.inject.Inject

class ApiClient @Inject constructor(@ApplicationContext context: Context) {

    private val cacheSize = 50L * 1024L * 1024L

    private val cache = Cache(directory = context.cacheDir, maxSize = cacheSize)

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BASIC
    }

    private val client = OkHttpClient().newBuilder()
        .cache(cache)
        .addInterceptor(loggingInterceptor)
        .retryOnConnectionFailure(false)
        .build()

    private fun retrofit(): Retrofit = Retrofit.Builder()
        .client(client)
        .baseUrl(BASE_URL)
        .addConverterFactory(ScalarsConverterFactory.create())
        .addConverterFactory(MoshiConverterFactory.create())
        .build()

    val api: ApiService = retrofit().create(ApiService::class.java)
}
