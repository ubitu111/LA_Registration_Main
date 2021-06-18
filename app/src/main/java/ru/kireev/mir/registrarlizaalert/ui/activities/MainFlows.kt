package ru.kireev.mir.registrarlizaalert.ui.activities

import com.github.terrakok.cicerone.androidx.FragmentScreen
import ru.kireev.mir.registrarlizaalert.ui.fragments.tabs.volunteers.TabbedVolunteersFragment

object MainFlows {
    fun volunteersTabbedScreen() = FragmentScreen {
        TabbedVolunteersFragment.newInstance()
    }
}