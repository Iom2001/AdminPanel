package uz.creator.adminpanel.ui.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import uz.creator.adminpanel.R
import uz.creator.adminpanel.databinding.FragmentHomeBinding
import uz.creator.adminpanel.utils.Permanent

class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(layoutInflater)
        binding.cardadd.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_newAddElonFragment)
        }

        binding.toolbar.cardAddUser.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_addUserFragment)
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (Permanent.isAdmin) {
            binding.toolbar.cardAddUser.visibility = View.VISIBLE
        }
    }


}