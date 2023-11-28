package com.example.domain

import kotlinx.coroutines.withTimeout
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

abstract class UseCase<in P, out R> {

    suspend operator fun invoke(
        params: P,
        timeout: Duration = DefaultTimeout,
    ): Result<R> =
        runCatching {
            withTimeout(timeout) {
                doWork(params)
            }
        }

    protected abstract suspend fun doWork(params: P): R

    companion object {
        internal val DefaultTimeout = 5.seconds
    }
}

suspend operator fun <R> UseCase<Unit, R>.invoke(
    timeout: Duration = UseCase.DefaultTimeout,
) = invoke(Unit, timeout)
