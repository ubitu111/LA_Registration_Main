package ru.kireev.mir.registrarlizaalert.listeners

import android.widget.TextView
import ru.kireev.mir.registrarlizaalert.data.Group

interface OnClickGroupOptionsMenu {
    fun onGroupOptionsMenuClick(textView: TextView, group: Group)
}