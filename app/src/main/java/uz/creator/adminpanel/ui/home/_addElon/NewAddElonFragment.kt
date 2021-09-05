package uz.creator.adminpanel.ui.home._addElon

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.location.Address
import android.location.Geocoder
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.darsh.multipleimageselect.activities.AlbumSelectActivity
import com.darsh.multipleimageselect.helpers.Constants
import com.darsh.multipleimageselect.models.Image
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.storage.FirebaseStorage
import com.synnapps.carouselview.ImageListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import uz.creator.adminpanel.R
import uz.creator.adminpanel.databinding.FragmentNewAddElonBinding
import uz.creator.adminpanel.adapters.CheckBoxAdapter
import uz.creator.adminpanel.models.Advertise
import uz.creator.adminpanel.ui.home._addElon.model.AddressModel
import uz.creator.adminpanel.models.CheckBoxModel
import uz.creator.adminpanel.utils.Permanent
import uz.creator.adminpanel.utils.snackBar
import java.util.*
import kotlin.collections.ArrayList
import java.io.IOException
import java.text.SimpleDateFormat


class NewAddElonFragment : Fragment(), CheckBoxAdapter.OnCheckBoxListener {

    private var _binding: FragmentNewAddElonBinding? = null
    private val binding get() = _binding!!
    lateinit var adapter: CheckBoxAdapter
    val urilist = ArrayList<Uri>()
    private var selectedImageUri: Uri? = null
    var imagelist = ArrayList<String>()
    private lateinit var addressModel: AddressModel
    private lateinit var firebaseFirestore: FirebaseFirestore
    private lateinit var storage: FirebaseStorage
    private lateinit var dialog: androidx.appcompat.app.AlertDialog
    var list = ArrayList<CheckBoxModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentNewAddElonBinding.inflate(layoutInflater)
        firebaseFirestore = FirebaseFirestore.getInstance()
        storage = FirebaseStorage.getInstance()
        ELonturi()
        UyTuri()
        XonaSonni()
        BinoHolati()
        SetupUI()
        CheckBoxList()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.camera.setOnClickListener {
            val intent = Intent(activity, AlbumSelectActivity::class.java)
            intent.putExtra(Constants.INTENT_EXTRA_LIMIT, 10)
            startActivityForResult(intent, Constants.REQUEST_CODE)
        }

        binding.btnuploadData.setOnClickListener {
            if (checkEmpty()) {
                val llPadding = 30
                val ll = LinearLayout(context)
                ll.orientation = LinearLayout.HORIZONTAL
                ll.setPadding(llPadding, llPadding, llPadding, llPadding)
                ll.gravity = Gravity.CENTER
                var llParam = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                llParam.gravity = Gravity.CENTER
                ll.layoutParams = llParam
                val progressBar = ProgressBar(context)
                progressBar.isIndeterminate = true
                progressBar.setPadding(0, 0, llPadding, 0)
                progressBar.layoutParams = llParam
                llParam = LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
                llParam.gravity = Gravity.CENTER
                val tvText = TextView(context)
                tvText.text = "Loading ..."
                tvText.setTextColor(Color.parseColor("#000000"))
                tvText.textSize = 20f
                tvText.layoutParams = llParam
                ll.addView(progressBar)
                ll.addView(tvText)
                val builder: androidx.appcompat.app.AlertDialog.Builder =
                    androidx.appcompat.app.AlertDialog.Builder(requireContext())
                builder.setCancelable(false)
                builder.setView(ll)
                dialog = builder.create()
                dialog.show()
                val type = binding.autoComplete.text.toString()
                val propertyType = binding.hometype.text.toString()
                val condition = binding.binoholati.text.toString()
                val geoPoint = GeoPoint(addressModel.latitude, addressModel.longitude)
                val roomCount = binding.roomcount.text.toString().toInt()
                val checkBoxList: List<CheckBoxModel> = adapter.list
                val simpleDateFormat = SimpleDateFormat("dd-MM-yyyy HH:MM:SS")
                val currentDT: String = simpleDateFormat.format(Date())

                loadImages(currentDT)

                var advertise: Advertise = Advertise(
                    type,
                    "",
                    "",
                    true,
                    condition,
                    propertyType,
                    Permanent.phoneNumber,
                    geoPoint,
                    roomCount,
                    0.0,
                    1,
                    4,
                    "G'isht",
                    currentDT,
                    checkBoxList,
                )
                firebaseFirestore.collection("elonlar")
                    .document("${Permanent.phoneNumber}${advertise.createdTime}")
                    .set(advertise)
                    .addOnSuccessListener {
                        dialog.dismiss()
                        requireView().snackBar("Uploaded successfully!!!")
                        findNavController().popBackStack()
                    }.addOnCanceledListener {
                        dialog.dismiss()
                        requireView().snackBar("Canceled!!!")
                        findNavController().popBackStack()
                    }.addOnFailureListener {
                        dialog.dismiss()
                        requireView().snackBar(it.message.toString())
                        findNavController().popBackStack()
                    }
            } else {
                requireView().snackBar("Select the fields!!!")
            }
        }

