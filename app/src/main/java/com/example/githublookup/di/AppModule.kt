package com.example.githublookup.di

import com.example.core.util.Dispatcher
import com.example.core.util.GitHubLookupDispatchers.Default
import com.example.core.util.GitHubLookupDispatchers.IO
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

@InstallIn(SingletonComponent::class)
@Module
class AppModule {

    @Provides
    @Dispatcher(IO)
    fun provideIODispatcher(): CoroutineDispatcher = Dispatchers.IO

    @Provides
    @Dispatcher(Default)
    fun provideDefaultDispatcher(): CoroutineDispatcher = Dispatchers.Default
}
