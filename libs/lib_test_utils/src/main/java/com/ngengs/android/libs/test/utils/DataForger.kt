package com.ngengs.android.libs.test.utils

import android.os.Parcelable
import fr.xgouchet.elmyr.Case
import fr.xgouchet.elmyr.Forge
import java.io.Serializable

/**
 * Created by rizky.kharisma on 29/12/22.
 * @ngengs
 */
object DataForger {
    inline fun <reified T : Serializable> forgeSerializable(
        forger: Forge,
        option: ForgeOption.() -> Unit = {},
    ): T {
        val forgeOption = ForgeOption().apply(option)
        return forgeSerializable(forger, forgeOption)
    }

    inline fun <reified T : Serializable> forgeSerializableStableId(
        forger: Forge,
        option: ForgeOption.() -> Unit = {},
    ): T {
        val forgeOption = ForgeOption().apply(option).apply { stableId = true }
        return forgeSerializable(forger, forgeOption)
    }

    inline fun <reified T : Serializable> forgeSerializable(
        forger: Forge,
        size: Int,
        option: ForgeOption.() -> Unit = {},
    ): List<T> {
        if (size < 1) throw IllegalStateException()
        val forgeOption = ForgeOption().apply(option)
        return (1..size).map { forgeSerializable(forger, forgeOption) }
    }

    inline fun <reified T : Serializable> forgeSerializableStableId(
        forger: Forge,
        size: Int,
        option: ForgeOption.() -> Unit = {},
    ): List<T> {
        if (size < 1) throw IllegalStateException()
        val forgeOption = ForgeOption().apply(option).apply { stableId = true }
        return (1..size).map { forgeSerializable(forger, forgeOption) }
    }

    inline fun <reified T : Parcelable> forgeSerializable(
        forger: Forge,
        option: ForgeOption,
    ): T {
        val instance = T::class.java.getDeclaredConstructor().newInstance()
        return DataForgerCreator.internalForgeClass(
            instance,
            instance.javaClass.declaredFields,
            option,
            forger,
        )
    }

    inline fun <reified T : Parcelable> forgeParcel(
        forger: Forge,
        option: ForgeOption.() -> Unit = {},
    ): T {
        val forgeOption = ForgeOption().apply(option)
        return forgeParcel(forger, forgeOption)
    }

    inline fun <reified T : Parcelable> forgeParcelStableId(
        forger: Forge,
        option: ForgeOption.() -> Unit = {},
    ): T {
        val forgeOption = ForgeOption().apply(option).apply { stableId = true }
        return forgeParcel(forger, forgeOption)
    }

    inline fun <reified T : Parcelable> forgeParcel(
        forger: Forge,
        size: Int,
        option: ForgeOption.() -> Unit = {},
    ): List<T> {
        if (size < 1) throw IllegalStateException()
        val forgeOption = ForgeOption().apply(option)
        return (1..size).map { forgeParcel(forger, forgeOption) }
    }

    inline fun <reified T : Parcelable> forgeParcelStableId(
        forger: Forge,
        size: Int,
        option: ForgeOption.() -> Unit = {},
    ): List<T> {
        if (size < 1) throw IllegalStateException()
        val forgeOption = ForgeOption().apply(option).apply { stableId = true }
        return (1..size).map { forgeParcel(forger, forgeOption) }
    }

    inline fun <reified T : Parcelable> forgeParcel(
        forger: Forge,
        option: ForgeOption,
    ): T {
        val instance = T::class.java.getDeclaredConstructor().newInstance()
        return DataForgerCreator.internalForgeClass(
            instance,
            instance.javaClass.declaredFields,
            option,
            forger,
        )
    }

    data class ForgeOption(
        var textSize: Int = -1,
        var textCase: Case = Case.ANY,
        var intMinSize: Int = 0,
        var intMaxSize: Int = Int.MAX_VALUE,
        var longMinSize: Long = 0L,
        var longMaxSize: Long = Long.MAX_VALUE,
        var doubleMinSize: Double = 0.0,
        var doubleMaxSize: Double = Double.MAX_VALUE,
        var floatMinSize: Float = 0F,
        var floatMaxSize: Float = Float.MAX_VALUE,
        var listSize: Int = -1,
        var stableId: Boolean = false,
    )
}