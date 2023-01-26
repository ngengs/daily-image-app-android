package com.ngengs.android.app.dailyimage.data.model.ext

import com.ngengs.android.app.dailyimage.data.local.model.PhotosLocal

/**
 * Created by rizky.kharisma on 26/01/23.
 * @ngengs
 */

val PhotosLocal.imageSmall get() = "$image&w=400"
val PhotosLocal.imageLarge get() = "$image&w=1080"
val PhotosLocal.imageLoadingThumb get() = "$image&w=10"