package uz.creator.adminpanel.ui.home._addHome

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.location.Address
import android.location.Geocoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.darsh.multipleimageselect.activities.AlbumSelectActivity
import com.darsh.multipleimageselect.helpers.Constants
import com.darsh.multipleimageselect.models.Image
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.storage.FirebaseStorage
import com.synnapps.carouselview.ImageListener
import uz.creator.adminpanel.R
import uz.creator.adminpanel.databinding.FragmentNewAddHomeBinding
import uz.creator.adminpanel.models.Advertise
import uz.creator.adminpanel.ui.home.model.AddressModel
import uz.creator.adminpanel.utils.CyrillicLatinConverter
import uz.creator.adminpanel.utils.MyDialog
import uz.creator.adminpanel.utils.Permanent
import uz.creator.adminpanel.utils.snackBar
import java.util.*
import kotlin.collections.ArrayList
import java.io.IOException
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class NewAddHomeFragment : Fragment() {

    private var _binding: FragmentNewAddHomeBinding? = null
    private val binding get() = _binding!!
    private val uriList = ArrayList<Uri>()
    var imagelist = ArrayList<String>()
    private lateinit var addressModel: AddressModel
    private lateinit var firebaseFirestore: FirebaseFirestore
    private lateinit var storage: FirebaseStorage
    private lateinit var myDialog: MyDialog
    private val checkedItemsHave =
        booleanArrayOf(false, false, false, false, false, false, false, false, false)
    private val checkedItemsNear =
        booleanArrayOf(false, false, false, false, false, false, false, false, false)
    private val listHave = arrayOf(
        "Konditsioner",
        "Kir yuvish mashinasi",
        "Muzlatkich",
        "Televizor",
        "Internet",
        "Kabelli TV",
        "Telefon",
        "Oshxona",
        "Balkon"
    )
    private val listNear = arrayOf(
        "Kasalxona",
        "Bolalar maydoni",
        "Bolalar bog'chasi",
        "Bekatlar",
        "Park, yashil zona",
        "Restoran, kafe",
        "Turargoh",
        "Supermaret, do'kon",
        "Maktab"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        myDialog = MyDialog(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentNewAddHomeBinding.inflate(layoutInflater, container, false)
//        put image default the image pager view
        if (imagelist.size == 0) {
            binding.carouselView.setImageListener { _, imageView ->
                Glide.with(requireContext()).load(R.drawable.home_placeholder).into(imageView)
            }
        } else {
            binding.carouselView.setImageListener(imageListener)
            binding.carouselView.pageCount = uriList.size
        }
        firebaseFirestore = FirebaseFirestore.getInstance()
        storage = FirebaseStorage.getInstance()
        setUpUi()
        binding.carouselView.pageCount = 1

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.camera.setOnClickListener {
//            get image
            val intent = Intent(activity, AlbumSelectActivity::class.java)
            intent.putExtra(Constants.INTENT_EXTRA_LIMIT, 10)
            resultLauncher.launch(intent)
        }

        binding.homeHaveEditText.setOnClickListener {
//            get things in the house
            val alertDialog = AlertDialog.Builder(requireContext())
            alertDialog.setTitle("Uyda bor narsalarni tanlang.")
            alertDialog.setCancelable(false)
            alertDialog.setMultiChoiceItems(
                listHave,
                checkedItemsHave
            ) { _, which, isChecked -> checkedItemsHave[which] = isChecked }
            alertDialog.setPositiveButton("Ok") { _, _ ->
                setHaveUi()
            }
            alertDialog.show()
        }

        binding.homeNearEditText.setOnClickListener {
//            get the home near sites
            val alertDialog = AlertDialog.Builder(requireContext())
            alertDialog.setTitle("Uyning yaqinida joylashgan.")
            alertDialog.setCancelable(false)
            alertDialog.setMultiChoiceItems(
                listNear,
                checkedItemsNear
            ) { _, which, isChecked -> checkedItemsNear[which] = isChecked }
            alertDialog.setPositiveButton("Ok") { _, _ ->
                setNearUi()
            }
            alertDialog.show()
        }

        binding.btnUploadData.setOnClickListener {
            if (checkEmpty()) {
                myDialog.showDialog()
                val type = CyrillicLatinConverter.ctl(binding.type.text.toString())
                val homeType = CyrillicLatinConverter.ctl(binding.homeType.text.toString())
                val condition = CyrillicLatinConverter.ctl(binding.condition.text.toString())
                val roomCount = binding.roomCount.text.toString()
                val foundation = CyrillicLatinConverter.ctl(binding.foundation.text.toString())
                val totalFloor = binding.totalFloor.text.toString()
                val floor = binding.floor.text.toString()
                val desc = CyrillicLatinConverter.ctl(binding.homeDescEditText.text.toString())
                val address = CyrillicLatinConverter.ctl(binding.addressEditText.text.toString())
                val price = binding.priceEditText.text.toString()
                val name = CyrillicLatinConverter.ctl(binding.usernameEditText.text.toString())
                val homePhone = binding.numberEditText.text.toString()
                val geoPoint = GeoPoint(addressModel.latitude, addressModel.longitude)

                val currentDT = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    val current = LocalDateTime.now()
                    val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:MM:SS")
                    current.format(formatter)
                } else {
                    val simpleDateFormat = SimpleDateFormat("dd-MM-yyyy HH:MM:SS")
                    simpleDateFormat.format(Date())
                }

                loadImages(currentDT)
                val haveList = ArrayList<Boolean>()
                for (i in checkedItemsHave) {
                    haveList.add(i)
                }
                val nearList = ArrayList<Boolean>()
                for (i in checkedItemsNear) {
                    nearList.add(i)
                }
                var advertise = Advertise(
                    type,
                    name,
                    desc,
                    address,
                    price.toInt(),
                    true,
                    condition,
                    homeType,
                    Permanent.phoneNumber,
                    homePhone,
                    geoPoint,
                    roomCount.toInt(),
                    floor.toInt(),
                    totalFloor.toInt(),
                    foundation,
                    currentDT,
                    haveList,
                    nearList,
                )
                firebaseFirestore.collection("elonlar")
                    .document("${Permanent.phoneNumber}${advertise.createdTime}")
                    .set(advertise)
                    .addOnSuccessListener {
                        myDialog.dismissDialog()
                        requireView().snackBar("Uploaded successfully!!!")
                        findNavController().popBackStack()
                    }.addOnCanceledListener {
                        myDialog.dismissDialog()
                        requireView().snackBar("Canceled!!!")
                        findNavController().popBackStack()
                    }.addOnFailureListener {
                        myDialog.dismissDialog()
                        requireView().snackBar(it.message.toString())
                        findNavController().popBackStack()
                    }
            }
        }

        binding.mapUpload.setOnClickListener {
            if (this::addressModel.isInitialized && addressModel != null) {
                val bundle = Bundle()
                bundle.putSerializable("addressModel", addressModel)
                findNavController().navigate(
                    R.id.action_newAddElonFragment_to_mapFragment,
                    bundle
                )
            } else {
                findNavController().navigate(R.id.action_newAddElonFragment_to_mapFragment)
            }
        }

//        findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<AddressModel>("address")
//            ?.observe(viewLifecycleOwner) {
//                Toast.makeText(requireContext(), it.toString() + "", Toast.LENGTH_SHORT).show()
//                addressModel = it
//                getAddress()
//            }
        ViewModelProvider(requireActivity())[ShareAddressModel::class.java].data.observe(
            viewLifecycleOwner,
            { t ->
                if (t != null) {
                    addressModel = t
                }
            })
    }

    private fun setNearUi() {
//        the home near sites set the textview
        var near = ""
        for (i in listNear.indices) {
            if (checkedItemsNear[i]) {
                if (near.isEmpty()) {
                    near += listNear[i]
                } else {
                    near += "," + listNear[i]
                }
            }
        }
        binding.homeNearEditText.setText(near)
    }

    private fun setHaveUi() {
//        things in the house set the textview
        var have = ""
        for (i in listHave.indices) {
            if (checkedItemsHave[i]) {
                if (have.isEmpty()) {
                    have += listHave[i]
                } else {
                    have += "," + listHave[i]
                }
            }
        }
        binding.homeHaveEditText.setText(have)
    }

    private fun loadImages(currentDT: String) {
        for (i in uriList.indices) {
            storage.getReference("elonImages")
                .child("${Permanent.phoneNumber}${currentDT}")
                .child("${i}.jpg")
                .putFile(uriList[i])
        }
    }

    private fun checkEmpty(): Boolean {

        val type = binding.type.text.toString()
        val homeType = binding.homeType.text.toString()
        val condition = binding.condition.text.toString()
        val roomCount = binding.roomCount.text.toString()
        val foundation = binding.foundation.text.toString()
        val totalFloor = binding.totalFloor.text.toString()
        val floor = binding.floor.text.toString()
        val desc = binding.homeDescEditText.text.toString()
        val address = binding.addressEditText.text.toString()
        val price = binding.priceEditText.text.toString()
        val name = binding.usernameEditText.text.toString()
        val homePhone = binding.numberEditText.text.toString()
        if (type.isBlank()) {
            binding.typeLayout.error = "Select the fields!!!"
            return false
        } else {
            binding.typeLayout.error = null
        }
        if (homeType.isBlank()) {
            binding.homeTypeLayout.error = "Select the fields!!!"
            return false
        } else {
            binding.homeTypeLayout.error = null
        }
        if (roomCount.isBlank()) {
            binding.roomCountLayout.error = "Select the fields!!!"
            return false
        } else {
            binding.roomCountLayout.error = null
        }
        if (condition.isBlank()) {
            binding.conditionLayout.error = "Select the fields!!!"
            return false
        } else {
            binding.conditionLayout.error = null
        }
        if (foundation.isBlank()) {
            binding.foundationLayout.error = "Select the fields!!!"
            return false
        } else {
            binding.foundationLayout.error = null
        }
        if (totalFloor.isBlank()) {
            binding.totalFloorLayout.error = "Fill the blank!!!"
            return false
        } else {
            binding.totalFloorLayout.error = null
        }
        if (floor.isBlank()) {
            binding.floorLayout.error = "Fill the blank!!!"
            return false
        } else {
            binding.floorLayout.error = null
        }
        if (!this::addressModel.isInitialized) {
            requireView().snackBar("Select a location on the map!!!")
            return false
        }
        if (desc.isBlank()) {
            binding.homeDescLayout.error = "Fill the blank!!!"
            return false
        } else {
            binding.homeDescLayout.error = null
        }
        if (address.isBlank()) {
            binding.addressLayout.error = "Fill the blank!!!"
            return false
        } else {
            binding.addressLayout.error = null
        }
        if (price.isBlank()) {
            binding.priceLayout.error = "Fill the blank!!!"
            return false
        } else {
            binding.priceLayout.error = null
        }
        if (name.isBlank()) {
            binding.nameLayout.error = "Fill the blank!!!"
            return false
        } else {
            binding.nameLayout.error = null
        }
        if (homePhone.length != 19) {
            binding.numberLayout.error = "Fill the phone number!!!"
            return false
        } else {
            binding.numberLayout.error = null
        }
        return true
    }

    private fun getAddress() {
        if (addressModel != null) {
            try {
                val lat = addressModel.latitude
                val lon = addressModel.longitude
                var addresses: List<Address>
                var geocoder = Geocoder(requireContext(), Locale.getDefault())

                addresses = geocoder.getFromLocation(
                    lat,
                    lon,
                    1
                ) // Here 1 represent max location result to returned, by documents it recommended 1 to 5

                val city = addresses[0].locality
                val state = addresses[0].adminArea

                when {
                    city != null -> {
                        binding.mapUpload.text = city.toString()
                    }
                    state != null -> {
                        binding.mapUpload.text = state.toString()
                    }
                    else -> {
                        binding.mapUpload.text = "Latitude: ${
                            lat.toString().substring(0, 5)
                        } and longitude : ${lon.toString().substring(1, 5)}"
                    }
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    private fun setUpUi() {
        advertiseType()
        homeType()
        roomCount()
        roomCondition()
        buildingFoundation()
    }

    private fun roomCondition() {
        val items = listOf("Yaxshi", "O'rtacha", "Yomon")
        val adapter =
            ArrayAdapter(requireContext(), R.layout.support_simple_spinner_dropdown_item, items)
        binding.condition.setAdapter(adapter)
    }

    private fun roomCount() {
        val items = listOf("1", "2", "3", "4", "5", "6", "7", "8")
        val adapter =
            ArrayAdapter(requireContext(), R.layout.support_simple_spinner_dropdown_item, items)
        binding.roomCount.setAdapter(adapter)
    }

    private fun homeType() {
        val items = listOf("Kvartira", "Uy", "Hovli", "Noturar joy")
        val adapter =
            ArrayAdapter(requireContext(), R.layout.support_simple_spinner_dropdown_item, items)
        binding.homeType.setAdapter(adapter)
    }

    private fun advertiseType() {
        val items = listOf("Sotuv", "Ijara")
        val adapter =
            ArrayAdapter(requireContext(), R.layout.support_simple_spinner_dropdown_item, items)
        binding.type.setAdapter(adapter)
    }

    private fun buildingFoundation() {
        val items = listOf("G'ishtli", "Panelli", "Monolitli", "Blokli", "Yog'och")
        val adapter =
            ArrayAdapter(requireContext(), R.layout.support_simple_spinner_dropdown_item, items)
        binding.foundation.setAdapter(adapter)
    }

    var imageListener = ImageListener { position, imageView ->
        imageView.setImageURI(uriList[position])
    }

    private var resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                uriList.clear()
                val images: ArrayList<Image> =
                    data?.getParcelableArrayListExtra(Constants.INTENT_EXTRA_IMAGES)!!
                var i = 0
                val l = images.size
                while (i < l) {
                    uriList.add((Uri.parse("file://${images[i].path}")))
                    imagelist.add(images[i].name.toString())
                    i++
                }
                binding.carouselView.setImageListener(imageListener)
                binding.carouselView.pageCount = uriList.size
            }
        }
}

