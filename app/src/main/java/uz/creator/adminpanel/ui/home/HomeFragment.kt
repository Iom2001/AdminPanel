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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (Permanent.isAdmin) {
            binding.cardAddUser.visibility = View.VISIBLE
        }

        binding.cardAdd.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_newAddElonFragment)
        }

        binding.cardAddUser.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_addUserFragment)
        }

        binding.cardSettings.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_editPinFragment)
        }

        binding.cardEdt.setOnClickListener {
            findNavController().navigate(R.id.homeListFragment)
        }

        binding.cardStatistic.setOnClickListener {
            findNavController().navigate(R.id.statisticsFragment)
        }
    }


}