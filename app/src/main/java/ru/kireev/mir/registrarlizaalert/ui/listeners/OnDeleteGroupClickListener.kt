package ru.kireev.mir.registrarlizaalert.ui.listeners

import ru.kireev.mir.registrarlizaalert.data.database.entity.Group

interface OnDeleteGroupClickListener {
    fun onDeleteGroupClick(group: Group)
}