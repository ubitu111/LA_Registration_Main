package ru.kireev.mir.registrarlizaalert.ui.activities

import com.github.terrakok.cicerone.androidx.FragmentScreen
import ru.kireev.mir.registrarlizaalert.ui.fragments.AddManuallyFragment
import ru.kireev.mir.registrarlizaalert.ui.fragments.tabs.volunteers.TabbedVolunteersFragment

object MainFlows {
    fun volunteersTabbedScreen() = FragmentScreen {
        TabbedVolunteersFragment.newInstance()
    }
    fun addManuallyScreen(index: Int, volunteerId: Int? = null) = FragmentScreen {
        AddManuallyFragment.newInstance(index, volunteerId)
    }
}