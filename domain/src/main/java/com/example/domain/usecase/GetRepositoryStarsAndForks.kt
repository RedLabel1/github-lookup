package com.example.domain.usecase

import com.example.core.util.Dispatcher
import com.example.core.util.GitHubLookupDispatchers.IO
import com.example.domain.UseCase
import com.example.domain.model.GitHubRepositoryDetailDomainModel
import com.example.domain.repository.GitHubRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class GetRepositoryStarsAndForks @Inject constructor(
    private val gitHubRepository: GitHubRepository,
    @Dispatcher(IO) private val ioDispatcher: CoroutineDispatcher,
) : UseCase<GetRepositoryStarsAndForks.Params, GitHubRepositoryDetailDomainModel>() {

    override suspend fun doWork(params: Params) = withContext(ioDispatcher) {
        gitHubRepository.getRepositoryStarsAndForks(params.url)
    }

    data class Params(val url: String)
}
