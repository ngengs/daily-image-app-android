package com.ngengs.android.app.dailyimage.data.model.ext

import com.ngengs.android.app.dailyimage.data.model.UserSimple
import com.ngengs.android.app.dailyimage.data.remote.model.User

/**
 * Created by rizky.kharisma on 26/01/23.
 * @ngengs
 */

fun User.toUserSimple() = UserSimple(
    id = this.id,
    username = this.username,
    name = this.name,
    avatar = this.profileImage.medium,
)