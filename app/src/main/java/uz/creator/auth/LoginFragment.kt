package uz.creator.auth

import android.os.Bundle
import android.text.TextUtils
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import io.grpc.internal.SharedResourceHolder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import uz.creator.adminpanel.MainActivity
import uz.creator.adminpanel.R
import uz.creator.adminpanel.databinding.FragmentLoginBinding
import uz.creator.adminpanel.utils.Permanent
import uz.creator.adminpanel.utils.Resource
import uz.creator.adminpanel.utils.showToast
import uz.creator.adminpanel.viewmodel.AdminViewModel
import uz.creator.utils.hideKeyboard
import uz.creator.utils.snackbar

class LoginFragment : Fragment(R.layout.fragment_login) {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: AdminViewModel
    // Coroutine Scope
    private val uiScope = CoroutineScope(Dispatchers.Main)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLoginBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = (activity as MainActivity).viewModel
        binding.registrationBtn.setOnClickListener {
            requireView().hideKeyboard()
            uiScope.launch {
                if (checkEmpty()) {
                    if (checkAdmin()) {
                        findNavController().navigate(R.id.action_loginFragment_to_homeFragment)
                    } else {
                        if (checkUser()) {

                        }
                    }
                }
            }
//            var username = binding.nameEdit.text.toString()
//            var phoneNumber = binding.numberEdit.text.toString()
//            var pinNumber = binding.passwordEdit.text.toString()
//
//            if (username.isNotBlank() && phoneNumber.isNotBlank() && pinNumber.isNotBlank()) {
//            }
        }
    }

    private fun checkUser(): Boolean {
        return false
    }

    private fun checkEmpty(): Boolean {
        binding.apply {
            if (TextUtils.isEmpty(nameEdit.text?.trim().toString())) {
                nameEdit.error
                requireView().snackbar("Login bo'sh")
                return@apply
            } else if (TextUtils.isEmpty(numberEdit.text?.trim().toString())) {
                numberEdit.error
                requireView().snackbar("Telefon raqam bo'sh")
                return@apply
            } else if (TextUtils.isEmpty(passwordEdit.text?.trim().toString())) {
                passwordEdit.error
                requireView().snackbar("Pin bo'sh")
                return@apply
            } else {
                return true
            }
        }
        return false
    }

    private suspend fun checkAdmin(): Boolean {
        viewModel.checkAdminByPhoneNumber(binding.numberEdit.text.toString()).collect { state ->
            when (state) {
                is Resource.Loading -> {
                    requireContext().showToast("Loading...")
                }

                is Resource.Success -> {
                    if (state.data == true) {
                        Permanent.isAdmin = true
                        true
                    } else {
                        false
                    }
                }

                is Resource.Error -> requireContext().showToast("Failed!!! ${state.message}")
            }
        }
        return false
    }

}