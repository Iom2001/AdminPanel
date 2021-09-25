package uz.creator.adminpanel.ui.elon

import android.app.AlertDialog
import android.location.Address
import android.location.Geocoder
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.storage.FirebaseStorage
import uz.creator.adminpanel.R
import uz.creator.adminpanel.databinding.FragmentAddElonBinding
import uz.creator.adminpanel.models.Elon
import uz.creator.adminpanel.ui.home._addHome.ShareAddressModel
import uz.creator.adminpanel.ui.home.model.AddressModel
import uz.creator.adminpanel.utils.MyDialog
import uz.creator.adminpanel.utils.Permanent
import uz.creator.adminpanel.utils.snackBar
import java.io.IOException
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList

class AddElonFragment : Fragment() {

    private lateinit var _binding: FragmentAddElonBinding
    private val binding get() = _binding!!
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
        _binding = FragmentAddElonBinding.inflate(
            inflater,
            container,
            false
        )

        firebaseFirestore = FirebaseFirestore.getInstance()
        storage = FirebaseStorage.getInstance()
        setUpUi()

        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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
//            upload the home
            if (checkEmpty()) {
                myDialog.showDialog()
                val type = binding.type.text.toString()
                val homeType = binding.homeType.text.toString()
                val condition = binding.condition.text.toString()
                val roomCount = binding.roomCount.text.toString()
                val foundation = binding.foundation.text.toString()
                val totalFloor = binding.totalFloor.text.toString()
                val floor = binding.floor.text.toString()
                val desc = binding.homeDescEditText.text.toString()
                val price = binding.priceEditText.text.toString()
                var geoPoint = GeoPoint(addressModel.latitude, addressModel.longitude)

                val currentDT = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    val current = LocalDateTime.now()
                    val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:MM:SS")
                    current.format(formatter)
                } else {
                    val simpleDateFormat = SimpleDateFormat("dd-MM-yyyy HH:MM:SS")
                    simpleDateFormat.format(Date())
                }

                val haveList = ArrayList<Boolean>()
                for (i in checkedItemsHave) {
                    haveList.add(i)
                }
                val nearList = ArrayList<Boolean>()
                for (i in checkedItemsNear) {
                    nearList.add(i)
                }
                val elon = Elon(
                    type,
                    desc,
                    price,
                    condition,
                    homeType,
                    Permanent.phoneNumber,
                    geoPoint,
                    roomCount,
                    floor,
                    totalFloor,
                    foundation,
                    currentDT,
                    haveList,
                    nearList,
                )
                firebaseFirestore.collection("elonforsearch")
                    .document("${Permanent.phoneNumber}${elon.createdTime}")
                    .set(elon)
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
                    R.id.mapFragment,
                    bundle
                )
            } else {
                findNavController().navigate(R.id.mapFragment)
            }
        }

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

    private fun checkEmpty(): Boolean {
//        check the home fields the empty
        val type = binding.type.text.toString()
        val homeType = binding.homeType.text.toString()
        val price = binding.priceEditText.text.toString()
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
        if (!this::addressModel.isInitialized) {
            requireView().snackBar("Select a location on the map!!!")
            return false
        }
        if (price.isBlank()) {
            binding.priceLayout.error = "Fill the blank!!!"
            return false
        } else {
            binding.priceLayout.error = null
        }
        return true
    }

    private fun setUpUi() {
//        set the elements to ui
        elonType()
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

    private fun elonType() {
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

}