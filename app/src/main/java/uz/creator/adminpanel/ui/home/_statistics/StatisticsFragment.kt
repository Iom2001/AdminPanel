package uz.creator.adminpanel.ui.home._statistics

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
import uz.creator.adminpanel.adapters.StatisticAdapter
import uz.creator.adminpanel.databinding.FragmentStatisticsBinding
import uz.creator.adminpanel.models.Advertise
import uz.creator.adminpanel.utils.MyDialog
import uz.creator.adminpanel.utils.showToast

class StatisticsFragment : Fragment() {

    private lateinit var _binding: FragmentStatisticsBinding
    private val binding get() = _binding
    private lateinit var firebaseFirestore: FirebaseFirestore
    private lateinit var statisticAdapter: StatisticAdapter
    private lateinit var advertiseList: ArrayList<Advertise>
    private lateinit var myDialog: MyDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        firebaseFirestore = FirebaseFirestore.getInstance()
        advertiseList = ArrayList()
//        set up adapter
        statisticAdapter =
            StatisticAdapter(advertiseList, object : StatisticAdapter.StatisticClick {
                override fun onItemClick(advertise: Advertise, position: Int) {
                    var bundle = Bundle()
                    bundle.putString("date", advertise.createdTime)
                    bundle.putString("phoneNumber", advertise.phoneNumber)
                    findNavController().navigate(R.id.homeInfoFragment, bundle)
                }
            })
        myDialog = MyDialog(requireContext())
        myDialog.showDialog()
        getAdvertiseList()
    }

    private fun getAdvertiseList() {
//        get and listen home list
        firebaseFirestore.collection("elonlar")
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
                            var advertise = a.document.toObject(Advertise::class.java)
                            advertiseList.add(advertise)
                            var listSize = advertiseList.size
                            statisticAdapter.notifyItemInserted(listSize - 1)
                            statisticAdapter.notifyItemRangeChanged(listSize - 1, listSize)
                        }
                        DocumentChange.Type.MODIFIED -> {
                            val advertise = a.document.toObject(Advertise::class.java)
                            for (i in advertiseList) {
                                if (advertise.phoneNumber == i.phoneNumber) {
                                    val position = advertiseList.indexOf(i)
                                    advertiseList[position] = advertise
                                    statisticAdapter.notifyItemChanged(position)
                                    break
                                }
                            }
                        }
                        DocumentChange.Type.REMOVED -> {
                            var advertise = a.document.toObject(Advertise::class.java)
                            val position = advertiseList.indexOf(advertise)
                            advertiseList.remove(advertise)
                            statisticAdapter.notifyItemRemoved(position);
                            statisticAdapter.notifyItemRangeChanged(position, advertiseList.size)
                        }
                    }
                }
            }
        myDialog.dismissDialog()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentStatisticsBinding.inflate(inflater, container, false)
        binding.rvHomeList.setHasFixedSize(true)
        binding.rvHomeList.adapter = statisticAdapter
        return binding.root
    }


}