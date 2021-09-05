package uz.creator.adminpanel.ui.elon

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.core.view.get
import androidx.navigation.fragment.findNavController
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import uz.creator.adminpanel.R
import uz.creator.adminpanel.adapters.ElonAdapter
import uz.creator.adminpanel.databinding.FragmentElonBinding
import uz.creator.adminpanel.models.Advertise
import uz.creator.adminpanel.models.User
import uz.creator.adminpanel.utils.Permanent
import uz.creator.adminpanel.utils.snackBar

class ElonFragment : Fragment() {

    private lateinit var binding: FragmentElonBinding
    private lateinit var advertiseList: ArrayList<Advertise>
    private lateinit var elonAdapter: ElonAdapter
    private lateinit var firebaseFirestore: FirebaseFirestore

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
        advertiseList = ArrayList()
        elonAdapter = ElonAdapter(advertiseList, object : ElonAdapter.ElonClick {

            override fun onElonClick(advertise: Advertise, position: Int) {
                var bundle = Bundle()
                bundle.putString("date", advertise.createdTime)
                findNavController().navigate(R.id.homeItemFragment, bundle)
            }

            override fun onElonItemClick(advertise: Advertise, position: Int, view: View) {
                val popupMenu: PopupMenu = PopupMenu(requireContext(), view)
                popupMenu.menuInflater.inflate(R.menu.elon_menu, popupMenu.menu)

                val menuOpts = popupMenu.menu
                advertise.isActive?.let {
                    if (!it) {
                        menuOpts[0].title = "Aktivlashtirish"
                    }
                }

                popupMenu.setOnMenuItemClickListener(PopupMenu.OnMenuItemClickListener { item ->
                    when (item.itemId) {
                        R.id.action_crick -> {
                            advertise.isActive?.let {
                                val washingtonRef =
                                    firebaseFirestore.collection("elonlar")
                                        .document("${advertise.phoneNumber}${advertise.createdTime}")
                                if (it) {

                                    // Set the "isCapital" field of the city 'DC'
                                    washingtonRef
                                        .update("active", false)
                                        .addOnSuccessListener {
                                        }
                                        .addOnFailureListener { e ->
                                        }
                                } else {
                                    washingtonRef
                                        .update("active", true)
                                        .addOnSuccessListener {
                                        }
                                        .addOnFailureListener { e ->
                                        }
                                }
                            }
                        }

                        R.id.action_ftbal -> {
                            // [START delete_document]
                            firebaseFirestore.collection("elonlar")
                                .document("${advertise.phoneNumber}${advertise.createdTime}")
                                .delete()
                                .addOnSuccessListener {
                                    requireView().snackBar("Deleted Successfully")
                                }
                                .addOnFailureListener { e ->
                                    requireView().snackBar("Failed!!! ${e.message}")
                                }
                        }
                    }
                    true
                })
                popupMenu.show()
            }
        })
        binding.rvElon.adapter = elonAdapter
        getAdvertiseList()
    }

    private fun getAdvertiseList() {
        firebaseFirestore.collection("elonlar").whereEqualTo("phoneNumber", Permanent.phoneNumber)
            .addSnapshotListener { snapshots, e ->
                if (e != null) {
                    Log.w("TAG", "listen:error", e)
                    return@addSnapshotListener
                }
                for (a in snapshots!!.documentChanges) {
                    when (a.type) {
                        DocumentChange.Type.ADDED -> {
                            var advertise = a.document.toObject(Advertise::class.java)
                            advertiseList.add(advertise)
                            var listSize = advertiseList.size
                            elonAdapter.notifyItemInserted(listSize - 1)
                            elonAdapter.notifyItemRangeChanged(listSize - 1, listSize)
                        }
                        DocumentChange.Type.MODIFIED -> {
                            val advertise = a.document.toObject(Advertise::class.java)
                            for (i in advertiseList) {
                                if (advertise.phoneNumber == i.phoneNumber) {
                                    val position = advertiseList.indexOf(i)
                                    advertiseList[position] = advertise
                                    elonAdapter.notifyItemChanged(position)
                                    break
                                }
                            }
                        }
                        DocumentChange.Type.REMOVED -> {
                            var advertise = a.document.toObject(Advertise::class.java)
                            val position = advertiseList.indexOf(advertise)
                            advertiseList.remove(advertise)
                            elonAdapter.notifyItemRemoved(position);
                            elonAdapter.notifyItemRangeChanged(position, advertiseList.size);
                        }
                    }
                }
            }
    }

}