package com.example.data.di

import android.content.Context
import com.example.data.network.ApiClient
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object DataModule {

    @Singleton
    @Provides
    fun provideApiClient(@ApplicationContext context: Context) = ApiClient(context)

    @Singleton
    @Provides
    fun provideMoshi(): Moshi = Moshi.Builder().build()

    @Singleton
    @Provides
    fun provideMoshiJsonToMapAdapter(moshi: Moshi): JsonAdapter<Map<String, Int>> = moshi.adapter(
        Types.newParameterizedType(
            MutableMap::class.java,
            String::class.java,
            Int::class.javaObjectType
        )
    )
}
