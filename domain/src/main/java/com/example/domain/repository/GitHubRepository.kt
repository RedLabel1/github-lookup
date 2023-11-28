package com.example.domain.repository

import com.example.domain.model.GitHubRepositoryDetailDomainModel
import com.example.domain.model.GitHubRepositoryDomainModel
import com.example.domain.model.GitHubRepositoryLanguageDomainModel

interface GitHubRepository {

    suspend fun getAllRepositories(): List<GitHubRepositoryDomainModel>

    suspend fun getRepositoryStarsAndForks(url: String): GitHubRepositoryDetailDomainModel

    suspend fun getRepositoryLanguage(url: String): GitHubRepositoryLanguageDomainModel
}
