package ru.kireev.mir.registrarlizaalert.ui.main

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import ru.kireev.mir.registrarlizaalert.R

class SectionsPagerAdapter(private val context: Context, fm: FragmentManager) : FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    companion object {
        private val TAB_TITLES = arrayOf(R.string.tab_text_1, R.string.tab_text_2, R.string.tab_text_3)
    }

    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> NotSentVolunteersFragment()
            1 -> SentVolunteersFragment()
            else -> AllVolunteersFragment()
        }
    }

    override fun getCount() = 3

    override fun getPageTitle(position: Int): CharSequence? {
        return context.resources.getString(TAB_TITLES[position])
    }
}