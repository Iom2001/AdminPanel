package uz.creator.adminpanel.ui.elon

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import uz.creator.adminpanel.R
import uz.creator.adminpanel.adapters.ElonAdapter
import uz.creator.adminpanel.databinding.FragmentElonBinding
import uz.creator.adminpanel.models.Elon
import uz.creator.adminpanel.models.FilterModel
import uz.creator.adminpanel.ui.gallery._filterHome.ShareFilterModel
import uz.creator.adminpanel.utils.MyDialog
import uz.creator.adminpanel.utils.Permanent
import uz.creator.adminpanel.utils.showToast
import uz.creator.adminpanel.utils.snackBar

class ElonFragment : Fragment() {

    private lateinit var binding: FragmentElonBinding
    private lateinit var elonList: ArrayList<Elon>
    private lateinit var elonAdapter: ElonAdapter
    private lateinit var firebaseFirestore: FirebaseFirestore
    private lateinit var myDialog: MyDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        myDialog = MyDialog(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentElonBinding.inflate(inflater, container, false)
        firebaseFirestore = FirebaseFirestore.getInstance()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        elonList = ArrayList()
//        initialize the adapter
        elonAdapter = ElonAdapter(elonList, object : ElonAdapter.ElonClick {

            override fun onElonClick(elon: Elon, position: Int) {

            }

            override fun onElonItemClick(elon: Elon, position: Int, view: View) {
//                show menu when elon icon is pressed
                val popupMenu = PopupMenu(requireContext(), view)
                if (Permanent.isAdmin) {
                    popupMenu.menuInflater.inflate(R.menu.elon_admin_menu, popupMenu.menu)
                } else {
                    popupMenu.menuInflater.inflate(R.menu.elon_menu, popupMenu.menu)
                }

                popupMenu.setOnMenuItemClickListener(PopupMenu.OnMenuItemClickListener { item ->
                    when (item.itemId) {
                        R.id.action_crick -> {
//                            show elon location in map
                            if (elon.geoPoint != null) {
                                val gmmIntentUri =
                                    Uri.parse("geo:0,0?q=${elon.geoPoint?.latitude},${elon.geoPoint?.longitude}(Google+Uzbekistan)")
                                val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
                                mapIntent.setPackage("com.google.android.apps.maps")
                                startActivity(mapIntent)
                            } else {
                                requireContext().showToast("Xaritadan hudud tanlanmagan")
                            }
                        }

                        R.id.action_ftbal -> {
//                            delete elon
                            firebaseFirestore.collection("elonforsearch")
                                .document("${elon.phoneNumber}${elon.createdTime}")
                                .delete()
                                .addOnSuccessListener {
                                    requireView().snackBar("Deleted Successfully")
                                }
                                .addOnFailureListener { e ->
                                    requireView().snackBar("Failed!!! ${e.message}")
                                }
                        }

                        R.id.action_filter -> {
                            val filterModel =
                                FilterModel(type = elon.type, homeType = elon.homeType)
                            if (elon.price!!.toInt() >= 5000) {
                                filterModel.startPrice = (elon.price!!.toInt() - 5000).toString()
                                filterModel.endPrice = (elon.price!!.toInt() + 5000).toString()
                            } else {
                                filterModel.startPrice = "0"
                                filterModel.endPrice = (elon.price!!.toInt() + 5000).toString()
                            }
                            if (!elon.homeDesc.isNullOrBlank()) {
                                filterModel.searchText = elon.homeDesc
                            }
                            ViewModelProvider(requireActivity())[ShareFilterModel::class.java].setData(
                                filterModel
                            )
                            findNavController().popBackStack()
                            findNavController().navigate(R.id.galeryFragment)
                        }
                    }
                    true
                })
                popupMenu.show()
            }
        })
        binding.rvElon.adapter = elonAdapter
        getElonList()
        binding.cardAddElon.setOnClickListener {
            findNavController().navigate(R.id.addElonFragment)
        }
    }

    private fun getElonList() {
//        get and listen elon list from the firestore
        myDialog.showDialog()
        firebaseFirestore.collection("elonforsearch")
            .orderBy("createdTime", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshots, e ->
                if (e != null) {
                    requireContext().showToast(e.message.toString())
                    myDialog.dismissDialog()
                    return@addSnapshotListener
                }
                for (a in snapshots!!.documentChanges) {
                    when (a.type) {
                        DocumentChange.Type.ADDED -> {
                            var elon = a.document.toObject(Elon::class.java)
                            elonList.add(elon)
                            var listSize = elonList.size
                            elonAdapter.notifyItemInserted(listSize - 1)
                            elonAdapter.notifyItemRangeChanged(listSize - 1, listSize)
                        }
                        DocumentChange.Type.MODIFIED -> {
                            val elon = a.document.toObject(Elon::class.java)
                            for (i in elonList) {
                                if (elon.phoneNumber == i.phoneNumber) {
                                    val position = elonList.indexOf(i)
                                    elonList[position] = elon
                                    elonAdapter.notifyItemChanged(position)
                                    break
                                }
                            }
                        }
                        DocumentChange.Type.REMOVED -> {
                            var elon = a.document.toObject(Elon::class.java)
                            val position = elonList.indexOf(elon)
                            elonList.remove(elon)
                            elonAdapter.notifyItemRemoved(position);
                            elonAdapter.notifyItemRangeChanged(position, elonList.size)
                        }
                    }
                }
                myDialog.dismissDialog()
            }
    }

}