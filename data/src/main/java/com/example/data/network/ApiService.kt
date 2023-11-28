package com.example.data.network

import com.example.data.BuildConfig
import com.example.data.model.GitHubRepositoryDataModel
import com.example.data.model.GitHubRepositoryDetailDataModel
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query
import retrofit2.http.Url

interface ApiService {

    @GET("/users/kotlin/repos")
    suspend fun allRepositories(
        @Header("Authorization") accessToken: String = BuildConfig.PERSONAL_ACCESS_TOKEN,
        @Query("per_page") resultsPerPage: Int = 8
    ): List<GitHubRepositoryDataModel>

    @GET
    suspend fun repositoryStarsAndForks(
        @Url url: String,
        @Header("Authorization") accessToken: String = BuildConfig.PERSONAL_ACCESS_TOKEN
    ): GitHubRepositoryDetailDataModel

    @GET
    suspend fun repositoryLanguages(
        @Url url: String,
        @Header("Authorization") accessToken: String = BuildConfig.PERSONAL_ACCESS_TOKEN
    ): String
}
