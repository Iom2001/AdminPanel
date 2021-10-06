package uz.creator.adminpanel.ui.gallery._filterHome

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import uz.creator.adminpanel.R
import uz.creator.adminpanel.databinding.FragmentFilterBinding
import uz.creator.adminpanel.models.FilterModel

class FilterFragment : Fragment() {

    private lateinit var binding: FragmentFilterBinding
    private var filterModel = FilterModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
//            get filter model
            filterModel = it.getSerializable("filterModel") as FilterModel
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFilterBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpUi()
        binding.filterBtn.setOnClickListener {
//            filter
            setFilterModel()
            ViewModelProvider(requireActivity())[ShareFilterModel::class.java].setData(filterModel)
            findNavController().popBackStack()
        }
    }

    private fun setUpUi() {
        filterType()
        filterHomeType()
        filterAnotherUi()
    }

    private fun filterAnotherUi() {
        if (!filterModel.searchText.isNullOrBlank())
            binding.searchTextTv.setText(filterModel.searchText!!.trim())
        if (!filterModel.startPrice.isNullOrBlank())
            binding.priceStartEdt.setText(filterModel.startPrice)
        if (!filterModel.endPrice.isNullOrBlank())
            binding.priceEndEdt.setText(filterModel.endPrice)
        if (filterModel.startRoom != null)
            binding.roomStartEdt.setText(filterModel.startRoom.toString())
        if (filterModel.endRoom != null)
            binding.roomEndEdt.setText(filterModel.endRoom.toString())
        if (filterModel.startFloor != null)
            binding.floorStartEdt.setText(filterModel.startFloor.toString())
        if (filterModel.endFloor != null)
            binding.floorEndEdt.setText(filterModel.endFloor.toString())
        if (filterModel.startTotalFloor != null)
            binding.totalFloorStartEdt.setText(filterModel.startTotalFloor.toString())
        if (filterModel.endTotalFloor != null)
            binding.totalFloorEndEdt.setText(filterModel.endTotalFloor.toString())
    }

    private fun setFilterModel() {
        val searchText = binding.searchTextTv.text.toString()
        val type = binding.advertiseTypeTv.text.toString()
        val homeType = binding.homeTypeTv.text.toString()
        val priceStart = binding.priceStartEdt.text.toString()
        val priceEnd = binding.priceEndEdt.text.toString()
        val roomStart = binding.roomStartEdt.text.toString()
        val roomEnd = binding.roomEndEdt.text.toString()
        val floorStart = binding.floorStartEdt.text.toString()
        val floorEnd = binding.floorEndEdt.text.toString()
        val totalFloorStart = binding.totalFloorStartEdt.text.toString()
        val totalFloorEnd = binding.totalFloorEndEdt.text.toString()
        if (!searchText.isNullOrBlank()) {
            filterModel.searchText = searchText.trim()
        }
        if (!type.isNullOrBlank()) {
            filterModel.type = type
        }
        if (!homeType.isNullOrBlank()) {
            filterModel.homeType = homeType
        }
        if (!priceStart.isNullOrBlank()) {
            filterModel.startPrice = priceStart
        }
        if (!priceEnd.isNullOrBlank()) {
            filterModel.endPrice = priceEnd
        }
        if (!roomStart.isNullOrBlank()) {
            filterModel.startRoom = roomStart.toInt()
        }
        if (!roomEnd.isNullOrBlank()) {
            filterModel.endRoom = roomEnd.toInt()
        }
        if (!floorStart.isNullOrBlank()) {
            filterModel.startFloor = floorStart.toInt()
        }
        if (!floorEnd.isNullOrBlank()) {
            filterModel.endFloor = floorEnd.toInt()
        }
        if (!totalFloorStart.isNullOrBlank()) {
            filterModel.startTotalFloor = totalFloorStart.toInt()
        }
        if (!totalFloorEnd.isNullOrBlank()) {
            filterModel.endTotalFloor = totalFloorEnd.toInt()
        }
    }

    private fun filterType() {
        if (!filterModel.type.isNullOrBlank()) {
            binding.advertiseTypeTv.setText(filterModel.type)
        }
        val items = listOf("Sotuv", "Ijara", "")
        val adapter =
            ArrayAdapter(requireContext(), R.layout.support_simple_spinner_dropdown_item, items)
        binding.advertiseTypeTv.setAdapter(adapter)
    }

    private fun filterHomeType() {
        if (!filterModel.homeType.isNullOrBlank()) {
            binding.homeTypeTv.setText(filterModel.homeType)
        }
        val items = listOf("Uy", "Kvartira", "Hovli", "Noturar joy", "")
        val adapter =
            ArrayAdapter(requireContext(), R.layout.support_simple_spinner_dropdown_item, items)
        binding.homeTypeTv.setAdapter(adapter)
    }
}

class ShareFilterModel : ViewModel() {
    private var _data: MutableLiveData<FilterModel> = MutableLiveData()
    val data: LiveData<FilterModel> get() = _data
    fun setData(model: FilterModel) {
        _data.postValue(model)
    }
}