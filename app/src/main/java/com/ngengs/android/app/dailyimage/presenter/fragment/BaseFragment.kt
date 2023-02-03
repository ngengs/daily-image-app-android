package com.ngengs.android.app.dailyimage.presenter.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import timber.log.Timber

/**
 * Created by rizky.kharisma on 12/01/23.
 * @ngengs
 */
abstract class BaseFragment<VB : ViewBinding> : Fragment() {
    protected abstract val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> VB

    private var _binding: VB? = null
    protected val binding: VB get() = _binding!!

    protected open val screenName: String get() = javaClass.simpleName
    protected val log get() = Timber.tag(screenName)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        log.d("onCreateView: base")
        _binding = bindingInflater(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        log.d("onViewCreated: base")
        initializeView()
    }

    override fun onDestroyView() {
        log.d("onDestroyView: base")
        _binding = null
        super.onDestroyView()
    }

    protected abstract fun initializeView()
}