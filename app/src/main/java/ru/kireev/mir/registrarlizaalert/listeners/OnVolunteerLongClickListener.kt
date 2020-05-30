package ru.kireev.mir.registrarlizaalert.listeners

import ru.kireev.mir.registrarlizaalert.data.Volunteer

interface OnVolunteerLongClickListener {
    fun onLongVolunteerClick(volunteer: Volunteer)
}