package ru.kireev.mir.registrarlizaalert.ui.util

import android.view.View
import android.widget.EditText
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar

fun EditText.getTrimmedString() = this.text.toString().trim()

fun Fragment.showShortSnack(view: View, message: String) {
    Snackbar.make(view, message, Snackbar.LENGTH_SHORT).show()
}