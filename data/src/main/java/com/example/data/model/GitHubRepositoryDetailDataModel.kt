package com.example.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GitHubRepositoryDetailDataModel(

    @Json(name = "stargazers_count")
    val stargazersCount: Int?,

    @Json(name = "forks_count")
    val forks: Int?
)
