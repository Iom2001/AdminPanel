package uz.creator.adminpanel.ui.home._editHome

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import uz.creator.adminpanel.R
import uz.creator.adminpanel.adapters.HomeAdapter
import uz.creator.adminpanel.databinding.FragmentHomeListBinding
import uz.creator.adminpanel.models.Advertise
import uz.creator.adminpanel.utils.MyDialog
import uz.creator.adminpanel.utils.Permanent
import uz.creator.adminpanel.utils.showToast

class HomeListFragment : Fragment() {

    private lateinit var _binding: FragmentHomeListBinding
    private val binding get() = _binding
    private lateinit var advertiseList: ArrayList<Advertise>
    private lateinit var homeAdapter: HomeAdapter
    private lateinit var firebaseFirestore: FirebaseFirestore
    private lateinit var myDialog: MyDialog
    private lateinit var filterQuery: Query

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        myDialog = MyDialog(requireContext())
        advertiseList = ArrayList()
        firebaseFirestore = FirebaseFirestore.getInstance()
        filterQuery =
            firebaseFirestore.collection("elonlar")
                .orderBy("createdTime", Query.Direction.DESCENDING)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        advertiseList = ArrayList()
//        set up adapter
        homeAdapter = HomeAdapter(advertiseList, object : HomeAdapter.HomeClick {
            override fun onHomeClick(advertise: Advertise, position: Int) {
                var bundle = Bundle()
                bundle.putString("date", advertise.createdTime)
                findNavController().navigate(R.id.editHomeFragment, bundle)
            }
        })
        binding.rvHomeList.setHasFixedSize(true)
        binding.rvHomeList.adapter = homeAdapter
        getAdvertiseList()
    }

    private fun getAdvertiseList() {
//        get and listen home list
        myDialog.showDialog()
        filterQuery.addSnapshotListener { snapshots, e ->
            if (e != null) {
                requireContext().showToast(e.message.toString())
                myDialog.dismissDialog()
                return@addSnapshotListener
            }
            for (a in snapshots!!.documentChanges) {
                when (a.type) {
                    DocumentChange.Type.ADDED -> {
                        var advertise = a.document.toObject(Advertise::class.java)
                        if (advertise.isActive!! && advertise.phoneNumber == Permanent.phoneNumber) {
                            advertiseList.add(advertise)
                            var listSize = advertiseList.size
                            homeAdapter.notifyItemInserted(listSize - 1)
                            homeAdapter.notifyItemRangeChanged(listSize - 1, listSize)
                        }
                    }
                    DocumentChange.Type.MODIFIED -> {
                        val advertise = a.document.toObject(Advertise::class.java)
                        if (advertise.isActive!! && advertise.phoneNumber == Permanent.phoneNumber) {
                            for (i in advertiseList) {
                                if (advertise.phoneNumber == i.phoneNumber) {
                                    val position = advertiseList.indexOf(i)
                                    advertiseList[position] = advertise
                                    homeAdapter.notifyItemChanged(position)
                                    break
                                }
                            }
                        }
                    }
                    DocumentChange.Type.REMOVED -> {
                        var advertise = a.document.toObject(Advertise::class.java)
                        if (advertise.isActive!! && advertise.phoneNumber == Permanent.phoneNumber) {
                            val position = advertiseList.indexOf(advertise)
                            advertiseList.remove(advertise)
                            homeAdapter.notifyItemRemoved(position);
                            homeAdapter.notifyItemRangeChanged(position, advertiseList.size)
                        }
                    }
                }
            }
            myDialog.dismissDialog()
        }
    }
}