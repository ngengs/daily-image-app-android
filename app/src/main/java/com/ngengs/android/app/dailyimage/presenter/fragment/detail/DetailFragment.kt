package com.ngengs.android.app.dailyimage.presenter.fragment.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toDrawable
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.transition.TransitionInflater
import com.ngengs.android.app.dailyimage.R
import com.ngengs.android.app.dailyimage.data.local.model.PhotosLocal
import com.ngengs.android.app.dailyimage.data.local.model.PhotosLocal.Companion.imageLarge
import com.ngengs.android.app.dailyimage.data.local.model.PhotosLocal.Companion.imageLoadingThumb
import com.ngengs.android.app.dailyimage.databinding.FragmentDetailBinding
import com.ngengs.android.app.dailyimage.presenter.fragment.BaseViewModelFragment
import com.ngengs.android.app.dailyimage.presenter.fragment.detail.DetailViewModel.ViewData
import com.ngengs.android.app.dailyimage.utils.image.BlurHashDecoder
import com.ngengs.android.app.dailyimage.utils.image.GlideUtils
import com.ngengs.android.app.dailyimage.utils.ui.TransitionUtils
import com.ngengs.android.app.dailyimage.utils.ui.ext.gone
import com.ngengs.android.app.dailyimage.utils.ui.ext.isVisible
import com.ngengs.android.app.dailyimage.utils.ui.ext.load
import com.ngengs.android.app.dailyimage.utils.ui.ext.visible
import com.ngengs.android.app.dailyimage.utils.ui.ext.visibleIf
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class DetailFragment : BaseViewModelFragment<FragmentDetailBinding, ViewData, DetailViewModel>() {
    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentDetailBinding
        get() = FragmentDetailBinding::inflate
    override val viewModel: DetailViewModel by viewModels()
    private val args: DetailFragmentArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.set(args.photo)
        sharedElementEnterTransition =
            TransitionInflater.from(requireContext()).inflateTransition(android.R.transition.move)
    }

    override fun onDestroy() {
        log.d("onDestroy")
        BlurHashDecoder.clearCache()
        super.onDestroy()
    }

    override fun initializeView() {
        val photo = args.photo
        parentFragment?.postponeEnterTransition()
        binding.photo.transitionName = TransitionUtils.imageTransitionName(photo.id)
        binding.toolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
        binding.photo.setOnClickListener {
            toggleToolbarAndDetailContent()
        }
        binding.favoriteButton.setOnClickListener {
            viewModel.changeFavorite()
        }
        bindingData(photo)
        createBlurBackground(photo)
    }

    private fun bindingData(photo: PhotosLocal) {
        val thumbnailImage = GlideUtils.thumbnailBuilder(requireContext(), photo.imageLoadingThumb)
            .fitCenter()
        binding.photo.load(photo.imageLarge) {
            thumbnail = thumbnailImage
            onImageLoaded = { parentFragment?.startPostponedEnterTransition() }
            onLoadFailed = { parentFragment?.startPostponedEnterTransition() }
        }
        binding.fullName.text = photo.user?.name
        val username = photo.user?.username.orEmpty()
        binding.username.text = getString(R.string.username_format, username)
        binding.description.visibleIf(photo.description != null)
        binding.description.text = photo.description
    }

    private fun createBlurBackground(photo: PhotosLocal) {
        viewLifecycleOwner.lifecycleScope.launch {
            val width = photo.width
            val height = photo.height
            val divider = if (width > height) height else width
            val normalWidth = ((width / divider.toDouble()) * 100).toInt()
            val normalHeight = ((height / divider.toDouble()) * 100).toInt()
            val bitmap = BlurHashDecoder.decode(photo.blurHash, normalWidth, normalHeight)
            binding.root.background = bitmap?.toDrawable(resources)
        }
    }

    override fun render(data: ViewData) {
        val isFavorite = data.isFavorite
        if (isFavorite == null) {
            binding.favoriteButton.tag = null
            binding.favoriteButton.hide()
        } else {
            val buttonImageRes = if (isFavorite) {
                R.drawable.ic_baseline_favorite_24
            } else {
                R.drawable.ic_baseline_favorite_border_24
            }
            binding.favoriteButton.setImageDrawable(
                ContextCompat.getDrawable(requireContext(), buttonImageRes)
            )
            binding.favoriteButton.tag = FAB_FAVORITE_TAG
            binding.favoriteButton.show()
        }
    }

    private fun toggleToolbarAndDetailContent() {
        val isShowing = binding.detailContent.isVisible()
        val isFavoriteButtonIncluded = binding.favoriteButton.tag != null
        if (isShowing) {
            binding.detailContent.gone()
            binding.appBar.gone()
            if (isFavoriteButtonIncluded) binding.favoriteButton.gone()
        } else {
            binding.detailContent.visible()
            binding.appBar.visible()
            if (isFavoriteButtonIncluded) binding.favoriteButton.visible()
        }
    }

    companion object {
        private const val FAB_FAVORITE_TAG = "favorite_button_enabled"
    }
}