package uz.creator.adminpanel.ui.editPin

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.andrognito.pinlockview.PinLockListener
import com.google.firebase.firestore.FirebaseFirestore
import uz.creator.adminpanel.databinding.FragmentEditPinBinding
import uz.creator.adminpanel.models.User
import uz.creator.adminpanel.utils.MyDialog
import uz.creator.adminpanel.utils.Permanent
import uz.creator.adminpanel.utils.snackBar

class EditPinFragment : Fragment(), PinLockListener {

    private lateinit var binding: FragmentEditPinBinding
    private lateinit var firestore: FirebaseFirestore
    private var checkFeature = 0
    private lateinit var newPin: String
    private lateinit var myDialog: MyDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        firestore = FirebaseFirestore.getInstance()
        myDialog = MyDialog(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentEditPinBinding.inflate(inflater, container, false)
        binding.apply {
            pinLockView.attachIndicatorDots(indicatorDots)
            pinLockView.setPinLockListener(this@EditPinFragment)
        }
        return binding.root
    }

    override fun onComplete(pin: String?) {
        when (checkFeature) {
//            check the pin is first or second
            1 -> {
                binding.pinLockView.resetPinLockView()
                newPin = pin!!
                binding.profileName.text = "Pin-kodni tasdiqlang"
                checkFeature = 2
            }
            2 -> {
                if (newPin == pin) {
                    myDialog.showDialog()
                    firestore.collection("users").document(Permanent.phoneNumber!!)
                        .update("pin", newPin)
                        .addOnSuccessListener {
                            myDialog.dismissDialog()
                            requireView().snackBar("Pin o'zgartirildi!!!")
                            findNavController().popBackStack()
                        }
                        .addOnFailureListener { e ->
                            myDialog.dismissDialog()
                            requireView().snackBar("Pin o'zgartirishda xatolik ro'y berdi!!!")
                            findNavController().popBackStack()
                        }
                } else {
                    binding.pinLockView.resetPinLockView()
                    binding.progressBar.visibility = View.INVISIBLE
                    requireView().snackBar("Tasdiqlash kodi xato!!!")
                }
            }
            else -> {
                binding.progressBar.visibility = View.VISIBLE
                firestore.collection("users").document(Permanent.phoneNumber!!).get()
                    .addOnSuccessListener {
                        val user = it.toObject(User::class.java)
                        if (user?.pin == pin && checkFeature == 0) {
                            binding.pinLockView.resetPinLockView()
                            binding.progressBar.visibility = View.INVISIBLE
                            binding.profileName.text = "Yangi Pin-kodni kiriting"
                            checkFeature = 1
                        } else {
                            binding.pinLockView.resetPinLockView()
                            binding.progressBar.visibility = View.INVISIBLE
                            requireView().snackBar("Pin xato kiritildi!!!")
                        }
                    }
            }
        }
    }

    override fun onEmpty() {
    }

    override fun onPinChange(pinLength: Int, intermediatePin: String?) {
    }
}