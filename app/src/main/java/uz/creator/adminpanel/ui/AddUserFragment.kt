package uz.creator.adminpanel.ui

import android.app.AlertDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import uz.creator.adminpanel.R
import uz.creator.adminpanel.adapters.UserAdapter
import uz.creator.adminpanel.databinding.AddUserDialogBinding
import uz.creator.adminpanel.databinding.FragmentAddUserBinding
import uz.creator.adminpanel.models.User
import uz.creator.adminpanel.utils.snackBar

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class AddUserFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var binding: FragmentAddUserBinding
    private lateinit var firebaseFirestore: FirebaseFirestore
    private lateinit var userAdapter: UserAdapter
    private lateinit var userList: ArrayList<User>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAddUserBinding.inflate(layoutInflater, container, false)
        firebaseFirestore = FirebaseFirestore.getInstance()
        binding.cardAddUser.setOnClickListener {
            val alertDialog = AlertDialog.Builder(context)
            val dialog = alertDialog!!.create()
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT));
            val dialogView: View = layoutInflater.inflate(
                R.layout.add_user_dialog,
                null,
                false
            )
            dialog.setView(dialogView)
            val bindDialog = AddUserDialogBinding.bind(dialogView)

            bindDialog.addBtn.setOnClickListener {
                var phoneNumber = bindDialog.numberEdit.text.toString()
                if (phoneNumber.length == 19) {
                    val user = User("", phoneNumber, "", "", "")
                    var fireStoreRef =
                        firebaseFirestore.collection("users").document(
                            phoneNumber
                        )
                    fireStoreRef?.set(user)
                } else {
                    requireView().snackBar("Telefon raqam to'liq kiritilmagan!!!")
                }
                dialog.dismiss()
            }

            bindDialog.cancelBtn.setOnClickListener {
                dialog.dismiss()
            }
            dialog.show()
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        userList = ArrayList()
        userAdapter = UserAdapter(userList)
        binding.rvUser.adapter = userAdapter
        firebaseFirestore.collection("users").addSnapshotListener { snapshots, e ->
            if (e != null) {
                Log.w("TAG", "listen:error", e)
                return@addSnapshotListener
            }
            for (a in snapshots!!.documentChanges) {
                when (a.type) {
                    DocumentChange.Type.ADDED -> {
                        var user = a.document.toObject(User::class.java)
                        userList.add(user)
                        var listSize = userList.size
                        userAdapter.notifyItemInserted(listSize - 1)
                        userAdapter.notifyItemRangeChanged(listSize - 1, listSize)
                    }
                    DocumentChange.Type.MODIFIED -> {
                        val user = a.document.toObject(User::class.java)
                        for (i in userList) {
                            if (user.phoneNumber == i.phoneNumber) {
                                val position = userList.indexOf(i)
                                userList[position] = user
                                userAdapter.notifyItemChanged(position)
                                break
                            }
                        }
                    }
                    DocumentChange.Type.REMOVED -> {
                        var user = a.document.toObject(User::class.java)
                        val position = userList.indexOf(user)
                        userList.remove(user)
                        userAdapter.notifyItemRemoved(position);
                        userAdapter.notifyItemRangeChanged(position, userList.size);
                    }
                }
            }
        }
    }
}