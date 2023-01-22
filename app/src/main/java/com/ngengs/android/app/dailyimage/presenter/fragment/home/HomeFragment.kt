package com.ngengs.android.app.dailyimage.presenter.fragment.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.activity.addCallback
import androidx.core.view.postDelayed
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ConcatAdapter
import com.google.android.material.snackbar.Snackbar
import com.ngengs.android.app.dailyimage.R
import com.ngengs.android.app.dailyimage.databinding.FragmentHomeBinding
import com.ngengs.android.app.dailyimage.domain.model.Results
import com.ngengs.android.app.dailyimage.domain.model.Results.Companion.anyData
import com.ngengs.android.app.dailyimage.presenter.fragment.BaseViewModelFragment
import com.ngengs.android.app.dailyimage.presenter.fragment.home.HomeViewModel.ViewData
import com.ngengs.android.app.dailyimage.presenter.shared.adapter.HeaderToolsAdapter
import com.ngengs.android.app.dailyimage.presenter.shared.adapter.LoadingItemAdapter
import com.ngengs.android.app.dailyimage.presenter.shared.adapter.PhotoListAdapter
import com.ngengs.android.app.dailyimage.presenter.shared.ui.delegation.ChangeableListViewTypeScreen
import com.ngengs.android.app.dailyimage.presenter.shared.ui.delegation.ErrorHandlerScreen
import com.ngengs.android.app.dailyimage.presenter.shared.ui.delegation.LoadingHandlerScreen
import com.ngengs.android.app.dailyimage.presenter.shared.ui.delegation.SearchableScreen
import com.ngengs.android.app.dailyimage.presenter.shared.ui.delegation.implementation.ChangeableListViewTypeScreenImpl
import com.ngengs.android.app.dailyimage.presenter.shared.ui.delegation.implementation.ErrorHandlerScreenImpl
import com.ngengs.android.app.dailyimage.presenter.shared.ui.delegation.implementation.LoadingHandlerScreenImpl
import com.ngengs.android.app.dailyimage.presenter.shared.ui.delegation.implementation.SearchableScreenImpl
import com.ngengs.android.app.dailyimage.utils.ui.ext.visible
import com.ngengs.android.app.dailyimage.utils.ui.rv.SimpleEndlessRecyclerScrollListener
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class HomeFragment : HomeFragmentImpl()

