package com.ngengs.android.app.dailyimage.di

import com.ngengs.android.app.dailyimage.domain.usecase.ChangeFavoriteStatusUseCase
import com.ngengs.android.app.dailyimage.domain.usecase.GetFavoriteListUseCase
import com.ngengs.android.app.dailyimage.domain.usecase.GetFavoriteStatusUseCase
import com.ngengs.android.app.dailyimage.domain.usecase.GetPhotoListUseCase
import com.ngengs.android.app.dailyimage.domain.usecase.GetSearchSuggestion
import com.ngengs.android.app.dailyimage.domain.usecase.GetSearchedPhotoUseCase
import com.ngengs.android.app.dailyimage.helpers.fake.domain.usecase.FakeChangeFavoriteStatusUseCase
import com.ngengs.android.app.dailyimage.helpers.fake.domain.usecase.FakeGetFavoriteListUseCase
import com.ngengs.android.app.dailyimage.helpers.fake.domain.usecase.FakeGetFavoriteStatusUseCase
import com.ngengs.android.app.dailyimage.helpers.fake.domain.usecase.FakeGetPhotoListUseCase
import com.ngengs.android.app.dailyimage.helpers.fake.domain.usecase.FakeGetSearchSuggestionUseCase
import com.ngengs.android.app.dailyimage.helpers.fake.domain.usecase.FakeGetSearchedPhotoUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped
import dagger.hilt.testing.TestInstallIn

@Module
@TestInstallIn(
    components = [ViewModelComponent::class],
    replaces = [UseCaseModule::class]
)
class FakeUseCaseModule {
    @Provides
    @ViewModelScoped
    fun provideGetPhotoListUseCase(): GetPhotoListUseCase = useCase.getPhotoListUseCase

    @Provides
    @ViewModelScoped
    fun provideGetSearchSuggestionUseCase(): GetSearchSuggestion =
        useCase.getSearchSuggestionUseCase

    @Provides
    @ViewModelScoped
    fun provideGetSearchedPhotoUseCase(): GetSearchedPhotoUseCase = useCase.getSearchedPhotoUseCase

    @Provides
    @ViewModelScoped
    fun provideGetFavoriteStatusUseCase(): GetFavoriteStatusUseCase =
        useCase.getFavoriteStatusUseCase

    @Provides
    @ViewModelScoped
    fun provideFavoriteStatusUseCase(): ChangeFavoriteStatusUseCase =
        useCase.changeFavoriteStatusUseCase

    @Provides
    @ViewModelScoped
    fun provideGetFavoriteListUseCase(): GetFavoriteListUseCase = useCase.getFavoriteListUseCase


    data class FakeUseCaseHolder(
        val getSearchedPhotoUseCase: FakeGetSearchedPhotoUseCase = FakeGetSearchedPhotoUseCase(),
        val getPhotoListUseCase: FakeGetPhotoListUseCase = FakeGetPhotoListUseCase(),
        val getSearchSuggestionUseCase: FakeGetSearchSuggestionUseCase =
            FakeGetSearchSuggestionUseCase(),
        val changeFavoriteStatusUseCase: FakeChangeFavoriteStatusUseCase =
            FakeChangeFavoriteStatusUseCase(),
        val getFavoriteListUseCase: FakeGetFavoriteListUseCase = FakeGetFavoriteListUseCase(),
        val getFavoriteStatusUseCase: FakeGetFavoriteStatusUseCase = FakeGetFavoriteStatusUseCase(),
    )

    companion object {
        lateinit var useCase: FakeUseCaseHolder
//        val useCase = FakeUseCaseHolder()

        fun init() {
            useCase = FakeUseCaseHolder()
        }

        fun tearDown() {
            useCase.getPhotoListUseCase.reset()
            useCase.getSearchSuggestionUseCase.reset()
            useCase.getSearchedPhotoUseCase.reset()
            useCase.changeFavoriteStatusUseCase.reset()
            useCase.getFavoriteListUseCase.reset()
            useCase.getFavoriteStatusUseCase.reset()
        }
    }
}