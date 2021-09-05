package uz.creator.adminpanel.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import uz.creator.adminpanel.R
import uz.creator.adminpanel.databinding.FragmentHomeItemBinding
import java.util.zip.Inflater

class HomeItemFragment : Fragment() {

    lateinit var binding: FragmentHomeItemBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeItemBinding.inflate(layoutInflater , container , false)
        return binding.root
    }

    companion object {

        @JvmStatic
        fun newInstance() = HomeItemFragment()
    }
}