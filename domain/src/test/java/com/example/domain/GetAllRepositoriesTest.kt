package com.example.domain

import com.example.domain.model.GitHubRepositoryDomainModel
import com.example.domain.repository.GitHubRepository
import com.example.domain.usecase.GetAllRepositories
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.runTest
import org.junit.Test

import org.junit.Assert.*
import java.io.IOException

class GetAllRepositoriesTest {

    private val transactionsRepository: GitHubRepository = mockk()
    private val testDispatcher = Dispatchers.Unconfined

    private val cut = GetAllRepositories(transactionsRepository, testDispatcher)

    @Test
    fun `GetAllRepositories returns successful response`() = runTest {
        val response: List<GitHubRepositoryDomainModel> = mockk()
        coEvery { transactionsRepository.getAllRepositories() } returns response
        cut().onSuccess {
            assertEquals(it, response)
        }
    }

    @Test
    fun `GetAllRepositories returns failure response`() = runTest {
        coEvery { transactionsRepository.getAllRepositories() } throws IOException()
        cut().onFailure {
            assert(it is IOException)
        }
    }
}
