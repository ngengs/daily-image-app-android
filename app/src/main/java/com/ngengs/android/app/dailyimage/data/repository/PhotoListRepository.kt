package com.ngengs.android.app.dailyimage.data.repository

import com.ngengs.android.app.dailyimage.data.local.model.PhotosLocal
import com.ngengs.android.app.dailyimage.data.model.CompletableData
import com.ngengs.android.app.dailyimage.utils.common.constant.ApiConstant.OrderBy

/**
 * Created by rizky.kharisma on 11/01/23.
 * @ngengs
 */
interface PhotoListRepository {
    suspend fun get(page: Long, @OrderBy orderBy: String): CompletableData<PhotosLocal>
    suspend fun cache(@OrderBy orderBy: String): List<PhotosLocal>
}