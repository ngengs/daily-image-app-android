package com.ngengs.android.app.dailyimage.presenter.fragment.latest

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
import com.google.android.material.snackbar.Snackbar
import com.ngengs.android.app.dailyimage.R
import com.ngengs.android.app.dailyimage.databinding.FragmentLatestImageBinding
import com.ngengs.android.app.dailyimage.domain.model.Results
import com.ngengs.android.app.dailyimage.domain.model.Results.Companion.anyData
import com.ngengs.android.app.dailyimage.presenter.fragment.BaseViewModelFragment
import com.ngengs.android.app.dailyimage.presenter.fragment.home.HomeFragmentDirections
import com.ngengs.android.app.dailyimage.presenter.fragment.latest.LatestImageViewModel.ViewData
import com.ngengs.android.app.dailyimage.presenter.shared.adapter.HeaderToolsAdapter
import com.ngengs.android.app.dailyimage.presenter.shared.adapter.LoadingItemAdapter
import com.ngengs.android.app.dailyimage.presenter.shared.adapter.PhotoListAdapter
import com.ngengs.android.app.dailyimage.presenter.shared.ui.ProvidableTopPaddingScreen
import com.ngengs.android.app.dailyimage.presenter.shared.ui.ScrollableTopScreen
import com.ngengs.android.app.dailyimage.presenter.shared.ui.delegation.ChangeableListViewTypeScreen
import com.ngengs.android.app.dailyimage.presenter.shared.ui.delegation.ErrorHandlerScreen
import com.ngengs.android.app.dailyimage.presenter.shared.ui.delegation.LoadingHandlerScreen
import com.ngengs.android.app.dailyimage.presenter.shared.ui.delegation.implementation.ChangeableListViewTypeScreenImpl
import com.ngengs.android.app.dailyimage.presenter.shared.ui.delegation.implementation.ErrorHandlerScreenImpl
import com.ngengs.android.app.dailyimage.presenter.shared.ui.delegation.implementation.LoadingHandlerScreenImpl
import com.ngengs.android.app.dailyimage.utils.common.constant.ViewConstant
import com.ngengs.android.app.dailyimage.utils.ui.ext.visible
import com.ngengs.android.app.dailyimage.utils.ui.rv.SimpleEndlessRecyclerScrollListener
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LatestImageFragment : LatestImageFragmentImpl()

open class LatestImageFragmentImpl :
    BaseViewModelFragment<FragmentLatestImageBinding, ViewData, LatestImageViewModel>(),
    ScrollableTopScreen,
    ChangeableListViewTypeScreen by ChangeableListViewTypeScreenImpl(),
    LoadingHandlerScreen by LoadingHandlerScreenImpl(),
    ErrorHandlerScreen by ErrorHandlerScreenImpl() {

    override val viewModel: LatestImageViewModel by viewModels()
    override val bindingInflater: (
        LayoutInflater,
        ViewGroup?,
        Boolean
    ) -> FragmentLatestImageBinding
        get() = FragmentLatestImageBinding::inflate

    private lateinit var concatAdapter: ConcatAdapter
    private lateinit var headerAdapter: HeaderToolsAdapter
    private lateinit var topLoadingAdapter: LoadingItemAdapter
    private lateinit var bottomLoadingAdapter: LoadingItemAdapter
    private lateinit var photoAdapter: PhotoListAdapter
    private lateinit var endlessScrollListener: SimpleEndlessRecyclerScrollListener

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
                scrollToTop()
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
        photoAdapter.stateRestorationPolicy = PREVENT_WHEN_EMPTY
        endlessScrollListener = SimpleEndlessRecyclerScrollListener(currentSpanSize()) {
            viewModel.fetchNextIfNeeded()
        }
        endlessScrollListener.isEnabled = false
        binding.rv.addOnScrollListener(endlessScrollListener)

        headerAdapter.updatingSpaceTopBasedOnView(binding.root) {
            (parentFragment as? ProvidableTopPaddingScreen)?.provideTopPadding()
        }
    }

    override fun render(data: ViewData) {
        log.d("render")
        binding.rv.visible()
        renderHeaderTools(data)
        onHandleLayoutType(data.viewType, binding.rv, concatAdapter) {
            photoAdapter.updateViewType(data.viewType)
        }

        renderLoading(data)
        renderSuccess(data)
        renderError(data)
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
            log.d("renderSuccess")
            endlessScrollListener.isEnabled = true
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
        log.d("renderHeaderTools")
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

    private fun createViewTypeIcon(viewType: Int) = if (viewType == ViewConstant.VIEW_TYPE_GRID) {
        R.drawable.ic_baseline_grid_view_24
    } else R.drawable.ic_baseline_view_list_24

    override fun scrollToTop() {
        binding.rv.scrollToPosition(0)
    }
}