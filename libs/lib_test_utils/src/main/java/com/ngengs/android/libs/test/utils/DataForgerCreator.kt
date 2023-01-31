package com.ngengs.android.libs.test.utils

import android.os.Parcelable
import com.ngengs.android.libs.test.utils.DataForger.ForgeOption
import com.ngengs.android.libs.test.utils.ext.aDate
import fr.xgouchet.elmyr.Forge
import java.io.Serializable
import java.lang.reflect.Field
import java.lang.reflect.Modifier
import java.lang.reflect.ParameterizedType
import java.util.Date

/**
 * Created by rizky.kharisma on 31/01/23.
 * @ngengs
 */
object DataForgerCreator {
    private const val WEIRD_CHAR = "["

    fun <T> internalForgeClass(
        data: T,
        fields: Array<Field>,
        option: ForgeOption,
        forger: Forge
    ): T {
        fields.forEach {

            // Skip static field
            if (Modifier.isStatic(it.modifiers)) return@forEach

            // If protected then make it accessible
            if (it.isAccessible.not()) {
                it.isAccessible = true
            }

            val needStableId = option.stableId && it.name == "id"
            when (val type = it.type) {
                String::class.java -> it.set(data, createStringData(forger, option, needStableId))
                Integer::class.javaObjectType, Integer::class.javaPrimitiveType ->
                    it.set(data, createIntData(forger, option, needStableId))
                Long::class.javaObjectType, Long::class.javaPrimitiveType ->
                    it.set(data, createLongData(forger, option, needStableId))
                Double::class.javaObjectType, Double::class.javaPrimitiveType ->
                    it.set(data, createDoubleData(forger, option, needStableId))
                Float::class.javaPrimitiveType, Float::class.javaObjectType ->
                    it.set(data, createFloatData(forger, option))
                Boolean::class.javaObjectType, Boolean::class.javaPrimitiveType ->
                    it.set(data, forger.aBool())
                Date::class.java -> it.set(data, forger.aDate())
                List::class.java -> {
                    val lTypeArguments = (it.genericType as ParameterizedType).actualTypeArguments
                    val listClz = lTypeArguments.first() as Class<*>
                    val listSize = if (option.listSize <= 0) {
                        forger.anInt(min = 1, max = 5)
                    } else option.listSize
                    val listData = try {
                        when (listClz) {
                            String::class.java ->
                                (1..listSize).map { createStringData(forger, option) }
                            Integer::class.javaObjectType, Integer::class.javaPrimitiveType ->
                                (1..listSize).map { createIntData(forger, option) }
                            Long::class.javaObjectType, Long::class.javaPrimitiveType ->
                                (1..listSize).map { createLongData(forger, option) }
                            Double::class.javaObjectType, Double::class.javaPrimitiveType ->
                                (1..listSize).map { createDoubleData(forger, option) }
                            Float::class.javaPrimitiveType, Float::class.javaObjectType ->
                                (1..listSize).map { createFloatData(forger, option) }
                            Boolean::class.javaObjectType, Boolean::class.javaPrimitiveType ->
                                (1..listSize).map { forger.aBool() }
                            Date::class.java -> (1..listSize).map { forger.aDate() }
                            else -> {
                                if (Serializable::class.java.isAssignableFrom(listClz)) {
                                    (1..listSize).map {
                                        createSerializableData(listClz, option, forger)
                                    }
                                } else if (Parcelable::class.java.isAssignableFrom(listClz)) {
                                    (1..listSize).map {
                                        createParcelData(listClz, option, forger)
                                    }
                                } else emptyList()
                            }
                        }
                    } catch (e: Exception) {
                        emptyList()
                    }
                    it.set(data, listData)
                }
                else -> {
                    // Strange case when using gradle
                    if (type.toString().contains(WEIRD_CHAR)) return@forEach

                    if (type.isEnum) {
                        it.set(data, getEnumValue(type))
                    } else if (Serializable::class.java.isAssignableFrom(type)) {
                        it.set(data, createSerializableData(it.type, option, forger))
                    } else if (Parcelable::class.java.isAssignableFrom(type)) {
                        it.set(data, createParcelData(it.type, option, forger))
                    }
                }
            }
        }
        return data
    }

    private fun createStringData(
        forger: Forge,
        option: ForgeOption,
        needStableId: Boolean = false,
    ): String =
        if (needStableId) System.nanoTime().toString()
        else forger.anAlphabeticalString(option.textCase, option.textSize)

    private fun createIntData(
        forger: Forge,
        option: ForgeOption,
        needStableId: Boolean = false,
    ): Int {
        val intMin = option.intMinSize.takeIf { n -> n >= 0 } ?: 0
        return if (needStableId) {
            System.nanoTime().toInt()
        } else forger.anInt(intMin, option.intMaxSize)
    }

    private fun createLongData(
        forger: Forge,
        option: ForgeOption,
        needStableId: Boolean = false,
    ): Long {
        val longMin = option.longMinSize.takeIf { n -> n >= 0L } ?: 0L
        return if (needStableId) System.nanoTime() else forger.aLong(longMin, option.longMaxSize)
    }

    private fun createDoubleData(
        forger: Forge,
        option: ForgeOption,
        needStableId: Boolean = false,
    ): Double {
        val doubleMin = option.doubleMinSize.takeIf { n -> n >= 0.0 } ?: 0.0
        return if (needStableId) {
            System.nanoTime().toDouble()
        } else forger.aDouble(doubleMin, option.doubleMaxSize)
    }

    private fun createFloatData(
        forger: Forge,
        option: ForgeOption,
    ): Float {
        val floatMin = option.floatMinSize.takeIf { n -> n >= 0F } ?: 0F
        return forger.aFloat(floatMin, option.floatMaxSize)
    }

    private fun createParcelData(clz: Class<*>, option: ForgeOption, forger: Forge): Parcelable {
        val newInstance = clz.getDeclaredConstructor().newInstance() as Parcelable
        val dataFields = newInstance.javaClass.declaredFields
        return internalForgeClass(newInstance, dataFields, option, forger)
    }

    private fun createSerializableData(
        clz: Class<*>,
        option: ForgeOption,
        forger: Forge
    ): Serializable {
        val newInstance = clz.getDeclaredConstructor().newInstance() as Serializable
        val dataFields = newInstance.javaClass.declaredFields
        return internalForgeClass(newInstance, dataFields, option, forger)
    }

    @Suppress("UNCHECKED_CAST")
    private fun getEnumValue(enumClass: Class<*>): Any {
        val enumClz = enumClass.enumConstants as Array<Enum<*>>
        return enumClz.random()
    }
}