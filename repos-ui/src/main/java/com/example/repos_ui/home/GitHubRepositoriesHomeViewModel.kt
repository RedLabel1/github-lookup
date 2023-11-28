package com.example.repos_ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.core.util.Dispatcher
import com.example.core.util.GitHubLookupDispatchers.Default
import com.example.domain.invoke
import com.example.domain.usecase.GetAllRepositories
import com.example.domain.usecase.GetRepositoryLanguage
import com.example.domain.usecase.GetRepositoryStarsAndForks
import com.example.repos_ui.mapper.fill
import com.example.repos_ui.mapper.fillWithLanguage
import com.example.repos_ui.mapper.toUIModel
import com.example.repos_ui.model.GitHubRepositoryUIModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.concurrent.atomic.AtomicInteger
import javax.inject.Inject

@HiltViewModel
class GitHubRepositoriesHomeViewModel @Inject constructor(
    private val getAllRepositories: GetAllRepositories,
    private val getRepositoryStarsAndForks: GetRepositoryStarsAndForks,
    private val getRepositoryLanguage: GetRepositoryLanguage,
    @Dispatcher(Default) private val defaultDispatcher: CoroutineDispatcher
) : ViewModel() {

    private val callCount = AtomicInteger(0)
    private val loadingState = MutableStateFlow(callCount.get())
    private val repositoriesState = MutableStateFlow<List<GitHubRepositoryUIModel>>(emptyList())
    private val selectedRepositoryState = MutableStateFlow<GitHubRepositoryUIModel?>(null)
    private val errorState = MutableStateFlow<Error>(NoError)
    private val lookupAction = MutableStateFlow(false)

    val state = combine(
        repositoriesState,
        selectedRepositoryState,
        loadingState,
        errorState,
        lookupAction
    ) { repositories, selectedRepository, loading, error, lookupAction ->
        GitHubRepositoriesHomeViewState(
            gitHubRepositories = repositories,
            selectedRepository = selectedRepository,
            isLoading = loading > 0,
            error = error,
            lookupAction = lookupAction
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), GitHubRepositoriesHomeViewState.Empty)

    init {
        fetchAllRepositories()
    }

    fun onItemSelected(item: GitHubRepositoryUIModel) = selectedRepositoryState.update { item }

    private fun fetchAllRepositories() = viewModelScope.launch {
        incrementLoadingState()
        getAllRepositories()
            .onSuccess { repos -> fetchRepositoriesStarsAndForks(repos.map { it.toUIModel() }.toMutableList()) }
            .onFailure { error -> errorState.update { NetworkError(error.message) } }
        decrementLoadingState()
    }

    private suspend fun fetchRepositoriesStarsAndForks(currentRepos: MutableList<GitHubRepositoryUIModel>) {
            currentRepos.forEachIndexed { index, currentRepo ->
                currentRepo.url?.let { starsAndForksUrl ->
                    incrementLoadingState()
                    getRepositoryStarsAndForks(GetRepositoryStarsAndForks.Params(starsAndForksUrl))
                        .onSuccess { remoteRepo -> currentRepos[index] = currentRepo.fill(with = remoteRepo) }
                        .onFailure { error -> errorState.update { NetworkError(error.message) } }
                    decrementLoadingState()
                }
            }
            updateSorted(results = currentRepos)
    }

    fun fetchSelectedRepositoryTopLanguage() = viewModelScope.launch {
        val currentRepos = repositoriesState.value.toMutableList()
        val selectedRepo = selectedRepositoryState.value
        if (selectedRepo?.language == null) {
            selectedRepo?.languagesUrl?.let { languagesUrl ->
                incrementLoadingState()
                getRepositoryLanguage(GetRepositoryLanguage.Params(languagesUrl))
                    .onSuccess { language ->
                        with(currentRepos) {
                            val resultRepo = selectedRepo.fillWithLanguage(language)
                            this[indexOf(firstOrNull { it == selectedRepo })] = resultRepo
                            selectedRepositoryState.update { resultRepo }
                            repositoriesState.update { currentRepos }
                        }
                    }
                    .onFailure { error -> errorState.update { NetworkError(error.message) } }
                decrementLoadingState()
            }
        }
    }

    fun onLookupButtonClicked() = viewModelScope.launch {
        lookupAction.emit(true)
    }

    fun restoreLookupState() = viewModelScope.launch {
        lookupAction.emit(false)
    }

    private fun incrementLoadingState() = loadingState.update { callCount.incrementAndGet() }

    private fun decrementLoadingState() = loadingState.update { callCount.decrementAndGet() }

    private fun updateSorted(results: List<GitHubRepositoryUIModel>) = viewModelScope.launch(defaultDispatcher) {
        repositoriesState.update { results.sortedByDescending { it.stars } }
    }
}

data class GitHubRepositoriesHomeViewState(
    val gitHubRepositories: List<GitHubRepositoryUIModel> = emptyList(),
    val selectedRepository: GitHubRepositoryUIModel? = null,
    val isLoading: Boolean = false,
    val error: Error = NoError,
    val lookupAction: Boolean = false
) {
    companion object {
        val Empty = GitHubRepositoriesHomeViewState()
    }
}

sealed interface Error
data object NoError : Error
data class NetworkError(val message: String?) : Error
