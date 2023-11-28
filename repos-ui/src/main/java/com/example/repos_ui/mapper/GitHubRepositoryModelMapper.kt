package com.example.repos_ui.mapper

import com.example.domain.model.GitHubRepositoryDetailDomainModel
import com.example.domain.model.GitHubRepositoryDomainModel
import com.example.repos_ui.model.GitHubRepositoryUIModel

fun GitHubRepositoryDomainModel.toUIModel() = GitHubRepositoryUIModel(
    name = name,
    ownerAvatar = ownerAvatar,
    description = description,
    forks = null,
    stars = null,
    language = null,
    url = url,
    htmlUrl = htmlUrl,
    languagesUrl = languagesUrl,
)

fun GitHubRepositoryUIModel.fill(with: GitHubRepositoryDetailDomainModel) = copy(
    forks = with.forks,
    stars = with.stars
)

fun GitHubRepositoryUIModel.fillWithLanguage(language: String?) = copy(
    language = language
)
