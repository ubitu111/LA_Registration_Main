package ru.kireev.mir.registrarlizaalert.ui.util

import android.os.Binder
import android.os.Bundle
import android.os.Parcelable
import androidx.core.app.BundleCompat
import androidx.fragment.app.Fragment
import java.io.Serializable
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

class ReadWriteBundleDelegate<R, T>(
    private val getValueBlock: (R, KProperty<*>) -> T,
    private val setValueBlock: (R, KProperty<*>, T) -> Unit
) : ReadWriteProperty<R, T> {

    override operator fun setValue(thisRef: R, property: KProperty<*>, value: T) {
        setValueBlock(thisRef, property, value)
    }

    override operator fun getValue(thisRef: R, property: KProperty<*>): T {
        return getValueBlock(thisRef, property)
    }
}

inline fun <reified T> args(): ReadWriteProperty<Fragment, T> = ReadWriteBundleDelegate(
    getValueBlock = { thisRef, property ->
        getFromBundle(thisRef.arguments, property.name)
    },
    setValueBlock = { thisRef, property, value ->
        if (thisRef.arguments == null) {
            thisRef.arguments = Bundle()
        }
        val bundle = thisRef.requireArguments()
        setToBundle(bundle, property.name, value)
    }
)

inline fun <reified T> getFromBundle(bundle: Bundle?, key: String, defaultValue: T? = null): T {
    val result = bundle?.get(key) ?: defaultValue

    if (result != null && result !is T) {
        throw ClassCastException("Property for $key has different class type")
    }

    return result as T
}

fun <T> setToBundle(bundle: Bundle, key: String, value: T): Unit = when (value) {
    is String? -> bundle.putString(key, value)
    is Int -> bundle.putInt(key, value)
    is Short -> bundle.putShort(key, value)
    is Long -> bundle.putLong(key, value)
    is Byte -> bundle.putByte(key, value)
    is ByteArray? -> bundle.putByteArray(key, value)
    is Char -> bundle.putChar(key, value)
    is CharArray? -> bundle.putCharArray(key, value)
    is CharSequence? -> bundle.putCharSequence(key, value)
    is Float -> bundle.putFloat(key, value)
    is Bundle? -> bundle.putBundle(key, value)
    is Binder? -> BundleCompat.putBinder(bundle, key, value)
    is Parcelable? -> bundle.putParcelable(key, value)
    is Serializable? -> bundle.putSerializable(key, value)
    is LongArray? -> bundle.putLongArray(key, value)
    else -> throw IllegalStateException("Type ${(value as? Any)?.javaClass?.canonicalName} of property $key is not supported")
}