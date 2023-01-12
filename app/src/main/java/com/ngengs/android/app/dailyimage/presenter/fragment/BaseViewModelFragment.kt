package com.ngengs.android.app.dailyimage.presenter.fragment

import android.os.Bundle
import android.view.View
import androidx.annotation.VisibleForTesting
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.viewbinding.ViewBinding
import com.ngengs.android.app.dailyimage.presenter.BaseViewModel
import kotlinx.coroutines.launch
import timber.log.Timber

/**
 * Created by rizky.kharisma on 12/01/23.
 * @ngengs
 */
abstract class BaseViewModelFragment<VB : ViewBinding, VD, VM : BaseViewModel<VD>> :
    BaseFragment<VB>() {

    protected abstract val viewModel: VM

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Timber.d("onViewCreated")
        observeData()
    }

    private fun observeData() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.data
                .flowWithLifecycle(lifecycle, Lifecycle.State.RESUMED)
                .collect { render(it) }
        }
    }

    protected open fun render(data: VD) = Unit

    @VisibleForTesting
    fun setInitialData(initialData: VD) {
        viewModel.setInitialData(initialData)
    }
}