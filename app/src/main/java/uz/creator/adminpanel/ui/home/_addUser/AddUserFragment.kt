package uz.creator.adminpanel.ui.home._addUser

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
import android.widget.PopupMenu
import androidx.core.view.get
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import uz.creator.adminpanel.R
import uz.creator.adminpanel.adapters.UserAdapter
import uz.creator.adminpanel.databinding.AddUserDialogBinding
import uz.creator.adminpanel.databinding.FragmentAddUserBinding
import uz.creator.adminpanel.models.User
import uz.creator.adminpanel.utils.MyDialog
import uz.creator.adminpanel.utils.snackBar

class AddUserFragment : Fragment() {

    private lateinit var binding: FragmentAddUserBinding
    private lateinit var firebaseFirestore: FirebaseFirestore
    private lateinit var userAdapter: UserAdapter
    private lateinit var userList: ArrayList<User>
    private lateinit var myDialog: MyDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        myDialog = MyDialog(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAddUserBinding.inflate(layoutInflater, container, false)
        firebaseFirestore = FirebaseFirestore.getInstance()
        binding.cardAddUser.setOnClickListener {
//            add the user
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
//        set up adapter
        userAdapter = UserAdapter(userList, object : UserAdapter.UserClick {

            override fun moreItemClick(user: User, view: View) {
                val popupMenu = PopupMenu(requireContext(), view)
                popupMenu.menuInflater.inflate(R.menu.user_menu, popupMenu.menu)
                var value = false
                if (user.isActive == false) {
                    val menuOpts = popupMenu.menu
                    menuOpts[0].title = "Aktivlashtirish"
                    value = true
                }
                popupMenu.setOnMenuItemClickListener(PopupMenu.OnMenuItemClickListener { item ->
                    when (item.itemId) {
                        R.id.dis_active_user_btn -> {
//                            activate or anactivate user
                            myDialog.showDialog()
                            firebaseFirestore.collection("users")
                                .document("${user.phoneNumber}")
                                .update("active", value)
                                .addOnSuccessListener {
                                    myDialog.dismissDialog()
                                    var snackText = "Disactived Successfully"
                                    if (value) {
                                        snackText = "Actived Successfully"
                                    }
                                    requireView().snackBar(snackText)
                                }
                                .addOnFailureListener { e ->
                                    myDialog.dismissDialog()
                                    requireView().snackBar("Failed!!! ${e.message}")
                                }
                        }

                        R.id.delete_user_btn -> {
//                            delete user
                            myDialog.showDialog()
                            FirebaseStorage.getInstance().getReference("userImages")
                                .child("${user.phoneNumber}.jpg").delete()
                            firebaseFirestore.collection("users")
                                .document("${user.phoneNumber}")
                                .delete()
                                .addOnSuccessListener {
                                    myDialog.dismissDialog()
                                    requireView().snackBar("Deleted Successfully")
                                }
                                .addOnFailureListener { e ->
                                    myDialog.dismissDialog()
                                    requireView().snackBar("Failed!!! ${e.message}")
                                }
                        }
                    }
                    true
                })
                popupMenu.show()
            }

        })
        binding.rvUser.adapter = userAdapter
//        get and listen the users
        firebaseFirestore.collection("users")
            .addSnapshotListener { snapshots, e ->
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
                            userAdapter.notifyItemRangeChanged(position, userList.size)
                        }
                    }
                }
            }
    }
}