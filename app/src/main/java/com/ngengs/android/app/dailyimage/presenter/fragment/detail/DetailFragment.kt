package com.ngengs.android.app.dailyimage.presenter.fragment.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.graphics.drawable.toDrawable
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.transition.TransitionInflater
import com.ngengs.android.app.dailyimage.R
import com.ngengs.android.app.dailyimage.data.local.model.PhotosLocal
import com.ngengs.android.app.dailyimage.data.local.model.PhotosLocal.Companion.imageLarge
import com.ngengs.android.app.dailyimage.data.local.model.PhotosLocal.Companion.imageLoadingThumb
import com.ngengs.android.app.dailyimage.databinding.FragmentDetailBinding
import com.ngengs.android.app.dailyimage.presenter.fragment.BaseFragment
import com.ngengs.android.app.dailyimage.utils.image.BlurHashDecoder
import com.ngengs.android.app.dailyimage.utils.image.GlideUtils
import com.ngengs.android.app.dailyimage.utils.ui.TransitionUtils
import com.ngengs.android.app.dailyimage.utils.ui.ext.gone
import com.ngengs.android.app.dailyimage.utils.ui.ext.isVisible
import com.ngengs.android.app.dailyimage.utils.ui.ext.load
import com.ngengs.android.app.dailyimage.utils.ui.ext.visible
import com.ngengs.android.app.dailyimage.utils.ui.ext.visibleIf
import kotlinx.coroutines.launch

class DetailFragment : BaseFragment<FragmentDetailBinding>() {
    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentDetailBinding
        get() = FragmentDetailBinding::inflate

    private val args: DetailFragmentArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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

    private fun toggleToolbarAndDetailContent() {
        val isShowing = binding.detailContent.isVisible()
        if (isShowing) {
            binding.detailContent.gone()
            binding.appBar.gone()
        } else {
            binding.detailContent.visible()
            binding.appBar.visible()
        }
    }
}