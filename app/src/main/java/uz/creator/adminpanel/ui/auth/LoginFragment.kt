package uz.creator.adminpanel.ui.auth

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.text.TextUtils
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.navigation.fragment.findNavController
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.*
import uz.creator.adminpanel.ui.MainActivity
import uz.creator.adminpanel.R
import uz.creator.adminpanel.databinding.FragmentLoginBinding
import uz.creator.adminpanel.models.User
import uz.creator.adminpanel.utils.Permanent
import uz.creator.adminpanel.utils.hideKeyboard
import uz.creator.adminpanel.utils.snackBar
import uz.creator.adminpanel.viewmodel.AdminViewModel

class LoginFragment : Fragment(R.layout.fragment_login) {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: AdminViewModel
    private lateinit var imageUri: Uri
    private var boolean = false
    private lateinit var referenceStorage: StorageReference
    private val storage = FirebaseStorage.getInstance()
    private lateinit var firebaseFirestore: FirebaseFirestore
    private lateinit var dialog: androidx.appcompat.app.AlertDialog
    var userId = ""
    private lateinit var sharedPreferencesRegister: SharedPreferences
    private lateinit var sharedPreferencesPhone: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        userId = Settings.Secure.getString(
            context?.contentResolver,
            Settings.Secure.ANDROID_ID
        )
        sharedPreferencesRegister =
            activity?.getSharedPreferences(Permanent.PREF_REGISTER_NAME, Context.MODE_PRIVATE)!!
        sharedPreferencesPhone =
            activity?.getSharedPreferences(Permanent.PREF_PHONE_NAME, Context.MODE_PRIVATE)!!
        if (requireActivity().getSharedPreferences(
                Permanent.PREF_REGISTER_NAME,
                Context.MODE_PRIVATE
            ).getBoolean(Permanent.REGISTER_KEY, false)
        ) {
            findNavController().popBackStack()
            findNavController().navigate(R.id.passwordFragment)
//            findNavController().popBackStack(R.id.loginFragment, true)
        }
    }


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

        referenceStorage = storage.getReference("userImages")
        firebaseFirestore = FirebaseFirestore.getInstance()
