package uz.creator.userpanel.fragments.home._addElon
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import uz.creator.adminpanel.R
import uz.creator.adminpanel.databinding.FragmentNewAddElonBinding
import uz.creator.userpanel.fragments.home._addElon._viewPager.DataVPFragment
import uz.creator.userpanel.fragments.home._addElon._viewPager.ElonAddVPFragment
import uz.creator.userpanel.fragments.home._addElon._viewPager.PictureAddVPFragment
import uz.creator.userpanel.fragments.home._addElon.adapter.TabPagerAdapter

class NewAddElonFragment : Fragment() {
    private var _binding:FragmentNewAddElonBinding?=null
    private val binding get() =_binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
       _binding = FragmentNewAddElonBinding.inflate(layoutInflater)
        PagerAdapter()
        binding?.tabDots?.setupWithViewPager(binding?.viewpager)

        return binding.root
    }
    fun PagerAdapter() {
        val adapter = TabPagerAdapter(childFragmentManager)
        adapter.addFragment(DataVPFragment(), "")
        adapter.addFragment(ElonAddVPFragment(), "")
        adapter.addFragment(PictureAddVPFragment(), "")
        binding?.viewpager?.adapter = adapter
    }

}