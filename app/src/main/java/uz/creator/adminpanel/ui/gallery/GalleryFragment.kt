package uz.creator.adminpanel.ui.gallery

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage
import uz.creator.adminpanel.R
import uz.creator.adminpanel.adapters.GalleryAdapter
import uz.creator.adminpanel.databinding.FragmentGaleryBinding
import uz.creator.adminpanel.models.Advertise
import uz.creator.adminpanel.models.FilterModel
import uz.creator.adminpanel.ui.gallery._filterHome.ShareFilterModel
import kotlin.collections.ArrayList

class GalleryFragment : Fragment() {

    private lateinit var binding: FragmentGaleryBinding
    private lateinit var storage: FirebaseStorage
    private lateinit var firestore: FirebaseFirestore

    //        private lateinit var galleryAdapter: FirestorePagingAdapter<Advertise, Vh>
    private lateinit var galleryAdapter: GalleryAdapter
    private lateinit var advertiseList: ArrayList<Advertise>
    private lateinit var filterQuery: Query
    private var filterModel = FilterModel()
    private var lastTextChange = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        firestore = FirebaseFirestore.getInstance()
        storage = FirebaseStorage.getInstance()
        filterQuery =
            firestore.collection("elonlar")
                .orderBy("createdTime", Query.Direction.DESCENDING)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentGaleryBinding.inflate(layoutInflater, container, false)
        setUpAdapter()
        advertiseList = ArrayList()
        getAdvertiseList()
        listenFilter()
        return binding.root
    }

    private fun setUpAdapter() {
        galleryAdapter = GalleryAdapter(object : GalleryAdapter.GalleryClick {
            override fun onGalleryClick(advertise: Advertise, position: Int) {
                var bundle = Bundle()
                bundle.putString("date", advertise.createdTime)
                bundle.putString("phoneNumber", advertise.phoneNumber)
                findNavController().navigate(R.id.homeInfoFragment, bundle)
            }
        })
        binding.rvGallery.setHasFixedSize(true)
        binding.rvGallery.adapter = galleryAdapter
    }

    private fun listenFilter() {
//        Listen the FilterModel
        ViewModelProvider(requireActivity())[ShareFilterModel::class.java].data.observe(
            viewLifecycleOwner,
            { t ->
                if (t != null) {
                    filterModel = t
                    if (!filterModel.searchText.isNullOrBlank() && lastTextChange != filterModel.searchText) {
                        if (lastTextChange != filterModel.searchText)
                            binding.searchView.setText(filterModel.searchText)
                    } else {
                        galleryAdapter.filterList(advertiseList, filterModel)
                    }
                }
            })
    }

    private fun getAdvertiseList() {
//        get and listen home list
        binding.swipeRefreshLayout.isRefreshing = true
        filterQuery.addSnapshotListener { snapshots, e ->
            if (e != null) {
                Log.w("TAG", "listen:error", e)
                return@addSnapshotListener
            }
            for (a in snapshots!!.documentChanges) {
                when (a.type) {
                    DocumentChange.Type.ADDED -> {
                        var advertise = a.document.toObject(Advertise::class.java)
                        if (advertise.isActive!!) {
                            advertiseList.add(advertise)
                            galleryAdapter.filterList(advertiseList, filterModel)
                        }
                        binding.swipeRefreshLayout.isRefreshing = false
                    }
                    DocumentChange.Type.MODIFIED -> {
                        val advertise = a.document.toObject(Advertise::class.java)
                        if (advertise.isActive!!) {
                            for (i in advertiseList) {
                                if (advertise.phoneNumber == i.phoneNumber) {
                                    val position = advertiseList.indexOf(i)
                                    advertiseList[position] = advertise
                                    break
                                }
                            }
                            galleryAdapter.filterList(advertiseList, filterModel)
                        }
                        binding.swipeRefreshLayout.isRefreshing = false
                    }
                    DocumentChange.Type.REMOVED -> {
                        var advertise = a.document.toObject(Advertise::class.java)
                        if (advertise.isActive!!) {
                            advertiseList.remove(advertise)
                            galleryAdapter.filterList(advertiseList, filterModel)
                        }
                        binding.swipeRefreshLayout.isRefreshing = false
                    }
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.filterImg.setOnClickListener {
//            go to filter fragment
            val bundle = Bundle()
            bundle.putSerializable("filterModel", filterModel)
            findNavController().navigate(R.id.filterFragment, bundle)
        }

        // Refresh Action on Swipe Refresh Layout
        binding.swipeRefreshLayout.setOnRefreshListener {
            galleryAdapter.notifyDataSetChanged()
            binding.swipeRefreshLayout.isRefreshing = false
        }

//        listen the search edit text
        binding.searchView.addTextChangedListener { editable ->
            if (editable != null && editable.toString() != lastTextChange) {
                filterModel.searchText = editable.toString().trim()
                lastTextChange = filterModel.searchText!!
                galleryAdapter.filterList(advertiseList, filterModel)
            }
        }

//        clear filter
        binding.clearImg.setOnClickListener {
            filterModel =
                FilterModel("", null, null, null, null, null, null, null, null, null, null)
            ViewModelProvider(requireActivity())[ShareFilterModel::class.java].setData(filterModel)
            lastTextChange = ""
            binding.searchView.setText("")
        }
    }

//    private fun setAdapter() {
//        // Init Paging Configuration
//        val config = PagedList.Config.Builder()
//            .setEnablePlaceholders(false)
//            .setPrefetchDistance(4)
//            .setPageSize(20)
//            .build()
//
//        // Init Adapter Configuration
//        val options = FirestorePagingOptions.Builder<Advertise>()
//            .setLifecycleOwner(this)
//            .setQuery(filterQuery, config, Advertise::class.java)
//            .build()
//
//        galleryAdapter = object : FirestorePagingAdapter<Advertise, Vh>(options) {
//
//            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Vh {
//                val itemGalleryHomeBinding = ItemGalleryHomeBinding.inflate(
//                    LayoutInflater.from(parent.context),
//                    parent,
//                    false
//                )
//                return Vh(itemGalleryHomeBinding, object : Vh.GalleryClick {
//                    override fun onGalleryClick(advertise: Advertise, position: Int) {
//                        var bundle = Bundle()
//                        bundle.putString("date", advertise.createdTime)
//                        bundle.putString("phoneNumber", advertise.phoneNumber)
//                        findNavController().navigate(R.id.homeItemFragment, bundle)
//                    }
//                })
//            }
//
//            override fun onBindViewHolder(p0: Vh, p1: Int, p2: Advertise) {
//                p0.onBind(p2, p1)
//            }
//
//            override fun onError(e: Exception) {
//                super.onError(e)
//                Log.e("GalleryFragment", e.message.toString())
//            }
//
//            override fun onLoadingStateChanged(state: LoadingState) {
//                when (state) {
//                    LoadingState.LOADING_INITIAL -> {
//                        binding.swipeRefreshLayout.isRefreshing = true
//                    }
//
//                    LoadingState.LOADING_MORE -> {
//                        binding.swipeRefreshLayout.isRefreshing = true
//                    }
//
//                    LoadingState.LOADED -> {
//                        binding.swipeRefreshLayout.isRefreshing = false
//                    }
//
//                    LoadingState.ERROR -> {
//                        Toast.makeText(
//                            requireContext(),
//                            "Error Occurred!",
//                            Toast.LENGTH_SHORT
//                        ).show()
//                        binding.swipeRefreshLayout.isRefreshing = false
//                    }
//
//                    LoadingState.FINISHED -> {
//                        binding.swipeRefreshLayout.isRefreshing = false
//                    }
//                }
//            }
//        }
//    }

//    private fun setQuery() {
//        filterQuery =
//            firestore.collection("elonlar")
//                .orderBy("createdTime", Query.Direction.DESCENDING)
//        if (filterModel != null) {
//            if (!filterModel.type.isNullOrBlank()) {
//                filterQuery.whereEqualTo("type", filterModel.type)
//            }
//            if (!filterModel.homeType.isNullOrBlank()) {
//                filterQuery.whereEqualTo("homeType", filterModel.homeType)
//            }
//            if (!filterModel.startPrice.isNullOrBlank()) {
//                filterQuery.whereGreaterThanOrEqualTo(
//                    "price",
//                    filterModel.startPrice!!.toDouble()
//                )
//            }
//            if (!filterModel.endPrice.isNullOrBlank()) {
//                filterQuery.whereLessThanOrEqualTo("price", filterModel.endPrice!!.toDouble())
//            }
//            if (filterModel.startRoom != null) {
//                filterQuery.whereGreaterThanOrEqualTo("roomCount", filterModel.startRoom!!)
//            }
//            if (filterModel.endRoom != null) {
//                filterQuery.whereLessThanOrEqualTo("roomCount", filterModel.endRoom!!)
//            }
//            if (filterModel.startFloor != null) {
//                filterQuery.whereGreaterThanOrEqualTo("floor", filterModel.startFloor!!)
//            }
//            if (filterModel.endFloor != null) {
//                filterQuery.whereLessThanOrEqualTo("floor", filterModel.endFloor!!)
//            }
//            if (filterModel.startTotalFloor != null) {
//                filterQuery.whereGreaterThanOrEqualTo(
//                    "totalFloor",
//                    filterModel.startTotalFloor!!
//                )
//            }
//            if (filterModel.endTotalFloor != null) {
//                filterQuery.whereLessThanOrEqualTo("totalFloor", filterModel.endTotalFloor!!)
//            }
//        }
//    }

}