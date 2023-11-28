package com.example.data.mapper

import com.example.data.model.GitHubRepositoryDataModel
import com.example.data.model.GitHubRepositoryDetailDataModel
import com.example.domain.model.GitHubRepositoryDetailDomainModel
import com.example.domain.model.GitHubRepositoryDomainModel
import com.example.domain.model.GitHubRepositoryLanguageDomainModel
import com.squareup.moshi.JsonAdapter


fun GitHubRepositoryDataModel.toDomainModel() = GitHubRepositoryDomainModel(
    name = name,
    ownerAvatar = owner?.avatarUrl,
    description = description,
    url = url,
    htmlUrl = htmlUrl,
    languagesUrl = languagesUrl
)

fun GitHubRepositoryDetailDataModel.toDomainModel() = GitHubRepositoryDetailDomainModel(
    stars = stargazersCount,
    forks = forks
)

fun JsonAdapter<Map<String, Int>>.languageJsonMapToDomainModel(json: String) = GitHubRepositoryLanguageDomainModel(
    fromJson(json)
)
