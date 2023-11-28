package com.example.repos_ui.home

import app.cash.turbine.test
import com.example.domain.invoke
import com.example.domain.model.GitHubRepositoryDetailDomainModel
import com.example.domain.model.GitHubRepositoryDomainModel
import com.example.domain.usecase.GetAllRepositories
import com.example.domain.usecase.GetRepositoryLanguage
import com.example.domain.usecase.GetRepositoryStarsAndForks
import com.example.repos_ui.mapper.fill
import com.example.repos_ui.mapper.toUIModel
import com.example.repos_ui.model.GitHubRepositoryUIModel
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Test
import java.io.IOException
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class GitHubRepositoriesHomeViewModelTest {

    private val getAllRepositories: GetAllRepositories = mockk()
    private val getRepositoryStarsAndForks: GetRepositoryStarsAndForks = mockk()
    private val getRepositoryLanguage: GetRepositoryLanguage = mockk()
    private val testDispatcher = Dispatchers.Unconfined

    init {
        Dispatchers.setMain(testDispatcher)
    }

    private val cut
        get() = GitHubRepositoriesHomeViewModel(
            getAllRepositories = getAllRepositories,
            getRepositoryStarsAndForks = getRepositoryStarsAndForks,
            getRepositoryLanguage = getRepositoryLanguage,
            defaultDispatcher = testDispatcher
        )

    @Test
    fun `fetchAllRepositories emits successful response on init`() = runTest {
        val getAllRepositoriesResponse: GitHubRepositoryDomainModel = mockk(relaxed = true)
        val getRepositoryStarsAndForksResponse: GitHubRepositoryDetailDomainModel = mockk(relaxed = true)
        coEvery { getAllRepositories() } returns Result.success(listOf(getAllRepositoriesResponse))
        coEvery { getRepositoryStarsAndForks(any()) } returns Result.success(getRepositoryStarsAndForksResponse)
        cut.state.test {
            val response = awaitItem()
            assertEquals(
                expected = listOf(getAllRepositoriesResponse.toUIModel().fill(with = getRepositoryStarsAndForksResponse)),
                actual = response.gitHubRepositories
            )
            assertEquals(
                expected = NoError,
                actual = response.error
            )
            coVerify(exactly = 1) { getAllRepositories() }
            coVerify(exactly = 1) { getRepositoryStarsAndForks(any()) }
            confirmVerified(getAllRepositories, getRepositoryStarsAndForks)
        }
    }

    @Test
    fun `fetchAllRepositories emits descending sorted results`() = runTest {
        val getAllRepositoriesResponse: GitHubRepositoryDomainModel = mockk(relaxed = true)
        val getRepositoryStarsAndForksResponse1: GitHubRepositoryDetailDomainModel = mockk(relaxed = true) {
            every { stars } returns 0
        }
        val getRepositoryStarsAndForksResponse2: GitHubRepositoryDetailDomainModel = mockk(relaxed = true) {
            every { stars } returns 9
        }
        val getRepositoryStarsAndForksResponse3: GitHubRepositoryDetailDomainModel = mockk(relaxed = true) {
            every { stars } returns 3
        }
        val getRepositoryStarsAndForksResponses = listOf(
            Result.success(getRepositoryStarsAndForksResponse1),
            Result.success(getRepositoryStarsAndForksResponse2),
            Result.success(getRepositoryStarsAndForksResponse3)
        )
        coEvery { getAllRepositories() } returns Result.success(
            listOf(
                getAllRepositoriesResponse,
                getAllRepositoriesResponse,
                getAllRepositoriesResponse
            )
        )
        coEvery { getRepositoryStarsAndForks(any()) } returnsMany (getRepositoryStarsAndForksResponses)
        cut.state.test {
            assertEquals(
                expected = listOf(9, 3, 0),
                actual = awaitItem().gitHubRepositories.map { it.stars }
            )
            coVerify(exactly = 1) { getAllRepositories() }
            coVerify(exactly = 3) { getRepositoryStarsAndForks(any()) }
            confirmVerified(getAllRepositories, getRepositoryStarsAndForks)
        }
    }

    @Test
    fun `fetchAllRepositories emits NetworkError on fetchAllRepositories exception`() = runTest {
        coEvery { getAllRepositories() } returns Result.failure(IOException())
        cut.state.test {
            assert(awaitItem().error is NetworkError)
            coVerify(exactly = 1) { getAllRepositories() }
            coVerify(exactly = 0) { getRepositoryStarsAndForks(any()) }
            confirmVerified(getAllRepositories, getRepositoryStarsAndForks)
        }
    }

    @Test
    fun `fetchAllRepositories emits NetworkError on fetchRepositoriesStarsAndForks exception`() = runTest {
        val getAllRepositoriesResponse: GitHubRepositoryDomainModel = mockk(relaxed = true)
        coEvery { getAllRepositories() } returns Result.success(listOf(getAllRepositoriesResponse))
        coEvery { getRepositoryStarsAndForks(any()) } returns Result.failure(IOException())
        cut.state.test {
            assert(awaitItem().error is NetworkError)
            coVerify(exactly = 1) { getAllRepositories() }
            coVerify(exactly = 1) { getRepositoryStarsAndForks(any()) }
            confirmVerified(getAllRepositories, getRepositoryStarsAndForks)
        }
    }

    @Test
    fun `onItemSelected emits selected repo`() = runTest {
        val selectedItem: GitHubRepositoryUIModel = mockk()
        val getAllRepositoriesResponse: GitHubRepositoryDomainModel = mockk(relaxed = true)
        val getRepositoryStarsAndForksResponse: GitHubRepositoryDetailDomainModel = mockk(relaxed = true)
        coEvery { getAllRepositories() } returns Result.success(listOf(getAllRepositoriesResponse))
        coEvery { getRepositoryStarsAndForks(any()) } returns Result.success(getRepositoryStarsAndForksResponse)
        cut.apply {
            state.test {
                onItemSelected(selectedItem)
                awaitItem() // getRepositoryStarsAndForks
                assertEquals(
                    expected = selectedItem,
                    actual = awaitItem().selectedRepository
                )
                coVerify(exactly = 1) { getAllRepositories() }
                coVerify(exactly = 1) { getRepositoryStarsAndForks(any()) }
                confirmVerified(getAllRepositories, getRepositoryStarsAndForks)
            }
        }
    }

    @Test
    fun `fetchSelectedRepositoryTopLanguage emits successful response`() = runTest {
        val languageResponse = "my_language"
        val getAllRepositoriesResponse: GitHubRepositoryDomainModel = mockk(relaxed = true)
        val getRepositoryStarsAndForksResponse: GitHubRepositoryDetailDomainModel = mockk(relaxed = true)
        val selectedItem = getAllRepositoriesResponse.toUIModel().fill(with = getRepositoryStarsAndForksResponse)
        coEvery { getAllRepositories() } returns Result.success(listOf(getAllRepositoriesResponse))
        coEvery { getRepositoryStarsAndForks(any()) } returns Result.success(getRepositoryStarsAndForksResponse)
        coEvery { getRepositoryLanguage(any()) } returns Result.success(languageResponse)
        cut.apply {
            state.test {
                onItemSelected(selectedItem)
                fetchSelectedRepositoryTopLanguage()
                awaitItem() // getRepositoryStarsAndForks
                awaitItem() // selectedRepository
                assertEquals(
                    expected = languageResponse,
                    actual = awaitItem().selectedRepository?.language
                )
                coVerify(exactly = 1) { getAllRepositories() }
                coVerify(exactly = 1) { getRepositoryStarsAndForks(any()) }
                coVerify(exactly = 1) { getRepositoryLanguage(any()) }
                confirmVerified(getAllRepositories, getRepositoryStarsAndForks, getRepositoryLanguage)
            }
        }
    }

    @Test
    fun `fetchSelectedRepositoryTopLanguage emits failure response`() = runTest {
        val getAllRepositoriesResponse: GitHubRepositoryDomainModel = mockk(relaxed = true)
        val getRepositoryStarsAndForksResponse: GitHubRepositoryDetailDomainModel = mockk(relaxed = true)
        val selectedItem = getAllRepositoriesResponse.toUIModel().fill(with = getRepositoryStarsAndForksResponse)
        coEvery { getAllRepositories() } returns Result.success(listOf(getAllRepositoriesResponse))
        coEvery { getRepositoryStarsAndForks(any()) } returns Result.success(getRepositoryStarsAndForksResponse)
        coEvery { getRepositoryLanguage(any()) } returns Result.failure(IOException())
        cut.apply {
            state.test {
                onItemSelected(selectedItem)
                fetchSelectedRepositoryTopLanguage()
                awaitItem() // getRepositoryStarsAndForks
                awaitItem() // selectedRepository
                assert(awaitItem().error is NetworkError)
                coVerify(exactly = 1) { getAllRepositories() }
                coVerify(exactly = 1) { getRepositoryStarsAndForks(any()) }
                coVerify(exactly = 1) { getRepositoryLanguage(any()) }
                confirmVerified(getAllRepositories, getRepositoryStarsAndForks, getRepositoryLanguage)
            }
        }
    }

    @Test
    fun `onLookupButtonClicked emits true to lookupAction state`() = runTest {
        val getAllRepositoriesResponse: GitHubRepositoryDomainModel = mockk(relaxed = true)
        val getRepositoryStarsAndForksResponse: GitHubRepositoryDetailDomainModel = mockk(relaxed = true)
        val selectedItem = getAllRepositoriesResponse.toUIModel().fill(with = getRepositoryStarsAndForksResponse)
        coEvery { getAllRepositories() } returns Result.success(listOf(getAllRepositoriesResponse))
        coEvery { getRepositoryStarsAndForks(any()) } returns Result.success(getRepositoryStarsAndForksResponse)
        cut.apply {
            state.test {
                onItemSelected(selectedItem)
                onLookupButtonClicked()
                awaitItem() // getRepositoryStarsAndForks
                awaitItem() // selectedRepository
                assertTrue(awaitItem().lookupAction)
                coVerify(exactly = 1) { getAllRepositories() }
                coVerify(exactly = 1) { getRepositoryStarsAndForks(any()) }
                confirmVerified(getAllRepositories, getRepositoryStarsAndForks)
            }
        }
    }

    @Test
    fun `restoreLookupState emits false to lookupAction state`() = runTest {
        val getAllRepositoriesResponse: GitHubRepositoryDomainModel = mockk(relaxed = true)
        val getRepositoryStarsAndForksResponse: GitHubRepositoryDetailDomainModel = mockk(relaxed = true)
        val selectedItem = getAllRepositoriesResponse.toUIModel().fill(with = getRepositoryStarsAndForksResponse)
        coEvery { getAllRepositories() } returns Result.success(listOf(getAllRepositoriesResponse))
        coEvery { getRepositoryStarsAndForks(any()) } returns Result.success(getRepositoryStarsAndForksResponse)
        cut.apply {
            state.test {
                onItemSelected(selectedItem)
                onLookupButtonClicked()
                restoreLookupState()
                awaitItem() // getRepositoryStarsAndForks
                awaitItem() // selectedRepository
                awaitItem() // lookupAction click
                assertFalse(awaitItem().lookupAction)
                coVerify(exactly = 1) { getAllRepositories() }
                coVerify(exactly = 1) { getRepositoryStarsAndForks(any()) }
                confirmVerified(getAllRepositories, getRepositoryStarsAndForks)
            }
        }
    }
}
