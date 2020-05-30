package ru.kireev.mir.registrarlizaalert.listeners

import android.widget.AdapterView

interface OnVolunteerItemClickListener {
    fun onItemClick(parent: AdapterView<*>, position: Int, adapterPosition: Int)
}