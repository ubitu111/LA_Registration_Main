package ru.kireev.mir.registrarlizaalert.listeners

import ru.kireev.mir.registrarlizaalert.data.Group

interface OnDeleteGroupClickListener {
    fun onDeleteGroupClick(group: Group)
}