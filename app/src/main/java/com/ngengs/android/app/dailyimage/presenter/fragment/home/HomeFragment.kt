package com.ngengs.android.app.dailyimage.presenter.fragment.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.ngengs.android.app.dailyimage.R
import com.ngengs.android.app.dailyimage.databinding.FragmentHomeBinding
import com.ngengs.android.app.dailyimage.domain.model.Results
import com.ngengs.android.app.dailyimage.domain.model.Results.Companion.anyData
import com.ngengs.android.app.dailyimage.domain.model.Results.FailureType.EMPTY
import com.ngengs.android.app.dailyimage.domain.model.Results.FailureType.NETWORK
import com.ngengs.android.app.dailyimage.domain.model.Results.FailureType.SERVER
import com.ngengs.android.app.dailyimage.presenter.common.HeaderToolsAdapter
import com.ngengs.android.app.dailyimage.presenter.common.LoadingItemAdapter
import com.ngengs.android.app.dailyimage.presenter.common.PhotoListAdapter
import com.ngengs.android.app.dailyimage.presenter.fragment.BaseViewModelFragment
import com.ngengs.android.app.dailyimage.presenter.fragment.home.HomeViewModel.ViewData
import com.ngengs.android.app.dailyimage.utils.common.constant.ViewConstant.PhotoListViewType
import com.ngengs.android.app.dailyimage.utils.common.constant.ViewConstant.VIEW_TYPE_GRID
import com.ngengs.android.app.dailyimage.utils.image.BlurHashDecoder
import com.ngengs.android.app.dailyimage.utils.ui.ext.gone
import com.ngengs.android.app.dailyimage.utils.ui.ext.visible
import com.ngengs.android.app.dailyimage.utils.ui.ext.visibleIf
import com.ngengs.android.app.dailyimage.utils.ui.rv.SimpleEndlessRecyclerScrollListener
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class HomeFragment : BaseViewModelFragment<FragmentHomeBinding, ViewData, HomeViewModel>() {
    override val viewModel: HomeViewModel by viewModels()
    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentHomeBinding
        get() = FragmentHomeBinding::inflate

    private lateinit var concatAdapter: ConcatAdapter
    private lateinit var headerAdapter: HeaderToolsAdapter
    private lateinit var topLoadingAdapter: LoadingItemAdapter
    private lateinit var bottomLoadingAdapter: LoadingItemAdapter
    private lateinit var photoAdapter: PhotoListAdapter
    private lateinit var gridLayoutManager: GridLayoutManager
    private var spanCount: Int = 2

    override fun initializeView() {
        binding.toolbar.setupWithNavController(findNavController())
        spanCount = resources.getInteger(R.integer.grid_span)
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
        gridLayoutManager = GridLayoutManager(requireContext(), spanCount).apply {
            spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                override fun getSpanSize(position: Int): Int {
                    val topItemCount =
                        headerAdapter.itemCount + topLoadingAdapter.itemCount
                    val itemCountWithoutLoadingBottom = topItemCount + photoAdapter.itemCount
                    val canTakeAllSpan =
                        position < topItemCount || position >= itemCountWithoutLoadingBottom
                    return if (canTakeAllSpan) {
                        spanCount
                    } else {
                        1
                    }
                }
            }
        }
        updateLayoutManager(viewType)
        binding.rv.addOnScrollListener(
            SimpleEndlessRecyclerScrollListener(spanCount) {
                viewModel.fetchNextIfNeeded()
            }
        )
    }

    override fun render(data: ViewData) {
        Timber.d("render")
        renderHeader(data)
        renderLayoutType(data)
        renderLoading(data)
        renderSuccess(data)
        renderError(data)
    }

    private fun renderLayoutType(data: ViewData) {
        val isViewTypeChanged = data.viewType != photoAdapter.getViewType()
        photoAdapter.updateViewType(data.viewType)
        if (isViewTypeChanged) updateLayoutManager(data.viewType)
    }

    private fun renderLoading(data: ViewData) {
        val mainData = data.mainData
        binding.rv.visible()
        if (mainData is Results.Loading) {
            val cache = data.cache
            if (data.page == 1L && cache.isEmpty()) {
                binding.rv.gone()
                binding.layoutLoading.root.visible()
            } else if (data.page == 1L) {
                photoAdapter.submitList(cache)
                topLoadingAdapter.startLoading()
                binding.layoutLoading.root.gone()
            } else {
                photoAdapter.submitList(mainData.anyData()?.data.orEmpty())
                bottomLoadingAdapter.startLoading()
                binding.layoutLoading.root.gone()
            }
        } else {
            topLoadingAdapter.stopLoading()
            bottomLoadingAdapter.stopLoading()
            binding.layoutLoading.root.gone()
        }
    }

    private fun renderSuccess(data: ViewData) {
        val mainData = data.mainData
        binding.rv.visible()
        if (mainData is Results.Success) {
            Timber.d("renderSuccess")
            binding.rv.visible()
            val photos = mainData.data
            photoAdapter.submitList(photos.data)
        }
    }

    private fun renderError(data: ViewData) {
        val mainData = data.mainData
        binding.rv.visible()
        if (mainData is Results.Failure) {
            if (data.page == 1L) {
                val errorType = mainData.type
                binding.rv.gone()
                binding.layoutError.root.visible()
                val errorMessage = when (errorType) {
                    EMPTY -> getString(R.string.error_message_empty)
                    SERVER -> getString(R.string.error_message_server)
                    NETWORK -> getString(R.string.error_message_network)
                    else -> getString(R.string.error_message_other)
                }
                binding.layoutError.errorMessage.text = errorMessage
                binding.layoutError.retryButton.visibleIf(errorType != EMPTY)
                binding.layoutError.retryButton.setOnClickListener { viewModel.reload() }
            } else {
                photoAdapter.submitList(mainData.anyData()?.data.orEmpty())
                val errorMessage = mainData.throwable.message ?: "Something went wrong"
                Snackbar.make(binding.root, errorMessage, Snackbar.LENGTH_SHORT).show()
            }
        } else {
            binding.layoutError.root.gone()
        }
    }

    private fun updateLayoutManager(@PhotoListViewType viewType: Int) {
        binding.rv.layoutManager = gridLayoutManager
        if (viewType == VIEW_TYPE_GRID) {
            gridLayoutManager.spanCount = spanCount
            binding.rv.adapter = concatAdapter
        } else {
            gridLayoutManager.spanCount = 1
            binding.rv.adapter = concatAdapter
        }
    }

    private fun renderHeader(data: ViewData) {
        val (headerTitle, headerOrderIcon) = createHeaderTitleAndIcon()
        val headerViewTypeIcon = createViewTypeIcon(data.viewType)
        headerAdapter.changeTitle(headerTitle)
        headerAdapter.changeOrderIcon(headerOrderIcon)
        headerAdapter.changeViewTypeIcon(headerViewTypeIcon)
    }

    private fun createHeaderTitleAndIcon() = if (viewModel.isOrderByLatest()) {
        getString(R.string.latest_images) to R.drawable.ic_baseline_sort_calendar_desc_24
    } else {
        getString(R.string.popular_images) to R.drawable.ic_baseline_trending_up_24
    }

    private fun createViewTypeIcon(viewType: Int) = if (viewType == VIEW_TYPE_GRID) {
        R.drawable.ic_baseline_grid_view_24
    } else R.drawable.ic_baseline_view_list_24

    override fun onDestroy() {
        BlurHashDecoder.clearCache()
        super.onDestroy()
    }
}