package uz.creator.adminpanel.ui.home._addHome

import android.Manifest
import android.app.AlertDialog
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import uz.creator.adminpanel.R
import uz.creator.adminpanel.databinding.FragmentMapBinding
import uz.creator.adminpanel.ui.home.model.AddressModel
import android.content.Intent
import android.location.LocationManager
import android.os.Build
import android.provider.Settings
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import uz.creator.adminpanel.utils.showToast
import uz.creator.adminpanel.utils.snackBar

class MapFragment : Fragment(), OnMapReadyCallback {

    private var _binding: FragmentMapBinding? = null
    val binding get() = _binding!!
    private lateinit var mapFragment: SupportMapFragment
    private lateinit var map: GoogleMap
    private var fusedLocationProviderClient: FusedLocationProviderClient? = null
    private var locationPermissionGranted = false
    private lateinit var addressModel: AddressModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            addressModel = it.getSerializable("addressModel") as AddressModel
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMapBinding.inflate(inflater, container, false)
        loadMap()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnConfirm.setOnClickListener {
//            put the location
            if (this::map.isInitialized) {
                val address = AddressModel(
                    map.cameraPosition.target.latitude,
                    map.cameraPosition.target.longitude
                )
                ViewModelProvider(requireActivity())[ShareAddressModel::class.java].setData(address)
                findNavController().popBackStack()
            }
        }

        binding.locationCard.setOnClickListener {
//            move the camera to user location
            if (locationPermissionGranted) {
                if (isGpsEnabled()) {
                    getDeviceLocation()
                } else {
                    createGpsDialog()
                }
            } else {
                requireContext().showToast("Permission denied!!!")
                moveCameraDefault()
            }
        }
    }

    private fun createGpsDialog() {
//        create alert dialog for turn on gps
        AlertDialog.Builder(requireContext())
            .setTitle("GPS Permission")
            .setMessage("GPS is required for this app to work. Please enable GPS")
            .setPositiveButton(
                "Yes"
            ) { _, _ ->
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(intent)
            }.setCancelable(false)
            .show()
    }

    private fun isGpsEnabled(): Boolean {
//        check gps is turn on
        val locationManager: LocationManager =
            requireContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
    }


    private fun loadMap() {
//        set up the map
        mapFragment = (childFragmentManager.findFragmentById(R.id.mapAPI) as SupportMapFragment?)!!
        val fm = childFragmentManager
        val ft = fm.beginTransaction()
        mapFragment = SupportMapFragment.newInstance()
        ft.replace(R.id.mapAPI, mapFragment).commit()
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
//        move the camera
        map = googleMap
        map.mapType = GoogleMap.MAP_TYPE_HYBRID
        moveCameraDefault()
    }

    private fun getDeviceLocation() {
//        get device location
        try {
            val locationResult = fusedLocationProviderClient?.lastLocation
            locationResult?.addOnSuccessListener { location ->
                if (location != null) {
                    map.animateCamera(
                        CameraUpdateFactory.newLatLngZoom(
                            LatLng(
                                location!!.latitude,
                                location!!.longitude
                            ), 17F
                        ), 1000, null
                    )
                } else {
                    moveCameraDefault()
                    requireContext().showToast("Devise location topilmadi!!!")
                }
            }?.addOnFailureListener {
                moveCameraDefault()
                requireContext().showToast("Devise location topilmadi!!! ${it.message}")
            }
        } catch (e: SecurityException) {
            requireView().snackBar(e.message.toString())
        }
    }

    private fun moveCameraDefault() {
//        move camera where can not get device location
        if (this::addressModel.isInitialized && addressModel != null) {
            map.animateCamera(
                CameraUpdateFactory.newLatLngZoom(
                    LatLng(
                        addressModel.latitude, addressModel.longitude
                    ), 17F
                ), 1000, null
            )
        } else {
            map.animateCamera(
                CameraUpdateFactory.newLatLngZoom(
                    LatLng(
                        39.65755076044682, 66.94765977847547
                    ), 17F
                ), 1000, null
            )
        }
    }


    private fun checkLocationPermission() {
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    requireActivity(),
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            ) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                AlertDialog.Builder(requireContext())
                    .setTitle("Location Permission Needed")
                    .setMessage("This app needs the Location permission, please accept to use location functionality")
                    .setPositiveButton(
                        "OK"
                    ) { _, _ ->
                        //Prompt the user once explanation has been shown
                        requestLocationPermission()
                    }
                    .create()
                    .show()
            } else {
                // No explanation needed, we can request the permission.
                requestLocationPermission()
            }
        } else {
            locationPermissionGranted = true
        }
    }

    private fun requestLocationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_BACKGROUND_LOCATION
                ),
                MY_PERMISSIONS_REQUEST_LOCATION
            )
        } else {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                MY_PERMISSIONS_REQUEST_LOCATION
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            MY_PERMISSIONS_REQUEST_LOCATION -> {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    locationPermissionGranted = ContextCompat.checkSelfPermission(
                        requireContext(),
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) == PackageManager.PERMISSION_GRANTED
                } else {
                    locationPermissionGranted = false
                }
                return
            }
        }
    }

    override fun onStart() {
        super.onStart()
        fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(requireActivity())
        checkLocationPermission()
    }

    companion object {
        private const val MY_PERMISSIONS_REQUEST_LOCATION = 1
    }
}

class ShareAddressModel : ViewModel() {
    private var _data: MutableLiveData<AddressModel> = MutableLiveData()
    val data: LiveData<AddressModel> get() = _data
    fun setData(model: AddressModel) {
        _data.postValue(model)
    }
}