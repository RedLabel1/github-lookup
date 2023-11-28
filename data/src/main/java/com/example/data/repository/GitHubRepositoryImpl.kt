package com.example.data.repository

import com.example.data.mapper.languageJsonMapToDomainModel
import com.example.data.mapper.toDomainModel
import com.example.data.network.ApiClient
import com.example.domain.repository.GitHubRepository
import com.squareup.moshi.JsonAdapter
import javax.inject.Inject

class GitHubRepositoryImpl @Inject constructor(
    private val apiClient: ApiClient,
    private val adapter: JsonAdapter<Map<String, Int>>
) : GitHubRepository {

    override suspend fun getAllRepositories() =
        apiClient.api.allRepositories().map { it.toDomainModel() }

    override suspend fun getRepositoryStarsAndForks(url: String) =
        apiClient.api.repositoryStarsAndForks(url).toDomainModel()

    override suspend fun getRepositoryLanguage(url: String) =
        adapter.languageJsonMapToDomainModel(apiClient.api.repositoryLanguages(url))
}
