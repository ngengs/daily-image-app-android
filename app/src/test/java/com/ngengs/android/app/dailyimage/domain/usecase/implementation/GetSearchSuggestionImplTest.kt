package com.ngengs.android.app.dailyimage.domain.usecase.implementation

import com.ngengs.android.app.dailyimage.helpers.fake.FakeDispatcherProvider
import com.ngengs.android.app.dailyimage.helpers.fake.data.repository.FakeSearchRepository
import com.ngengs.android.libs.test.utils.ext.shouldBe
import com.ngengs.android.libs.test.utils.ext.shouldBeEmpty
import com.ngengs.android.libs.test.utils.rules.CoroutineRule
import fr.xgouchet.elmyr.junit4.ForgeRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class GetSearchSuggestionImplTest {

    @get:Rule
    var forge = ForgeRule()

    @get:Rule
    var coroutineRule = CoroutineRule()

    private val dispatcherProvider by lazy { FakeDispatcherProvider(coroutineRule.dispatcher) }
    private val fakeRepository = FakeSearchRepository()
    private lateinit var useCase: GetSearchSuggestionImpl

    @Before
    fun setUp() {
        useCase = GetSearchSuggestionImpl(fakeRepository, dispatcherProvider)
    }

    @After
    fun tearDown() {
        fakeRepository.reset()
    }

    @Test
    fun useCase_returnEmpty_whenTextLowerThanThreshold() = runTest {
        // Given
        val text = forge.anAlphabeticalString(size = GetSearchSuggestionImpl.LENGTH_THRESHOLD - 1)

        // When
        val result = useCase.invoke(text)

        // Then
        result.shouldBeEmpty()
    }

    @Test
    fun useCase_returnEmpty_whenTextEmpty() = runTest {
        // Given
        val text = forge.aWhitespaceString(GetSearchSuggestionImpl.LENGTH_THRESHOLD * 2)

        // When
        val result = useCase.invoke(text)

        // Then
        result.shouldBeEmpty()
    }

    @Test
    fun useCase_success() = runTest {
        // Given
        val data = (1..10).map { forge.anAlphabeticalString(size = 20) }
        fakeRepository.searchSuggestion = data
        val text = forge.anAlphabeticalString(size = GetSearchSuggestionImpl.LENGTH_THRESHOLD * 2)

        // When
        val result = useCase.invoke(text)

        // Then
        result shouldBe data
    }
}