package com.example.domain.usecase

import com.example.core.util.Dispatcher
import com.example.core.util.GitHubLookupDispatchers.IO
import com.example.domain.UseCase
import com.example.domain.model.GitHubRepositoryDomainModel
import com.example.domain.repository.GitHubRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class GetAllRepositories @Inject constructor(
    private val gitHubRepository: GitHubRepository,
    @Dispatcher(IO) private val ioDispatcher: CoroutineDispatcher,
) : UseCase<Unit, List<GitHubRepositoryDomainModel>>() {

    override suspend fun doWork(params: Unit) = withContext(ioDispatcher) {
        gitHubRepository.getAllRepositories()
    }
}
