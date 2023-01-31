package com.ngengs.android.app.dailyimage.di

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
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

/**
 * Created by rizky.kharisma on 11/01/23.
 * @ngengs
 */
@Module
@InstallIn(ViewModelComponent::class)
abstract class UseCaseModule {
    @Binds
    @ViewModelScoped
    abstract fun bindGetPhotoListUseCase(impl: GetPhotoListUseCaseImpl): GetPhotoListUseCase

    @Binds
    @ViewModelScoped
    abstract fun bindGetSearchSuggestionUseCase(impl: GetSearchSuggestionImpl): GetSearchSuggestion

    @Binds
    @ViewModelScoped
    abstract fun bindGetSearchedPhotoUseCase(
        impl: GetSearchedPhotoUseCaseImpl
    ): GetSearchedPhotoUseCase

    @Binds
    @ViewModelScoped
    abstract fun bindGetFavoriteStatusUseCase(
        impl: GetFavoriteStatusUseCaseImpl
    ): GetFavoriteStatusUseCase

    @Binds
    @ViewModelScoped
    abstract fun bindChangeFavoriteStatusUseCase(
        impl: ChangeFavoriteStatusUseCaseImpl
    ): ChangeFavoriteStatusUseCase

    @Binds
    @ViewModelScoped
    abstract fun bindGetFavoriteListUseCase(
        impl: GetFavoriteListUseCaseImpl
    ): GetFavoriteListUseCase
}