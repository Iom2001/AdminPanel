package uz.creator.adminpanel.ui

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.location.Address
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.synnapps.carouselview.ImageListener
import uz.creator.adminpanel.R
import uz.creator.adminpanel.databinding.FragmentHomeItemBinding
import uz.creator.adminpanel.models.Advertise
import uz.creator.adminpanel.utils.MyDialog
import uz.creator.adminpanel.utils.Permanent
import android.location.Geocoder
import android.view.*
import androidx.appcompat.app.AlertDialog
import androidx.navigation.fragment.findNavController
import uz.creator.adminpanel.databinding.CheckDialogBinding
import uz.creator.adminpanel.utils.CyrillicLatinConverter
import uz.creator.adminpanel.utils.snackBar
import java.lang.Exception
import java.util.*
import kotlin.collections.ArrayList

class HomeItemFragment : Fragment() {

    private lateinit var binding: FragmentHomeItemBinding
    private var date = ""
    private lateinit var firestore: FirebaseFirestore
    private lateinit var storage: FirebaseStorage
    private var imageUriList = ArrayList<Uri>()
    private lateinit var advertise: Advertise
    private var phoneNumber: String = ""
    private lateinit var myDialog: MyDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
//            get home added date and user phone number
            it.getString("date")?.let { d ->
                date = d
            }
            it.getString("phoneNumber")?.let { p ->
                phoneNumber = p
            }
        }
        myDialog = MyDialog(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeItemBinding.inflate(layoutInflater, container, false)
//        Put default image
        binding.carouselView.setImageListener(ImageListener { _, imageView ->
            Glide.with(requireContext()).load(R.drawable.home_placeholder).into(imageView)
        })
        binding.carouselView.pageCount = 1
        firestore = FirebaseFirestore.getInstance()
        storage = FirebaseStorage.getInstance()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        get home from firebase firesote
        getAdvertise()

//        get image from storage
        getImageUriList(0)

        binding.mapImage.setOnClickListener {
//            go google map app
            if (this::advertise.isInitialized) {
//                go to the map
                val gmmIntentUri =
                    Uri.parse("geo:0,0?q=${advertise.geoPoint?.latitude},${advertise.geoPoint?.longitude}(Google+Uzbekistan)")
                val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
                mapIntent.setPackage("com.google.android.apps.maps")
                startActivity(mapIntent)
            }
        }

        binding.callTv.setOnClickListener {
//            call the client
            call()
        }

        binding.disActiveBtn.setOnClickListener {
//            disactive the home
            val alertDialog = AlertDialog.Builder(requireContext())
            val dialog = alertDialog!!.create()
            dialog.setCancelable(false)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT));
            val dialogView: View = layoutInflater.inflate(
                R.layout.check_dialog,
                null,
                false
            )
            dialog.setView(dialogView)
            val bindDialog = CheckDialogBinding.bind(dialogView)
            bindDialog.title.text = "Bu uyni activlikdan to'xtatasizmi?"
            bindDialog.okBtn.setOnClickListener {
                dialog.dismiss()
                if (this::advertise.isInitialized) {
                    myDialog.showDialog()
                    firestore.collection("elonlar")
                        .document("${advertise.phoneNumber}${advertise.createdTime}")
                        .update("active", false)
                        .addOnSuccessListener {
                            binding.disActiveBtn.visibility = View.GONE
                            myDialog.dismissDialog()
                        }
                        .addOnFailureListener { e ->
                            myDialog.dismissDialog()
                        }
                }
            }
            bindDialog.cancelBtn.setOnClickListener {
                dialog.dismiss()
            }
            dialog.show()
        }

        binding.deleteBtn.setOnClickListener {
//            delete the home
            val alertDialog = AlertDialog.Builder(requireContext())
            val dialog = alertDialog!!.create()
            dialog.setCancelable(false)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT));
            val dialogView: View = layoutInflater.inflate(
                R.layout.check_dialog,
                null,
                false
            )
            dialog.setView(dialogView)
            val bindDialog = CheckDialogBinding.bind(dialogView)
            bindDialog.title.text = "Bu uyni o’chirasizmi?"
            bindDialog.okBtn.setOnClickListener {
                dialog.dismiss()
                if (this::advertise.isInitialized) {
                    myDialog.showDialog()
                    firestore.collection("elonlar")
                        .document("${advertise.phoneNumber}${advertise.createdTime}")
                        .delete()
                        .addOnSuccessListener {
                            myDialog.dismissDialog()
                            requireView().snackBar("Deleted Successfully")
                            findNavController().popBackStack()
                        }
                        .addOnFailureListener { e ->
                            myDialog.dismissDialog()
                            requireView().snackBar("Failed!!! ${e.message}")
                        }
                }
            }
            bindDialog.cancelBtn.setOnClickListener {
                dialog.dismiss()
            }
            dialog.show()
        }
    }

    private fun getImageUriList(i: Int) {
//        get image uri list from storage
        val listRef = storage.reference.child("elonImages/${phoneNumber}${date}/${i}.jpg")
        listRef.downloadUrl.addOnSuccessListener {
            imageUriList.add(it)
            getImageUriList(1 + i)
        }.addOnFailureListener {
            binding.carouselView.setImageListener(imageListener)
            binding.carouselView.pageCount = imageUriList.size
        }
    }

    private fun getAdvertise() {
//        get advertise
        myDialog.showDialog()
        firestore.collection("elonlar").document("${phoneNumber}${date}").get()
            .addOnSuccessListener {
                it.toObject(Advertise::class.java)?.let { advert ->
                    advertise = advert
                    setView()
                }
                if (Permanent.isAdmin) {
                    binding.buttonLayout.visibility = View.VISIBLE
                    if (!advertise.isActive!!) {
                        binding.disActiveBtn.visibility = View.GONE
                    }
                }
                myDialog.dismissDialog()
            }.addOnFailureListener {
                myDialog.dismissDialog()
            }
    }

    private fun call() {
        val dialIntent = Intent(Intent.ACTION_DIAL)
        val phone =
            advertise.homePhoneNumber?.substring(0, 4) + advertise.homePhoneNumber?.substring(
                6,
                8
            ) + advertise.homePhoneNumber?.substring(10, 14) + advertise.homePhoneNumber?.substring(
                14,
                17
            ) + advertise.homePhoneNumber?.substring(17)
        dialIntent.data = Uri.parse("tel:$phone")
        startActivity(dialIntent)
    }

    private val imageListener = ImageListener { position, imageView ->
        Glide.with(requireContext()).load(imageUriList[position])
            .placeholder(R.drawable.home_placeholder)
            .error(R.drawable.errorplaceholder).into(imageView)
    }

    private fun setView() {
        binding.createdTimeTv.text = advertise.createdTime
        binding.userPhoneNumber.text = advertise.phoneNumber
        binding.priceTv.text = "${advertise.price.toString()} $"
        if (Permanent.isKiril) {
            var type = advertise.homeType
            type = if (advertise.type == "Sotuv") {
                CyrillicLatinConverter.ltc("$type sotiladi")
            } else {
                CyrillicLatinConverter.ltc("$type ijaraga beriladi")
            }
            binding.typeTv.text = type
            binding.roomCountTv.text =
                CyrillicLatinConverter.ltc("Xonalar soni: ${advertise.roomCount}")
            binding.conditionTv.text = CyrillicLatinConverter.ltc("Xolati: ${advertise.condition}")
            binding.totalFloorTv.text =
                CyrillicLatinConverter.ltc("Bino qavati: ${advertise.totalFloor}")
            binding.floorTv.text = CyrillicLatinConverter.ltc("Uyning qavati: ${advertise.floor}")
            binding.foundationTv.text =
                CyrillicLatinConverter.ltc("Bino qurilish turi: ${advertise.foundation}")
            binding.descTv.text = advertise.homeDesc?.let { CyrillicLatinConverter.ltc(it) }
            binding.phoneNumberUsernameTv.text =
                CyrillicLatinConverter.ltc("${advertise.homePhoneNumber}\n${advertise.name}")
            binding.addressTv.text = advertise.address?.let { CyrillicLatinConverter.ltc(it) }
        } else {
            var type = advertise.homeType
            type = if (advertise.type == "Sotuv") {
                "$type sotiladi"
            } else {
                "$type ijaraga beriladi"
            }
            binding.typeTv.text = type
            binding.roomCountTv.text = "Xonalar soni: ${advertise.roomCount}"
            binding.conditionTv.text = "Xolati: ${advertise.condition}"
            binding.totalFloorTv.text = "Bino qavati: ${advertise.totalFloor}"
            binding.floorTv.text = "Uyning qavati: ${advertise.floor}"
            binding.foundationTv.text = "Bino qurilish turi: ${advertise.foundation}"
            binding.descTv.text = advertise.homeDesc
            binding.phoneNumberUsernameTv.text = "${advertise.homePhoneNumber}\n${advertise.name}"
            binding.addressTv.text = advertise.address
        }
        setHave()
        setNear()
        getRegion()
    }

    private fun setHave() {
        var have = "Uyda bor: "
        val listHave = arrayOf(
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
        for (i in listHave.indices) {
            if (advertise.checkedItemsHave!![i]) {
                have += if (have.length <= 10) {
                    listHave[i]
                } else {
                    "," + listHave[i]
                }
            }
        }
        if (have.length > 10) {
            if (Permanent.isKiril)
                binding.haveTv.text = CyrillicLatinConverter.ltc(have)
            else {
                binding.haveTv.text = have
            }
        }
    }

    private fun setNear() {
        val listNear = arrayOf(
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
        var near = "Uyga yaqin: "
        for (i in listNear.indices) {
            if (advertise.checkedItemsNear!![i]) {
                near += if (near.length <= 12) {
                    "${listNear[i]}"
                } else {
                    ",${listNear[i]}"
                }
            }
        }
        if (near.length > 12) {
            if (Permanent.isKiril) {
                binding.nearTv.text = CyrillicLatinConverter.ltc(near)
            } else {
                binding.nearTv.text = near
            }
        }
    }

    private fun getRegion() {

//        get location name

        val geocoder = Geocoder(requireContext(), Locale.getDefault())
        val addresses: List<Address> =
            geocoder.getFromLocation(
                advertise.geoPoint!!.latitude,
                advertise.geoPoint!!.longitude,
                1
            )
        var cityName: String? = ""
        var stateName: String? = ""
        var countryName: String? = ""
        try {
            cityName = addresses[0].getAddressLine(0)
            stateName = addresses[0].getAddressLine(1)
            countryName = addresses[0].getAddressLine(2)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        if (!cityName.isNullOrBlank()) {
            binding.addressRegionTv.text = cityName
        } else if (!stateName.isNullOrBlank()) {
            binding.addressRegionTv.text = stateName
        } else if (!countryName.isNullOrBlank()) {
            binding.addressRegionTv.text = countryName
        }
    }
}