package uz.creator.adminpanel.ui.home._editHome

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.activity.result.contract.ActivityResultContracts
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
import uz.creator.adminpanel.databinding.FragmentEditHomeBinding
import uz.creator.adminpanel.models.Advertise
import uz.creator.adminpanel.ui.home._addHome.ShareAddressModel
import uz.creator.adminpanel.ui.home.model.AddressModel
import uz.creator.adminpanel.utils.CyrillicLatinConverter
import uz.creator.adminpanel.utils.MyDialog
import uz.creator.adminpanel.utils.Permanent
import uz.creator.adminpanel.utils.snackBar
import kotlin.collections.ArrayList

class EditHomeFragment : Fragment() {

    private var _binding: FragmentEditHomeBinding? = null
    private val binding get() = _binding!!
    private var advertise: Advertise? = null
    private val uriList = ArrayList<Uri>()
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
    private var date = ""
    private var isImageListChange = false
    private var lastImageListCount = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            date = it.getString("date").toString()
        }
        firebaseFirestore = FirebaseFirestore.getInstance()
        storage = FirebaseStorage.getInstance()
        myDialog = MyDialog(requireContext())
    }

    private fun getAdvertise(date: String) {
        firebaseFirestore.collection("elonlar").document(Permanent.phoneNumber + date).get()
            .addOnSuccessListener {
                advertise = it.toObject(Advertise::class.java)!!
                for (i in advertise?.checkedItemsHave!!.indices) {
                    checkedItemsHave[i] = advertise?.checkedItemsHave!![i]
                }
                for (i in advertise?.checkedItemsNear!!.indices) {
                    checkedItemsNear[i] = advertise?.checkedItemsNear!![i]
                }
                addressModel =
                    AddressModel(advertise?.geoPoint!!.latitude, advertise?.geoPoint!!.longitude)
                setUpUi()
                myDialog.dismissDialog()
            }.addOnFailureListener {
                myDialog.dismissDialog()
                findNavController().popBackStack()
            }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (advertise == null) {
            binding.carouselView.setImageListener { _, imageView ->
                Glide.with(requireContext()).load(R.drawable.home_placeholder).into(imageView)
            }
            binding.carouselView.pageCount = 1
            myDialog.showDialog()
            getImageUriList(0)
            getAdvertise(date)
        } else {
            binding.carouselView.setImageListener(imageListener)
            binding.carouselView.pageCount = uriList.size
        }
        binding.camera.setOnClickListener {
//            get images
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
//                upload the home data
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

                loadImages(advertise?.createdTime!!)
                val haveList = ArrayList<Boolean>()
                for (i in checkedItemsHave) {
                    haveList.add(i)
                }
                val nearList = ArrayList<Boolean>()
                for (i in checkedItemsNear) {
                    nearList.add(i)
                }
                advertise?.type = type
                advertise?.name = name
                advertise?.homeDesc = desc
                advertise?.address = address
                advertise?.price = price.toInt()
                advertise?.condition = condition
                advertise?.homeType = homeType
                advertise?.homePhoneNumber = homePhone
                advertise?.geoPoint = geoPoint
                advertise?.roomCount = roomCount.toInt()
                advertise?.floor = floor.toInt()
                advertise?.totalFloor = totalFloor.toInt()
                advertise?.foundation = foundation
                advertise?.checkedItemsHave = haveList
                advertise?.checkedItemsNear = nearList
                firebaseFirestore.collection("elonlar")
                    .document("${Permanent.phoneNumber}${advertise?.createdTime}")
                    .set(advertise!!)
                    .addOnSuccessListener {
                        myDialog.dismissDialog()
                        requireView().snackBar("Updated successfully!!!")
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
            val bundle = Bundle()
            bundle.putSerializable("addressModel", addressModel)
            findNavController().navigate(
                R.id.mapFragment,
                bundle
            )
        }

//        Listen the address
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
                near += if (near.isEmpty()) {
                    listNear[i]
                } else {
                    "," + listNear[i]
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
                have += if (have.isEmpty()) {
                    listHave[i]
                } else {
                    "," + listHave[i]
                }
            }
        }
        binding.homeHaveEditText.setText(have)
    }

    private fun loadImages(currentDT: String) {
//        get images uri from storage
        for (i in uriList.indices) {
            storage.getReference("elonImages")
                .child("${advertise?.phoneNumber}${currentDT}")
                .child("${i}.jpg")
                .putFile(uriList[i])
        }
        if (isImageListChange && lastImageListCount > uriList.size) {
            for (i in uriList.size until lastImageListCount) {
                storage.getReference("elonImages")
                    .child("${Permanent.phoneNumber}${currentDT}")
                    .child("${i}.jpg").delete()
            }
        }
    }

    private fun checkEmpty(): Boolean {
//        check the home fields
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
            binding.foundationLayout.error = "Fill the phone number!!!"
            return false
        } else {
            binding.foundationLayout.error = null
        }
        return true
    }

    private fun setUpUi() {
//        set up ui
        advertiseType()
        homeType()
        roomCount()
        roomCondition()
        buildingFoundation()
        setUpAnotherUi()
        setHaveUi()
        setNearUi()
    }

    private fun setUpAnotherUi() {
        if (Permanent.isKiril) {
            binding.floor.setText(advertise?.floor.toString())
            binding.totalFloor.setText(advertise?.totalFloor.toString())
            binding.homeDescEditText.setText(advertise?.homeDesc?.let {
                CyrillicLatinConverter.ltc(
                    it
                )
            })
            binding.addressEditText.setText(advertise?.address?.let { CyrillicLatinConverter.ltc(it) })
            binding.priceEditText.setText(advertise?.price.toString())
            binding.usernameEditText.setText(advertise?.name?.let { CyrillicLatinConverter.ltc(it) })
            binding.numberEditText.setText(advertise?.homePhoneNumber?.substring(4))
        } else {
            binding.floor.setText(advertise?.floor.toString())
            binding.totalFloor.setText(advertise?.totalFloor.toString())
            binding.homeDescEditText.setText(advertise?.homeDesc)
            binding.addressEditText.setText(advertise?.address)
            binding.priceEditText.setText(advertise?.price.toString())
            binding.usernameEditText.setText(advertise?.name)
            binding.numberEditText.setText(advertise?.homePhoneNumber?.substring(4))
        }
    }

    private fun roomCondition() {
        binding.condition.setText(advertise?.condition)
        val items = listOf("Yaxshi", "O'rtacha", "Yomon")
        val adapter =
            ArrayAdapter(requireContext(), R.layout.support_simple_spinner_dropdown_item, items)
        binding.condition.setAdapter(adapter)
    }

    private fun roomCount() {
        binding.roomCount.setText(advertise?.roomCount.toString())
        val items = listOf("1", "2", "3", "4", "5", "6", "7", "8")
        val adapter =
            ArrayAdapter(requireContext(), R.layout.support_simple_spinner_dropdown_item, items)
        binding.roomCount.setAdapter(adapter)
    }

    private fun homeType() {
        binding.homeType.setText(advertise?.homeType)
        val items = listOf("Kvartira", "Uy", "Hovli", "Noturar joy")
        val adapter =
            ArrayAdapter(requireContext(), R.layout.support_simple_spinner_dropdown_item, items)
        binding.homeType.setAdapter(adapter)
    }

    private fun advertiseType() {
        binding.type.setText(advertise?.type)
        val items = listOf("Sotuv", "Ijara")
        val adapter =
            ArrayAdapter(requireContext(), R.layout.support_simple_spinner_dropdown_item, items)
        binding.type.setAdapter(adapter)
    }

    private fun buildingFoundation() {
        binding.foundation.setText(advertise?.foundation)
        val items = listOf("G'ishtli", "Panelli", "Monolitli", "Blokli", "Yog'och")
        val adapter =
            ArrayAdapter(requireContext(), R.layout.support_simple_spinner_dropdown_item, items)
        binding.foundation.setAdapter(adapter)
    }

    private fun getImageUriList(i: Int) {
        val listRef = storage.reference.child("elonImages/${Permanent.phoneNumber}${date}/${i}.jpg")
        listRef.downloadUrl.addOnSuccessListener {
            uriList.add(it)
            getImageUriList(1 + i)
        }.addOnFailureListener {
            binding.carouselView.setImageListener(imageListener)
            binding.carouselView.pageCount = uriList.size
            lastImageListCount = uriList.size
        }
    }

    private val imageListener = ImageListener { position, imageView ->
        Glide.with(requireContext()).load(uriList[position])
            .placeholder(R.drawable.home_placeholder)
            .error(R.drawable.errorplaceholder).into(imageView)
    }

    private var resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                isImageListChange = true
                val data: Intent? = result.data
                uriList.clear()
                val images: ArrayList<Image> =
                    data?.getParcelableArrayListExtra(Constants.INTENT_EXTRA_IMAGES)!!
                var i = 0
                val l = images.size
                while (i < l) {
                    uriList.add((Uri.parse("file://${images[i].path}")))
                    i++
                }
                binding.carouselView.setImageListener(imageListener)
                binding.carouselView.pageCount = uriList.size
            }
        }
}