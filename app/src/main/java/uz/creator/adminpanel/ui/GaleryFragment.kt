package uz.creator.adminpanel.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import uz.creator.adminpanel.R
import uz.creator.adminpanel.databinding.FragmentGaleryBinding

class GaleryFragment : Fragment() {

    lateinit var binding: FragmentGaleryBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentGaleryBinding.inflate(layoutInflater, container, false)
        return binding.root

        binding.rvGallery.layoutManager =
            LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.cardFilter.setOnClickListener {
            findNavController().navigate(R.id.filterFragment)
        }
    }
}