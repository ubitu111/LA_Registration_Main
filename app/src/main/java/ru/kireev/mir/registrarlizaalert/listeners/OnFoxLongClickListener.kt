package ru.kireev.mir.registrarlizaalert.listeners

import ru.kireev.mir.registrarlizaalert.data.Fox

interface OnFoxLongClickListener {
    fun onLongFoxClick(fox: Fox)
}