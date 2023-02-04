package com.ngengs.android.app.dailyimage.data.model.ext

import com.ngengs.android.app.dailyimage.data.local.model.PhotosLocal
import com.ngengs.android.app.dailyimage.data.remote.model.Photos

/**
 * Created by rizky.kharisma on 26/01/23.
 * @ngengs
 */

fun Photos.toPhotosLocal() = PhotosLocal(
    id = this.id,
    width = this.width,
    height = this.height,
    blurHash = this.blurHash,
    color = this.color,
    description = this.description,
    image = this.urls.raw,
    user = this.user.toUserSimple(),
)