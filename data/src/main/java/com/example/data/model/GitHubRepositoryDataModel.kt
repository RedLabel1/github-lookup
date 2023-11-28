package com.example.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GitHubRepositoryDataModel(

    @Json(name = "name")
    val name: String?,

    @Json(name = "owner")
    val owner: Owner?,

    @Json(name = "description")
    val description: String?,

    @Json(name = "url")
    val url: String?,

    @Json(name = "html_url")
    val htmlUrl: String?,

    @Json(name = "languages_url")
    val languagesUrl: String?
)

@JsonClass(generateAdapter = true)
data class Owner(

    @Json(name = "avatar_url")
    val avatarUrl: String?
)
