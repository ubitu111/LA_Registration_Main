package ru.kireev.mir.registrarlizaalert.ui

import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment

abstract class BaseFragment @JvmOverloads constructor(
    @LayoutRes layoutRes: Int = 0
) : Fragment(layoutRes) {

    open fun onBackPressed() = Unit
}