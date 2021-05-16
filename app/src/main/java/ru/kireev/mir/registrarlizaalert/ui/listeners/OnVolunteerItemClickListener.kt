package ru.kireev.mir.registrarlizaalert.ui.listeners

import android.widget.AdapterView

interface OnVolunteerItemClickListener {
    fun onItemClick(parent: AdapterView<*>, position: Int, adapterPosition: Int)
}