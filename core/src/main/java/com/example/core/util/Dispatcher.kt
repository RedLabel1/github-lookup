package com.example.core.util

import javax.inject.Qualifier

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class Dispatcher(val dispatcher: GitHubLookupDispatchers)

enum class GitHubLookupDispatchers {
    Default,
    IO,
}
