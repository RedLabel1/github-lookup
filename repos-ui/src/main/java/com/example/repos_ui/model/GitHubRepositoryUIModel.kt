package com.example.repos_ui.model

data class GitHubRepositoryUIModel(
    val name: String?,
    val ownerAvatar: String?,
    val description: String?,
    val stars: Int?,
    val forks: Int?,
    val language: String?,
    val url: String?,
    val htmlUrl: String?,
    val languagesUrl: String?
)
