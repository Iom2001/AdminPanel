package uz.creator.adminpanel.ui.auth

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import uz.creator.adminpanel.R
import uz.creator.adminpanel.databinding.FragmentLanguageBinding
import uz.creator.adminpanel.utils.Permanent

class LanguageFragment : Fragment() {

    private lateinit var binding: FragmentLanguageBinding
    private lateinit var sharedPreferencesLan: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (requireActivity().getSharedPreferences(
                Permanent.PREF_REGISTER_NAME,
                Context.MODE_PRIVATE
            ).getBoolean(Permanent.REGISTER_KEY, false)
        ) {
            findNavController().popBackStack()
            findNavController().navigate(R.id.passwordFragment)
        }
        sharedPreferencesLan =
            activity?.getSharedPreferences(Permanent.PREF_LAN_NAME, Context.MODE_PRIVATE)!!
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLanguageBinding.inflate(inflater, container, false)

        binding.apply {
            krillBtn.setOnClickListener {
                Permanent.isKiril = true
                saveLan()
                requireActivity().recreate()
                findNavController().popBackStack()
                findNavController().navigate(R.id.loginFragment)
            }
            latinBtn.setOnClickListener {
                Permanent.isKiril = false
                saveLan()
                requireActivity().recreate()
                findNavController().popBackStack()
                findNavController().navigate(R.id.loginFragment)
            }
        }

        return binding.root
    }

    private fun saveLan() {
//        save sharedpreference language
        var editor = sharedPreferencesLan.edit()
        editor.putBoolean(Permanent.LAN_KEY, Permanent.isKiril)
        editor.apply()
    }
}