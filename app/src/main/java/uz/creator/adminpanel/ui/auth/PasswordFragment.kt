package uz.creator.adminpanel.ui.auth

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.andrognito.pinlockview.PinLockListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import uz.creator.adminpanel.R
import uz.creator.adminpanel.databinding.FragmentPasswordBinding
import uz.creator.adminpanel.models.User
import uz.creator.adminpanel.utils.Permanent
import uz.creator.adminpanel.utils.snackBar

class PasswordFragment : Fragment(), PinLockListener {

    private lateinit var binding: FragmentPasswordBinding
    private lateinit var firestore: FirebaseFirestore
    private var phoneNumber: String? = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        get user phoneNumber
        phoneNumber =
            requireActivity().getSharedPreferences(Permanent.PREF_PHONE_NAME, Context.MODE_PRIVATE)
                .getString(Permanent.PHONE_KEY, "")
        firestore = Firebase.firestore
        phoneNumber?.let { checkIsAdmin(it) }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPasswordBinding.inflate(layoutInflater)
        binding.apply {
            pinLockView.attachIndicatorDots(indicatorDots)
            pinLockView.setPinLockListener(this@PasswordFragment)
        }
        return binding.root
    }

    override fun onComplete(pin: String?) {
        binding.progressBar.visibility = View.VISIBLE
//        get user object
        firestore.collection("users").document(phoneNumber!!).get().addOnSuccessListener {
            val user = it.toObject(User::class.java)
//            check the user is active and pin
            if (user?.isActive != null && user?.isActive == false) {
                binding.pinLockView.resetPinLockView()
                binding.progressBar.visibility = View.INVISIBLE
                requireView().snackBar("Siz admin tomonidan o'chirilgansiz!!!")
            } else if (user?.pin == pin) {
                binding.pinLockView.resetPinLockView()
                binding.progressBar.visibility = View.INVISIBLE
                Permanent.phoneNumber = user?.phoneNumber ?: ""
                findNavController().popBackStack()
                findNavController().navigate(R.id.homeFragment)
            } else {
                binding.pinLockView.resetPinLockView()
                binding.progressBar.visibility = View.INVISIBLE
                requireView().snackBar("Pin xato kiritildi!!!")
            }
        }.addOnFailureListener {
            it.message?.let { it1 -> requireView().snackBar(it1) }
            binding.pinLockView.resetPinLockView()
            binding.progressBar.visibility = View.INVISIBLE
        }
    }

    override fun onEmpty() {
    }

    override fun onPinChange(pinLength: Int, intermediatePin: String?) {

    }

    private fun checkIsAdmin(phoneNumber: String) {
//        check the user is admin
        firestore.collection("admin").document(phoneNumber).get().addOnSuccessListener {
            Permanent.isAdmin = it.exists()
        }
    }
}