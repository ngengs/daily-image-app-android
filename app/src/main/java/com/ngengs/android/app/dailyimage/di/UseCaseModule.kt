package com.ngengs.android.app.dailyimage.di

import com.ngengs.android.app.dailyimage.data.repository.FavoriteRepository
import com.ngengs.android.app.dailyimage.data.repository.PhotoListRepository
import com.ngengs.android.app.dailyimage.data.repository.SearchRepository
import com.ngengs.android.app.dailyimage.domain.usecase.ChangeFavoriteStatusUseCase
import com.ngengs.android.app.dailyimage.domain.usecase.GetFavoriteListUseCase
import com.ngengs.android.app.dailyimage.domain.usecase.GetFavoriteStatusUseCase
import com.ngengs.android.app.dailyimage.domain.usecase.GetPhotoListUseCase
import com.ngengs.android.app.dailyimage.domain.usecase.GetSearchSuggestion
import com.ngengs.android.app.dailyimage.domain.usecase.GetSearchedPhotoUseCase
import com.ngengs.android.app.dailyimage.domain.usecase.implementation.ChangeFavoriteStatusUseCaseImpl
import com.ngengs.android.app.dailyimage.domain.usecase.implementation.GetFavoriteListUseCaseImpl
import com.ngengs.android.app.dailyimage.domain.usecase.implementation.GetFavoriteStatusUseCaseImpl
import com.ngengs.android.app.dailyimage.domain.usecase.implementation.GetPhotoListUseCaseImpl
import com.ngengs.android.app.dailyimage.domain.usecase.implementation.GetSearchSuggestionImpl
import com.ngengs.android.app.dailyimage.domain.usecase.implementation.GetSearchedPhotoUseCaseImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

/**
 * Created by rizky.kharisma on 11/01/23.
 * @ngengs
 */
@Module
@InstallIn(ViewModelComponent::class)
internal object UseCaseModule {
    @Provides
    @ViewModelScoped
    fun provideGetPhotoListUseCase(
        repository: PhotoListRepository,
        dispatcherProvider: DispatcherProvider
    ): GetPhotoListUseCase = GetPhotoListUseCaseImpl(repository, dispatcherProvider)

    @Provides
    @ViewModelScoped
    fun provideGetSearchSuggestionUseCase(
        repository: SearchRepository,
        dispatcherProvider: DispatcherProvider
    ): GetSearchSuggestion = GetSearchSuggestionImpl(repository, dispatcherProvider)

    @Provides
    @ViewModelScoped
    fun provideGetSearchedPhotoUseCase(
        repository: SearchRepository,
        dispatcherProvider: DispatcherProvider
    ): GetSearchedPhotoUseCase = GetSearchedPhotoUseCaseImpl(repository, dispatcherProvider)

    @Provides
    @ViewModelScoped
    fun provideGetFavoriteStatusUseCase(
        repository: FavoriteRepository,
        dispatcherProvider: DispatcherProvider
    ): GetFavoriteStatusUseCase = GetFavoriteStatusUseCaseImpl(repository, dispatcherProvider)

    @Provides
    @ViewModelScoped
    fun provideChangeFavoriteStatusUseCase(
        repository: FavoriteRepository,
        dispatcherProvider: DispatcherProvider
    ): ChangeFavoriteStatusUseCase = ChangeFavoriteStatusUseCaseImpl(repository, dispatcherProvider)

    @Provides
    @ViewModelScoped
    fun provideGetFavoriteListUseCase(
        repository: FavoriteRepository,
        dispatcherProvider: DispatcherProvider
    ): GetFavoriteListUseCase = GetFavoriteListUseCaseImpl(repository, dispatcherProvider)
}