        binding.mapupload.setOnClickListener {
            findNavController().navigate(R.id.action_newAddElonFragment_to_mapFragment)
        }

        findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<AddressModel>("address")
            ?.observe(viewLifecycleOwner) {
                Toast.makeText(requireContext(), it.toString() + "", Toast.LENGTH_SHORT).show()
                addressModel = it
                getAddress()
            }
    }

    private fun loadImages(currentDT: String) {
        for (i in urilist.indices) {
            storage.getReference("elonImages")
                .child("${Permanent.phoneNumber}${currentDT}")
                .child("${i}.jpg")
                .putFile(urilist[i])
        }
    }

    private fun checkEmpty(): Boolean {
        val type = binding.autoComplete.text.toString()
        val propertyType = binding.hometype.text.toString()
        val condition = binding.binoholati.text.toString()
        val roomCount = binding.roomcount.text.toString()
        when {
            type.isBlank() -> {
                requireView().snackBar("Select the fields!!!")
                return false
            }
            propertyType.isBlank() -> {
                requireView().snackBar("Select the fields!!!")
                return false
            }
            condition.isBlank() -> {
                requireView().snackBar("Select the fields!!!")
                return false
            }
            roomCount.isBlank() -> {
                requireView().snackBar("Select the fields!!!")
                return false
            }
            !this::addressModel.isInitialized -> {
                requireView().snackBar("Select a location on the map!!!")
                return false
            }
        }
        return true
    }

    private fun getAddress() {
        if (addressModel != null) {
            try {
                val lat = addressModel.latitude
                val lon = addressModel.longitude

                var geocoder: Geocoder
                var addresses: List<Address>
                geocoder = Geocoder(requireContext(), Locale.getDefault())

                addresses = geocoder.getFromLocation(
                    lat,
                    lon,
                    1
                ) // Here 1 represent max location result to returned, by documents it recommended 1 to 5


                val address =
                    addresses[0].getAddressLine(0) // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()

                val city = addresses[0].locality
                val state = addresses[0].adminArea

                if (city != null) {
                    binding.mapupload.text = city.toString()
                } else if (state != null) {
                    binding.mapupload.text = state.toString()
                } else {
                    binding.mapupload.text = "Latitude: ${
                        lat.toString().substring(0, 5)
                    } and longitude : ${lon.toString().substring(1, 5)}"
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    private fun SetupUI() {
        adapter = CheckBoxAdapter(requireActivity(), this)
        binding.apply {
            recyclerView.setHasFixedSize(true)
            recyclerView.layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            recyclerView.adapter = adapter
        }
    }

    private fun CheckBoxList() {
        list.add(CheckBoxModel("Bog'cha", false))
        list.add(CheckBoxModel("Kir yuvish mashinasi", false))
        list.add(CheckBoxModel("Televizor", false))
        list.add(CheckBoxModel("Muzlatgich", false))
        adapter.list = list
    }

    private fun BinoHolati() {
        val items = listOf("Yaxshi", "O'rtacha", "Yomon")
        val adapter =
            ArrayAdapter(requireContext(), R.layout.support_simple_spinner_dropdown_item, items)
        binding.binoholati.setAdapter(adapter)
    }

    private fun XonaSonni() {
        val items = listOf("1", "2", "3", "4", "5", "6", "7", "8")
        val adapter =
            ArrayAdapter(requireContext(), R.layout.support_simple_spinner_dropdown_item, items)
        binding.roomcount.setAdapter(adapter)
    }

    private fun UyTuri() {
        val items = listOf("Kvartira", "Uy", "Hovli")
        val adapter =
            ArrayAdapter(requireContext(), R.layout.support_simple_spinner_dropdown_item, items)
        binding.hometype.setAdapter(adapter)
    }

    private fun ELonturi() {
        val items = listOf("Sotuv", "Ijara")
        val adapter =
            ArrayAdapter(requireContext(), R.layout.support_simple_spinner_dropdown_item, items)
        (binding.textfield.editText as? AutoCompleteTextView)?.setAdapter(adapter)
    }


    override fun OnclickListener(model: CheckBoxModel, ad: Boolean) {

    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == Constants.REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null) {
            urilist.clear()
            // bannerModel.clear()
            val images: ArrayList<Image> =
                data.getParcelableArrayListExtra(Constants.INTENT_EXTRA_IMAGES)!!
            selectedImageUri = data.data
            // val stringBuffer = StringBuffer()
            var i = 0
            val l = images.size
            while (i < l) {
                urilist.add((Uri.parse("file://${images[i].path}")))
                imagelist.add(images[i].name.toString())
                i++
            }
            binding.carouselView.setImageListener(imageListener)
            binding.carouselView.pageCount = urilist.size
        }
    }

    var imageListener = ImageListener { position, imageView ->
        imageView.setImageURI(urilist[position])
    }

    override fun onResume() {
        super.onResume()

    }

}

