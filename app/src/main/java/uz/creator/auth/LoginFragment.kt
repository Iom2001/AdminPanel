package uz.creator.auth

import android.os.Bundle
import android.text.TextUtils
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import uz.creator.adminpanel.R
import uz.creator.adminpanel.databinding.FragmentLoginBinding
import uz.creator.utils.hideKeyboard
import uz.creator.utils.snackbar

class LoginFragment : Fragment(R.layout.fragment_login) {
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLoginBinding.inflate(layoutInflater)
        binding.btnConfirmation.setOnClickListener {
            requireView().hideKeyboard()
          //  CheckEmpty()
            findNavController().navigate(R.id.action_loginFragment_to_homeFragment)
        }
        return binding.root
    }
    private fun CheckEmpty() {
        binding.apply {
            if (TextUtils.isEmpty(etLogin.text?.trim().toString())) {
                etLogin.error
                requireView().snackbar("Login bo'sh")
                return@apply
            }
            if (TextUtils.isEmpty(etPassword.text?.trim().toString())) {
                etPassword.error
                requireView().snackbar("Parol bo'sh")
                return@apply
            }
        }
    }


}