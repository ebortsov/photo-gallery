package com.github.ebortsov.photogallery.utils

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import kotlin.reflect.KProperty

class FragmentBoundedDelegate<T>(val block: (Context) -> T) : DefaultLifecycleObserver {
    var element: T? = null
    operator fun getValue(owner: Any, property: KProperty<*>): T {
        return checkNotNull(element) { "LifecycleOwner is not active" }
    }
    override fun onCreate(owner: LifecycleOwner) {
        check(owner is Fragment)
        element = block(owner.requireContext())
    }

    override fun onDestroy(owner: LifecycleOwner) {
        super.onDestroy(owner)
        element = null
    }
}