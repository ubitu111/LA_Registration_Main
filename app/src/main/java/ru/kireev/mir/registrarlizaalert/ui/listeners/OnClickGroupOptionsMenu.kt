package ru.kireev.mir.registrarlizaalert.ui.listeners

import android.widget.TextView
import ru.kireev.mir.registrarlizaalert.data.database.entity.Group

interface OnClickGroupOptionsMenu {
    fun onGroupOptionsMenuClick(textView: TextView, group: Group)
}