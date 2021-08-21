package uz.creator.userpanel.fragments.home
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import uz.creator.adminpanel.R
import uz.creator.adminpanel.databinding.FragmentHomeBinding
class HomeFragment : Fragment() {
   private  var _binding:FragmentHomeBinding?=null
    val binding get() =_binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(layoutInflater)
       binding.cardadd.setOnClickListener{
           findNavController().navigate(R.id.action_homeFragment_to_newAddElonFragment)
       }
        return binding.root
    }


}