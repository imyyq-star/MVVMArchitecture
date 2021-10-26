package com.imyyq.mvvm.binding.viewadapter.viewpager

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.imyyq.mvvm.base.fragment.ViewBindingBaseFragment

class VpFragmentPagerAdapter internal constructor(
    fm: FragmentManager,
    private val mPageList: List<ViewBindingBaseFragment<*, *>>
) : FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
    override fun getItem(i: Int): Fragment {
        return mPageList[i]
    }

    override fun getCount(): Int {
        return mPageList.size
    }
}