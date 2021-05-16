package ru.kireev.mir.registrarlizaalert.ui.fragments.tabs.groups

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import ru.kireev.mir.registrarlizaalert.R
import ru.kireev.mir.registrarlizaalert.data.database.GroupCallsigns

class GroupSectionsPagerAdapter(
    private var groupCallsign: GroupCallsigns,
    private val context: Context, fm: FragmentManager
) : FragmentStatePagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    companion object {
        private val TAB_TITLES = arrayOf(R.string.group_tab_text_1, R.string.group_tab_text_2)
    }

    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> ActiveGroupFragment.newInstance(groupCallsign)
            else -> ArchiveGroupFragment.newInstance(groupCallsign)
        }
    }

    override fun getCount(): Int = 2

    override fun getPageTitle(position: Int): CharSequence {
        return context.getString(TAB_TITLES[position])
    }
}