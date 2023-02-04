package com.ngengs.android.app.dailyimage.presenter

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * Created by rizky.kharisma on 12/01/23.
 * @ngengs
 */
abstract class BaseViewModel<T>(initial: T) : ViewModel() {
    // Runner
    private var mainJob: Job? = null

    // Data declaration
    protected val _data: MutableStateFlow<T> = MutableStateFlow(initial)
    open val data: StateFlow<T> = _data.asStateFlow()

    protected fun safeRunJob(
        dispatcher: CoroutineDispatcher,
        task: suspend CoroutineScope.() -> Unit,
    ) {
        stopRunningJob()
        mainJob = viewModelScope.launch(dispatcher) {
            task.invoke(this)
        }
    }

    @VisibleForTesting(otherwise = VisibleForTesting.PROTECTED)
    fun stopRunningJob() {
        if (mainJob?.isActive == true) mainJob?.cancel()
    }

    @VisibleForTesting
    fun setInitialData(initial: T) {
        _data.value = initial
    }
}