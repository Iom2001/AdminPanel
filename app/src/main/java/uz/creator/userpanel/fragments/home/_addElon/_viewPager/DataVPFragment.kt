package uz.creator.userpanel.fragments.home._addElon._viewPager

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import uz.creator.adminpanel.R
import uz.creator.adminpanel.databinding.FragmentDataVPBinding


class DataVPFragment : Fragment() {
    private var _binding: FragmentDataVPBinding? = null
    val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDataVPBinding.inflate(layoutInflater)

        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}