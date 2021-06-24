package ru.kireev.mir.registrarlizaalert.ui.util

import android.view.View
import android.widget.EditText
import androidx.annotation.StringRes
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import ru.kireev.mir.registrarlizaalert.R

fun EditText.getTrimmedString() = this.text.toString().trim()

fun View.showShortSnack(message: String) {
    Snackbar.make(this, message, Snackbar.LENGTH_SHORT).show()
}

fun Fragment.setActionBarTitle(@StringRes titleId: Int) {
    (activity as AppCompatActivity).supportActionBar?.title = getString(titleId)
}

fun Fragment.showSimpleAlertDialog(message: String): AlertDialog =
    AlertDialog.Builder(requireContext())
        .setTitle(getString(R.string.app_name))
        .setCancelable(false)
        .setMessage(message)
        .setPositiveButton("OK") { _, _ -> }
        .show()