open class HomeFragmentImpl :
    BaseViewModelFragment<FragmentHomeBinding, ViewData, HomeViewModel>(),
    SearchableScreen by SearchableScreenImpl(),
    ChangeableListViewTypeScreen by ChangeableListViewTypeScreenImpl(),
    LoadingHandlerScreen by LoadingHandlerScreenImpl(),
    ErrorHandlerScreen by ErrorHandlerScreenImpl() {

    override val viewModel: HomeViewModel by viewModels()
    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentHomeBinding
        get() = FragmentHomeBinding::inflate

    private lateinit var concatAdapter: ConcatAdapter
    private lateinit var headerAdapter: HeaderToolsAdapter
    private lateinit var topLoadingAdapter: LoadingItemAdapter
    private lateinit var bottomLoadingAdapter: LoadingItemAdapter
    private lateinit var photoAdapter: PhotoListAdapter
    private lateinit var onBackPressed: OnBackPressedCallback

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        onBackPressed = requireActivity().onBackPressedDispatcher.addCallback(this, false) {
            if (binding.searchView.isShown) binding.searchView.hide()
        }
    }

    override fun initializeView() {
        val viewType = viewModel.getViewType()
        photoAdapter = PhotoListAdapter(viewType) { data, view ->
            val extras = FragmentNavigatorExtras(view to view.transitionName)
            val navDirections = HomeFragmentDirections.actionHomeFragmentToDetailFragment(data)
            findNavController().navigate(navDirections, extras)
        }
        val (headerTitle, _) = createHeaderTitleAndIcon()
        headerAdapter = HeaderToolsAdapter(
            headerTitle = headerTitle,
            onClickOrderBy = {
                viewModel.changeOrderBy()
            },
            onClickViewType = {
                viewModel.changeViewType()
            }
        )
        topLoadingAdapter = LoadingItemAdapter(getString(R.string.loading_refresh_data))
        bottomLoadingAdapter = LoadingItemAdapter(getString(R.string.loading_more_data))
        val concatAdapterConfig = ConcatAdapter.Config.Builder()
            .setStableIdMode(ConcatAdapter.Config.StableIdMode.ISOLATED_STABLE_IDS)
            .build()
        concatAdapter = ConcatAdapter(
            concatAdapterConfig,
            headerAdapter,
            topLoadingAdapter,
            photoAdapter,
            bottomLoadingAdapter
        )
        initializeViewTypeScreen(
            context = requireContext(),
            recyclerView = binding.rv,
            adapter = concatAdapter,
            viewType = viewType,
            topFullSpanItemCount = { headerAdapter.itemCount + topLoadingAdapter.itemCount },
            singleSpanItemCount = { photoAdapter.itemCount }
        )
        binding.rv.addOnScrollListener(
            SimpleEndlessRecyclerScrollListener(currentSpanSize()) {
                viewModel.fetchNextIfNeeded()
            }
        )

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
                binding.searchView.postDelayed(300L) {
                    val direction =
                        HomeFragmentDirections.actionHomeFragmentToSearchFragment(it)
                    findNavController().navigate(direction)
                }
            }
        )
    }

    override fun render(data: ViewData) {
        Timber.d("render")
        binding.rv.visible()
        renderHeaderTools(data)
        onHandleLayoutType(data.viewType, binding.rv, concatAdapter) {
            photoAdapter.updateViewType(data.viewType)
        }

        renderLoading(data)
        renderSuccess(data)
        renderError(data)

        updateSearchSuggestion(data.searchSuggestion)
    }

    private fun renderLoading(data: ViewData) {
        onHandleLoading(
            mainViewContent = binding.rv,
            layoutLoading = binding.layoutLoading,
            data = data.mainData,
            page = data.page,
            hasCache = data.cache.isNotEmpty(),
            onDisplayingCache = {
                photoAdapter.submitList(data.cache)
                topLoadingAdapter.startLoading()
            },
            onLoadingNextPage = {
                photoAdapter.submitList(data.mainData.anyData()?.data.orEmpty())
                bottomLoadingAdapter.startLoading()
            },
            onNoLoading = {
                topLoadingAdapter.stopLoading()
                bottomLoadingAdapter.stopLoading()
            }
        )
    }

    private fun renderSuccess(data: ViewData) {
        val mainData = data.mainData
        if (mainData is Results.Success) {
            Timber.d("renderSuccess")
            binding.rv.visible()
            val photos = mainData.data
            photoAdapter.submitList(photos.data)
        }
    }

    private fun renderError(data: ViewData) {
        onHandleError(
            context = requireContext(),
            mainViewContent = binding.rv,
            layoutError = binding.layoutError,
            data = data.mainData,
            page = data.page,
            onRetry = { viewModel.reload() },
            onErrorNextPage = {
                val errorData = data.mainData as Results.Failure
                photoAdapter.submitList(errorData.anyData()?.data.orEmpty())
                val errorMessage = errorData.throwable.message ?: "Something went wrong"
                Snackbar.make(binding.root, errorMessage, Snackbar.LENGTH_SHORT).show()
            }
        )
    }

    private fun renderHeaderTools(data: ViewData) {
        val (headerTitle, headerOrderIcon) = createHeaderTitleAndIcon()
        headerAdapter.changeTitle(headerTitle)
        headerAdapter.changeOrderIcon(headerOrderIcon)
        headerAdapter.changeViewTypeIcon(viewTypeIcon(data.viewType))
    }

    private fun createHeaderTitleAndIcon() = if (viewModel.isOrderByLatest()) {
        getString(R.string.latest_images) to R.drawable.ic_baseline_sort_calendar_desc_24
    } else {
        getString(R.string.popular_images) to R.drawable.ic_baseline_trending_up_24
    }
}