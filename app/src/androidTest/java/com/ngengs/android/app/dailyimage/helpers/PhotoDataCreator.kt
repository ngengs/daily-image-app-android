package com.ngengs.android.app.dailyimage.helpers

import com.ngengs.android.app.dailyimage.data.local.model.PhotosLocal
import com.ngengs.android.libs.test.utils.DataForger
import fr.xgouchet.elmyr.Forge

object PhotoDataCreator {
    fun create(forge: Forge, position: Int = 0): PhotosLocal {
        val photos = DataForger.forgeParcel<PhotosLocal>(forge) { stableId = true }
        val desc = if (forge.aBool()) forge.anAlphaNumericalString(size = position + 20) else null

        val imageUrl =
            "https://images.unsplash.com/photo-1616441064900-e0adeacda4f3?auto=format&fit=crop&q=10"
        return photos.copy(
            user = photos.user?.copy(
                username = "usn${position}_${forge.aNumericalString(size = position + 10)}",
                name = "nm${position}_${forge.anAlphabeticalString(size = position + 10)}",
            ),
            width = forge.anInt(min = 10, max = 400),
            height = forge.anInt(min = 10, max = 400),
            description = desc,
            image = imageUrl,
            blurHash = "LGC~F;xE^OxajFjZR*js~BaeELR+",
            color = "#8c7359"
        )
    }

    fun createList(forge: Forge, size: Int, startAt: Int = 0): List<PhotosLocal> =
        (startAt until size + startAt).map { create(forge, it) }
}