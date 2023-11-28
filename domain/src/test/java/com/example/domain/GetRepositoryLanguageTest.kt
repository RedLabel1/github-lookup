package com.example.domain

import com.example.domain.model.GitHubRepositoryLanguageDomainModel
import com.example.domain.repository.GitHubRepository
import com.example.domain.usecase.GetRepositoryLanguage
import com.example.domain.usecase.GetRepositoryLanguage.Params
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Test
import java.io.IOException

class GetRepositoryLanguageTest {

    private val transactionsRepository: GitHubRepository = mockk()
    private val testDispatcher = Dispatchers.Unconfined

    private val cut = GetRepositoryLanguage(transactionsRepository, testDispatcher)

    @Test
    fun `GetRepositoryLanguage returns successful response`() = runTest {
        val response: GitHubRepositoryLanguageDomainModel = mockk {
            every { languages } returns mapOf("my_language" to 0)
        }
        coEvery { transactionsRepository.getRepositoryLanguage(any()) } returns response
        cut(Params("")).onSuccess {
            Assert.assertEquals(it, response.languages?.entries?.first()?.key)
        }
    }

    @Test
    fun `GetRepositoryLanguage returns null response`() = runTest {
        val response: GitHubRepositoryLanguageDomainModel = mockk {
            every { languages } returns null
        }
        coEvery { transactionsRepository.getRepositoryLanguage(any()) } returns response
        cut(Params("")).onSuccess {
            Assert.assertEquals(it, null)
        }
    }

    @Test
    fun `GetRepositoryLanguage returns failure response`() = runTest {
        coEvery { transactionsRepository.getRepositoryLanguage(any()) } throws IOException()
        cut(Params("")).onFailure {
            assert(it is IOException)
        }
    }
}
