package uz.creator.userpanel.fragments.home._addElon

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import uz.creator.adminpanel.R
import uz.creator.adminpanel.databinding.FragmentMapBinding
import uz.creator.userpanel.fragments.home._addElon.model.AddressModel

class MapFragment : Fragment(), OnMapReadyCallback {
    var _binding: FragmentMapBinding? = null
    val binding get() = _binding!!
    lateinit var mapFragment: SupportMapFragment
    private lateinit var mMap: GoogleMap
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMapBinding.inflate(inflater, container, false)
        loadMap()
        binding.btnConfirm.setOnClickListener {
            val address= AddressModel("",mMap.cameraPosition.target.latitude,mMap.cameraPosition.target.longitude)
            ViewModelProvider(requireActivity())[shareAddressModel::class.java].setData(address)
         findNavController().popBackStack()
        }
        return binding.root
    }

    private fun loadMap() {
        mapFragment = (childFragmentManager.findFragmentById(R.id.mapAPI) as SupportMapFragment?)!!
        val fm = childFragmentManager
        val ft = fm.beginTransaction()
        mapFragment = SupportMapFragment.newInstance()
        ft.replace(R.id.mapAPI, mapFragment).commit()
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(LatLng(40.19621393709922, 67.87068902630305), 12f), 1, null)

    }


}
class shareAddressModel: ViewModel(){
 private var _data:MutableLiveData<AddressModel> = MutableLiveData()
    val data:LiveData<AddressModel> get() =_data
    fun setData(model:AddressModel){
        _data.postValue(model)
    }
}