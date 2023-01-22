package com.ngengs.android.app.dailyimage.presenter.fragment.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.activity.addCallback
import androidx.core.view.postDelayed
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.tabs.TabLayoutMediator
import com.ngengs.android.app.dailyimage.R
import com.ngengs.android.app.dailyimage.databinding.FragmentHomeBinding
import com.ngengs.android.app.dailyimage.presenter.fragment.BaseViewModelFragment
import com.ngengs.android.app.dailyimage.presenter.fragment.home.HomeViewModel.ViewData
import com.ngengs.android.app.dailyimage.presenter.shared.ui.delegation.SearchableScreen
import com.ngengs.android.app.dailyimage.presenter.shared.ui.delegation.implementation.SearchableScreenImpl
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : HomeFragmentImpl()

open class HomeFragmentImpl :
    BaseViewModelFragment<FragmentHomeBinding, ViewData, HomeViewModel>(),
    SearchableScreen by SearchableScreenImpl() {

    override val viewModel: HomeViewModel by viewModels()
    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentHomeBinding
        get() = FragmentHomeBinding::inflate
    private lateinit var onBackPressed: OnBackPressedCallback

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        onBackPressed = requireActivity().onBackPressedDispatcher.addCallback(this, false) {
            if (binding.searchView.isShown) binding.searchView.hide()
        }
    }

    override fun initializeView() {
        val tabTitles = resources.getStringArray(R.array.home_tab_title)
        val adapter = HomePagerAdapter(tabTitles.size, childFragmentManager, lifecycle)
        binding.viewPager.adapter = adapter
        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = tabTitles[position]
        }.attach()

        prepareSearch(
            context = requireContext(),
            searchBar = binding.searchBar,
            searchView = binding.searchView,
            searchViewList = binding.rvSuggestion,
            updateSearchBarTextOnSearch = false,
            onTypeHandler = { viewModel.onTypedSearch(it) },
            onCloseHandler = {
                onBackPressed.isEnabled = false
                viewModel.resetSearchSuggestion()
            },
            onOpenHandler = {
                onBackPressed.isEnabled = true
            },
            onSearchHandler = {
                binding.searchView.clearFocusAndHideKeyboard()
                binding.searchView.postDelayed(300L) {
                    val direction =
                        HomeFragmentDirections.actionHomeFragmentToSearchFragment(it)
                    findNavController().navigate(direction)
                }
            }
        )
    }

    override fun render(data: ViewData) {
        log.d("render")
        updateSearchSuggestion(data.searchSuggestion)
    }
}