package com.ngengs.android.app.dailyimage.di

import com.ngengs.android.app.dailyimage.domain.usecase.GetPhotoListUseCase
import com.ngengs.android.app.dailyimage.domain.usecase.GetSearchSuggestion
import com.ngengs.android.app.dailyimage.domain.usecase.GetSearchedPhotoUseCase
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

    data class FakeUseCaseHolder(
        val getSearchedPhotoUseCase: FakeGetSearchedPhotoUseCase = FakeGetSearchedPhotoUseCase(),
        val getPhotoListUseCase: FakeGetPhotoListUseCase = FakeGetPhotoListUseCase(),
        val getSearchSuggestionUseCase: FakeGetSearchSuggestionUseCase =
            FakeGetSearchSuggestionUseCase()
    )

    companion object {
        val useCase = FakeUseCaseHolder()

        fun tearDown() {
            useCase.getPhotoListUseCase.reset()
            useCase.getSearchSuggestionUseCase.reset()
            useCase.getSearchedPhotoUseCase.reset()
        }
    }
}