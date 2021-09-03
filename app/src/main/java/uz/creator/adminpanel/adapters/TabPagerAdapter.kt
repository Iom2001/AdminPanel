package uz.creator.adminpanel.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

class TabPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {
    var fraglist = ArrayList<Fragment>()
    var titlelist = ArrayList<String>()

    override fun getItem(position: Int): Fragment {
        return fraglist[position]
    }
    override fun getCount(): Int {
        return titlelist.size
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return titlelist[position]
    }

    fun addFragment(fragment: Fragment, title: String) {
        fraglist.add(fragment)
          titlelist.add(title)
    }
}