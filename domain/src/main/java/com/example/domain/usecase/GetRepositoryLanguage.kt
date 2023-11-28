package com.example.domain.usecase

import com.example.core.util.Dispatcher
import com.example.core.util.GitHubLookupDispatchers.IO
import com.example.domain.UseCase
import com.example.domain.repository.GitHubRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class GetRepositoryLanguage @Inject constructor(
    private val gitHubRepository: GitHubRepository,
    @Dispatcher(IO) private val ioDispatcher: CoroutineDispatcher,
) : UseCase<GetRepositoryLanguage.Params, String?>() {

    override suspend fun doWork(params: Params) = withContext(ioDispatcher) {
        gitHubRepository.getRepositoryLanguage(params.url).languages?.maxBy { it.value }?.key
    }

    data class Params(val url: String)
}
