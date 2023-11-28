package com.example.domain

import com.example.domain.model.GitHubRepositoryDetailDomainModel
import com.example.domain.repository.GitHubRepository
import com.example.domain.usecase.GetRepositoryStarsAndForks
import com.example.domain.usecase.GetRepositoryStarsAndForks.Params
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Test
import java.io.IOException

class GetRepositoryStarsAndForksTest {

    private val transactionsRepository: GitHubRepository = mockk()
    private val testDispatcher = Dispatchers.Unconfined

    private val cut = GetRepositoryStarsAndForks(transactionsRepository, testDispatcher)

    @Test
    fun `GetRepository returns successful response`() = runTest {
        val response: GitHubRepositoryDetailDomainModel = mockk()
        coEvery { transactionsRepository.getRepositoryStarsAndForks(any()) } returns response
        cut(Params("")).onSuccess {
            Assert.assertEquals(it, response)
        }
    }

    @Test
    fun `GetRepository returns failure response`() = runTest {
        coEvery { transactionsRepository.getRepositoryStarsAndForks(any()) } throws IOException()
        cut(Params("")).onFailure {
            assert(it is IOException)
        }
    }
}
