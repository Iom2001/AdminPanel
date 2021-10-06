package uz.creator.adminpanel.ui.auth

import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.text.TextUtils
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.navigation.fragment.findNavController
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import id.zelory.compressor.Compressor
import kotlinx.coroutines.*
import uz.creator.adminpanel.R
import uz.creator.adminpanel.databinding.FragmentLoginBinding
import uz.creator.adminpanel.models.User
import uz.creator.adminpanel.utils.*

class LoginFragment : Fragment(R.layout.fragment_login) {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private lateinit var imageUri: Uri
    private var boolean = false
    private lateinit var referenceStorage: StorageReference
    private val storage = FirebaseStorage.getInstance()
    private lateinit var firebaseFirestore: FirebaseFirestore
    private lateinit var myDialog: MyDialog
    var userId = ""
    private lateinit var sharedPreferencesRegister: SharedPreferences
    private lateinit var sharedPreferencesPhone: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        get android id
        userId = Settings.Secure.getString(
            context?.contentResolver,
            Settings.Secure.ANDROID_ID
        )

//        check is registered
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
        }
        myDialog = MyDialog(requireContext())
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

        referenceStorage = storage.getReference("userImages")
        firebaseFirestore = FirebaseFirestore.getInstance()

        binding.getImage.setOnClickListener {
            pickImageFromNewGallery()
        }

        binding.registrationBtn.setOnClickListener {
//            registration the user
            requireView().hideKeyboard()
            var username = binding.nameEdit.text.trim().toString()
            var phoneNumber = binding.numberEdit.text?.trim().toString()
            var pinNumber = binding.passwordEdit.text.trim().toString()
            if (!this@LoginFragment::imageUri.isInitialized || !boolean) {
                Toast.makeText(context, "Please select the image!!!", Toast.LENGTH_SHORT).show()
            } else if (checkEmpty(username, phoneNumber, pinNumber)) {
                myDialog.showDialog()
                firebaseFirestore.collection("users").document(phoneNumber).get()
                    .addOnSuccessListener {
                        if (it.exists()) {
                            var emptyUser = it.toObject(User::class.java)
                            var isCurrentUser: Boolean =
                                emptyUser?.deviceId?.let { it1 -> checkDeviceId(it1) } == true
                            if (isCurrentUser) {
                                if (!emptyUser?.pin.isNullOrBlank() && emptyUser?.pin != pinNumber) {
                                    myDialog.dismissDialog()
                                    Toast.makeText(
                                        context,
                                        "Pin xato kiritilgan!!!",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                } else if (emptyUser?.isActive != null && emptyUser?.isActive == false) {
                                    myDialog.dismissDialog()
                                    Toast.makeText(
                                        context,
                                        "Siz admin tomonidan o'chirilgansiz!!!",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                } else {
                                    val user =
                                        User(
                                            CyrillicLatinConverter.ctl(username),
                                            phoneNumber,
                                            "",
                                            pinNumber,
                                            userId,
                                            true
                                        )
                                    runBlocking {
                                        addImage(phoneNumber, user)
                                    }
                                    checkIsAdmin(phoneNumber)
                                }
                            } else {
                                Toast.makeText(
                                    context,
                                    "Bu telefon raqam oldin boshqa qurilmada registratsiyadan o'tgan!!!",
                                    Toast.LENGTH_SHORT
                                ).show()
                                myDialog.dismissDialog()
                            }
                        } else {
                            myDialog.dismissDialog()
                            requireView().snackBar("Bu telefon raqam admin tomondan qo'shilmagan")
                        }
                    }.addOnFailureListener {
                        myDialog.dismissDialog()
                        requireView().snackBar("Internet bilan bog'lanishda uzilishlar bor!!!")
                    }
            }
        }
    }

    private fun checkDeviceId(deviceId: String): Boolean {
//        Check user device id
        if (deviceId.isEmpty())
            return true
        else if (deviceId == userId) {
            return true
        }
        return false
    }

    private suspend fun addImage(phoneNumber: String, user: User) {
        coroutineScope {
//        add the user image
            val uploadTask =
                referenceStorage.child("$phoneNumber.jpg").putFile(
                    Uri.parse(
                        "file://${
                            Compressor.compress(
                                requireContext(),
                                FileUtilsForImage.from(
                                    context,
                                    imageUri
                                )
                            )
                        }"
                    )
                )
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
                                var editorRegister = sharedPreferencesRegister.edit()
                                editorRegister.putBoolean(Permanent.REGISTER_KEY, true)
                                editorRegister.apply()
                                var editPhone = sharedPreferencesPhone.edit()
                                editPhone.putString(Permanent.PHONE_KEY, user.phoneNumber)
                                editPhone.apply()
                                Permanent.phoneNumber = user.phoneNumber ?: ""
                                binding.imagePerson.setImageURI(null)
                                myDialog.dismissDialog()
                                findNavController().popBackStack()
                                findNavController().navigate(R.id.homeFragment)
                            }?.addOnFailureListener {
                                myDialog.dismissDialog()
                                Toast.makeText(context, it.message, Toast.LENGTH_SHORT)
                                    .show()
                            }
                    }
                    downloadUrl?.addOnFailureListener {
                        myDialog.dismissDialog()
                        Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()
                    }
                }
            }.addOnFailureListener {
                myDialog.dismissDialog()
                Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun checkEmpty(username: String, phoneNumber: String, pinNumber: String): Boolean {

//        check the user fields for empty

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
//        check the user is admin
        firebaseFirestore.collection("admin").document(phoneNumber).get().addOnSuccessListener {
            Permanent.isAdmin = it.exists()
        }
    }
}