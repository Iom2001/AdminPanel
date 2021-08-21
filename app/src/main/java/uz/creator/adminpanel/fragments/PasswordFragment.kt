package uz.creator.adminpanel.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.andrognito.pinlockview.PinLockListener
import uz.creator.adminpanel.databinding.FragmentPasswordBinding
import uz.creator.adminpanel.utils.showToast

class PasswordFragment : Fragment(), PinLockListener {

    private lateinit var binding: FragmentPasswordBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
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
        requireContext().showToast(pin ?: "")
        binding.pinLockView.resetPinLockView()
    }

    override fun onEmpty() {

    }

    override fun onPinChange(pinLength: Int, intermediatePin: String?) {

    }
}