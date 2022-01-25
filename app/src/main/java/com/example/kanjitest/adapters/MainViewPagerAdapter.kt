package com.example.kanjitest.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.example.kanjitest.fragments.MainFragment
import com.example.kanjitest.fragments.MenuFragment
import com.example.kanjitest.fragments.SearchKanjiFragment

class MainViewPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {

    override fun getCount() = 3

    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> MainFragment()
            1 -> SearchKanjiFragment()
            else -> MenuFragment()
        }
    }
}