//        firebaseFirestore.collection("admin").document("mainAdmin").set(User("Islomjon", ""))


        binding.getImage.setOnClickListener {
            pickImageFromNewGallery()
        }

        binding.registrationBtn.setOnClickListener {
            requireView().hideKeyboard()
            var username = binding.nameEdit.text.trim().toString()
            var phoneNumber = binding.numberEdit.text?.trim().toString()
            var pinNumber = binding.passwordEdit.text.trim().toString()
            if (!this@LoginFragment::imageUri.isInitialized || !boolean) {
                Toast.makeText(context, "Please select the image!!!", Toast.LENGTH_SHORT).show()
            } else if (checkEmpty(username, phoneNumber, pinNumber)) {
                val llPadding = 30
                val ll = LinearLayout(context)
                ll.orientation = LinearLayout.HORIZONTAL
                ll.setPadding(llPadding, llPadding, llPadding, llPadding)
                ll.gravity = Gravity.CENTER
                var llParam = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                llParam.gravity = Gravity.CENTER
                ll.layoutParams = llParam
                val progressBar = ProgressBar(context)
                progressBar.isIndeterminate = true
                progressBar.setPadding(0, 0, llPadding, 0)
                progressBar.layoutParams = llParam
                llParam = LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
                llParam.gravity = Gravity.CENTER
                val tvText = TextView(context)
                tvText.text = "Loading ..."
                tvText.setTextColor(Color.parseColor("#000000"))
                tvText.textSize = 20f
                tvText.layoutParams = llParam
                ll.addView(progressBar)
                ll.addView(tvText)
                val builder: androidx.appcompat.app.AlertDialog.Builder =
                    androidx.appcompat.app.AlertDialog.Builder(requireContext())
                builder.setCancelable(false)
                builder.setView(ll)
                dialog = builder.create()
                dialog.show()
                firebaseFirestore.collection("users").document(phoneNumber).get()
                    .addOnSuccessListener {
                        if (it.exists()) {
                            var emptyUser = it.toObject(User::class.java)
                            var isCurrentUser: Boolean =
                                emptyUser?.deviceId?.let { it1 -> checkDeviceId(it1) } == true
                            if (isCurrentUser) {
                                val user = User(username, phoneNumber, "", pinNumber, userId)
                                addImage(phoneNumber, user)
                                checkIsAdmin(phoneNumber)
                            } else {
                                Toast.makeText(
                                    context,
                                    "Bu telefon raqam oldin boshqa qurilmada registratsiyadan o'tgan!!!",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        } else {
                            dialog.dismiss()
                            requireView().snackBar("Bu telefon raqam admin tomondan qo'shilmagan")
                        }
                    }.addOnFailureListener {
                        dialog.dismiss()
                        requireView().snackBar("Internet bilan bog'lanishda uzilishlar bor!!!")
                    }
            }
        }
    }

    private fun checkDeviceId(deviceId: String): Boolean {
        if (deviceId.isEmpty())
            return true
        else if (deviceId == userId) {
            return true
        }
        return false
    }

    private fun addImage(phoneNumber: String, user: User) {
        val uploadTask =
            referenceStorage.child("$phoneNumber.jpg").putFile(imageUri)
        uploadTask.addOnSuccessListener { it ->
            if (it.task.isSuccessful) {
                val downloadUrl = it.metadata?.reference?.downloadUrl
                downloadUrl?.addOnSuccessListener { imgUri ->
                    user.imageUri = imgUri.toString()
                    var fireStoreRef =
                        user.phoneNumber?.let { it1 ->
                            firebaseFirestore.collection("users").document(
                                it1
                            )
                        }
                    fireStoreRef?.set(user)
                        ?.addOnSuccessListener {
                            Toast.makeText(
                                context,
                                "Data uploaded successfully!!!",
                                Toast.LENGTH_SHORT
                            ).show()
                            binding.nameEdit.setText("")
                            binding.numberEdit.setText("")
                            binding.passwordEdit.setText("")
                            boolean = false
                            Permanent.phoneNumber = user.phoneNumber ?: ""
                            var editorRegister = sharedPreferencesRegister.edit()
                            editorRegister.putBoolean(Permanent.REGISTER_KEY, true)
                            editorRegister.apply()
                            var editPhone = sharedPreferencesPhone.edit()
                            editPhone.putString(Permanent.PHONE_KEY, user.phoneNumber)
                            editPhone.apply()
                            binding.imagePerson.setImageURI(null)
                            dialog.dismiss()
                            findNavController().popBackStack()
                            findNavController().navigate(R.id.homeFragment)
                        }?.addOnFailureListener {
                            dialog.dismiss()
                            Toast.makeText(context, it.message, Toast.LENGTH_SHORT)
                                .show()
                        }
                }
                downloadUrl?.addOnFailureListener {
                    dialog.dismiss()
                    Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()
                }
            }
        }.addOnFailureListener {
            dialog.dismiss()
            Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()
        }
    }

    private fun checkEmpty(username: String, phoneNumber: String, pinNumber: String): Boolean {
        binding.apply {
            if (TextUtils.isEmpty(username)) {
                nameEdit.error
                requireView().snackBar("Login bo'sh")
                return@apply
            } else if (TextUtils.isEmpty(phoneNumber) || phoneNumber.length != 19) {
                numberEdit.error
                requireView().snackBar("Telefon raqam bo'sh yoki to'liq emas!!!")
                return@apply
            } else if (TextUtils.isEmpty(pinNumber) || pinNumber.length != 4) {
                passwordEdit.error
                requireView().snackBar("Pin bo'sh, yoki 4 ta raqamdan kichkina")
                return@apply
            } else {
                return true
            }
        }
        return false
    }

    private val getImageContent =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri ?: return@registerForActivityResult
            binding.imagePerson.setImageURI(uri)
            imageUri = uri
            boolean = true
        }

    private fun pickImageFromNewGallery() {
        getImageContent.launch("image/*")
    }

    private fun checkIsAdmin(phoneNumber: String) {
        firebaseFirestore.collection("admin").document(phoneNumber).get().addOnSuccessListener {
            if (it.exists()) {
                Permanent.isAdmin = true
            } else {
                Permanent.isAdmin = true
            }
        }
    }
}