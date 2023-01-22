package com.ngengs.android.app.dailyimage.presenter.fragment.favorite

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ConcatAdapter
import com.ngengs.android.app.dailyimage.R
import com.ngengs.android.app.dailyimage.databinding.FragmentFavoriteBinding
import com.ngengs.android.app.dailyimage.domain.model.Results
import com.ngengs.android.app.dailyimage.presenter.fragment.BaseViewModelFragment
import com.ngengs.android.app.dailyimage.presenter.fragment.favorite.FavoriteViewModel.ViewData
import com.ngengs.android.app.dailyimage.presenter.fragment.home.HomeFragmentDirections
import com.ngengs.android.app.dailyimage.presenter.shared.adapter.HeaderToolsAdapter
import com.ngengs.android.app.dailyimage.presenter.shared.adapter.PhotoListAdapter
import com.ngengs.android.app.dailyimage.presenter.shared.ui.delegation.ChangeableListViewTypeScreen
import com.ngengs.android.app.dailyimage.presenter.shared.ui.delegation.ErrorHandlerScreen
import com.ngengs.android.app.dailyimage.presenter.shared.ui.delegation.LoadingHandlerScreen
import com.ngengs.android.app.dailyimage.presenter.shared.ui.delegation.implementation.ChangeableListViewTypeScreenImpl
import com.ngengs.android.app.dailyimage.presenter.shared.ui.delegation.implementation.ErrorHandlerScreenImpl
import com.ngengs.android.app.dailyimage.presenter.shared.ui.delegation.implementation.LoadingHandlerScreenImpl
import com.ngengs.android.app.dailyimage.utils.common.constant.ViewConstant
import com.ngengs.android.app.dailyimage.utils.ui.ext.visible
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FavoriteFragment : FavoriteFragmentImpl()

open class FavoriteFragmentImpl :
    BaseViewModelFragment<FragmentFavoriteBinding, ViewData, FavoriteViewModel>(),
    ChangeableListViewTypeScreen by ChangeableListViewTypeScreenImpl(),
    LoadingHandlerScreen by LoadingHandlerScreenImpl(),
    ErrorHandlerScreen by ErrorHandlerScreenImpl() {

    override val viewModel: FavoriteViewModel by viewModels()
    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentFavoriteBinding
        get() = FragmentFavoriteBinding::inflate

    private lateinit var concatAdapter: ConcatAdapter
    private lateinit var headerAdapter: HeaderToolsAdapter
    private lateinit var photoAdapter: PhotoListAdapter

    override fun initializeView() {
        val viewType = viewModel.getViewType()
        photoAdapter = PhotoListAdapter(viewType) { data, view ->
            val extras = FragmentNavigatorExtras(view to view.transitionName)
            val navDirections = HomeFragmentDirections.actionHomeFragmentToDetailFragment(data)
            findNavController().navigate(navDirections, extras)
        }
        headerAdapter = HeaderToolsAdapter(
            headerTitle = getString(R.string.favorite),
            iconOrderBy = null,
            onClickViewType = {
                viewModel.changeViewType()
            }
        )
        val concatAdapterConfig = ConcatAdapter.Config.Builder()
            .setStableIdMode(ConcatAdapter.Config.StableIdMode.ISOLATED_STABLE_IDS)
            .build()
        concatAdapter = ConcatAdapter(
            concatAdapterConfig,
            headerAdapter,
            photoAdapter
        )
        initializeViewTypeScreen(
            context = requireContext(),
            recyclerView = binding.rv,
            adapter = concatAdapter,
            viewType = viewType,
            topFullSpanItemCount = { headerAdapter.itemCount },
            singleSpanItemCount = { photoAdapter.itemCount }
        )
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
            page = 1L,
            hasCache = false,
            onDisplayingCache = null,
            onLoadingNextPage = {},
            onNoLoading = {}
        )
    }

    private fun renderSuccess(data: ViewData) {
        val mainData = data.mainData
        if (mainData is Results.Success) {
            log.d("renderSuccess")
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
            page = 1L,
            onRetry = { },
            onErrorNextPage = {}
        )
    }

    private fun renderHeaderTools(data: ViewData) {
        val headerViewTypeIcon = createViewTypeIcon(data.viewType)
        headerAdapter.changeViewTypeIcon(headerViewTypeIcon)
    }

    private fun createViewTypeIcon(viewType: Int) = if (viewType == ViewConstant.VIEW_TYPE_GRID) {
        R.drawable.ic_baseline_grid_view_24
    } else R.drawable.ic_baseline_view_list_24
}