package com.example.data

import com.example.data.mapper.languageJsonMapToDomainModel
import com.example.data.mapper.toDomainModel
import com.example.data.model.GitHubRepositoryDataModel
import com.example.data.model.GitHubRepositoryDetailDataModel
import com.example.data.network.ApiClient
import com.example.data.repository.GitHubRepositoryImpl
import com.squareup.moshi.JsonAdapter
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.IOException
import kotlin.test.assertFailsWith

class GitHubRepositoryImplTest {

    private val apiClient: ApiClient = mockk()
    private val adapter: JsonAdapter<Map<String, Int>> = mockk()

    private val cut = GitHubRepositoryImpl(apiClient, adapter)

    @Test
    fun `getAllRepositories successfully returns a list of GitHub repos`() = runTest {
        val gitHubRepository: GitHubRepositoryDataModel = mockk(relaxed = true)
        coEvery { apiClient.api.allRepositories() } returns listOf(gitHubRepository)
        assertEquals(cut.getAllRepositories(), listOf(gitHubRepository.toDomainModel()))
    }

    @Test
    fun `getAllRepositories returns an empty list of GitHub repos`() = runTest {
        coEvery { apiClient.api.allRepositories() } returns emptyList()
        assertTrue(cut.getAllRepositories() == emptyList<GitHubRepositoryDataModel>())
    }

    @Test
    fun `getAllRepositories throws an exception`() = runTest {
        coEvery { apiClient.api.allRepositories() } throws IOException()
        assertFailsWith<IOException> { cut.getAllRepositories() }
    }

    @Test
    fun `getRepository successfully returns a single GitHub repo`() = runTest {
        val gitHubRepository: GitHubRepositoryDetailDataModel = mockk(relaxed = true)
        coEvery { apiClient.api.repositoryStarsAndForks(any(), any()) } returns gitHubRepository
        assertEquals(cut.getRepositoryStarsAndForks(""), gitHubRepository.toDomainModel())
    }

    @Test
    fun `getRepository throws an exception`() = runTest {
        coEvery { apiClient.api.repositoryStarsAndForks(any(), any()) } throws IOException()
        assertFailsWith<IOException> { cut.getRepositoryStarsAndForks("") }
    }

    @Test
    fun `getRepositoryLanguage successfully returns a language map`() = runTest {
        coEvery { apiClient.api.repositoryLanguages(any(), any()) } returns ""
        coEvery { adapter.fromJson(any<String>()) } returns mapOf("my_language" to 0)
        assertEquals(cut.getRepositoryLanguage(""), adapter.languageJsonMapToDomainModel(""))
    }

    @Test
    fun `getRepositoryLanguage throws an exception`() = runTest {
        coEvery { apiClient.api.repositoryLanguages(any(), any()) } throws IOException()
        assertFailsWith<IOException> { cut.getRepositoryLanguage("") }
    }
}
