package uz.creator.adminpanel.ui

import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.synnapps.carouselview.ImageListener
import uz.creator.adminpanel.R
import uz.creator.adminpanel.adapters.CheckBoxAdapter
import uz.creator.adminpanel.adapters.HomeAdapter
import uz.creator.adminpanel.databinding.FragmentAddUserBinding
import uz.creator.adminpanel.databinding.FragmentGaleryBinding

class GaleryFragment : Fragment() {

    lateinit var binding: FragmentGaleryBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentGaleryBinding.inflate(layoutInflater, container, false)
        return binding.root

        binding.rvHomeItem.layoutManager = LinearLayoutManager(requireActivity() , LinearLayoutManager.VERTICAL , false)

        }

    companion object {

        @JvmStatic
        fun newInstance(param1: String, param2: String) = GaleryFragment()

    }
}