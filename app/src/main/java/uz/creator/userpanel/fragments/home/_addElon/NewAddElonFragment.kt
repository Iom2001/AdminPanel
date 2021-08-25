package uz.creator.userpanel.fragments.home._addElon

import android.app.Activity
import android.content.Intent
import android.location.Address
import android.location.Geocoder
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.darsh.multipleimageselect.activities.AlbumSelectActivity
import com.darsh.multipleimageselect.helpers.Constants
import com.darsh.multipleimageselect.models.Image
import com.synnapps.carouselview.ImageListener
import uz.creator.adminpanel.R
import uz.creator.adminpanel.databinding.FragmentNewAddElonBinding
import uz.creator.userpanel.fragments.home._addElon.adapter.CheckBoxAdapter
import uz.creator.userpanel.fragments.home._addElon.model.AddressModel
import uz.creator.userpanel.fragments.home._addElon.model.CheckBoxModel
import java.util.*
import kotlin.collections.ArrayList


class NewAddElonFragment : Fragment(), CheckBoxAdapter.OnCheckBoxListener {
    private var _binding: FragmentNewAddElonBinding? = null
    private val binding get() = _binding!!
    lateinit var adapter: CheckBoxAdapter
    val urilist = ArrayList<Uri>()
    private var selectedImageUri: Uri? = null
    var imagelist = ArrayList<String>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentNewAddElonBinding.inflate(layoutInflater)
        return binding.root
    }
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        ELonturi()
        UyTuri()
        XonaSonni()
        BinoHolati()
        SetupUI()
        CheckBoxList()
        binding.camera.setOnClickListener {
            val intent = Intent(activity, AlbumSelectActivity::class.java)
            intent.putExtra(Constants.INTENT_EXTRA_LIMIT, 10)
            startActivityForResult(intent, Constants.REQUEST_CODE)
        }
        binding.btnuploadData.setOnClickListener {
            Toast.makeText(requireContext(), "Yuklandi", Toast.LENGTH_SHORT).show()
        }
        binding.mapupload.setOnClickListener {
            findNavController().navigate(R.id.action_newAddElonFragment_to_mapFragment)
        }
        ViewModelProvider(requireActivity())[shareAddressModel::class.java].data.observe(viewLifecycleOwner,
            Observer {
              //  Toast.makeText(requireContext(), it.toString()+"", Toast.LENGTH_SHORT).show()
                //  getAddres(it)

            })

    }

    private fun getAddres(it: AddressModel?) {
        if (it != null) {
            val lat = it.latitude
            val lon = it.longitude

            val geocoder = Geocoder(requireContext(), Locale.getDefault())
            val addresses: List<Address> = geocoder.getFromLocation(lat, lon, 1)
            val address = addresses[0].getAddressLine(0)
            val address2 = addresses[0].getAddressLine(1)
            val city = addresses[0].locality
            val state = addresses[0].adminArea
            val country = addresses[0].countryName
            val postalCode = addresses[0].postalCode
            val knownName = addresses[0].featureName
            binding.mapupload.text =city.toString()
            val message =
                "Emergency situation. Call for help. My location is: " + address + "." + "http://maps.google.com/maps?saddr=" + lat + "," + lon
            Log.e("sada",message.toString())
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
        var list = ArrayList<CheckBoxModel>()
        list.add(CheckBoxModel(0, "Bog'cha", false))
        list.add(CheckBoxModel(0, "Kir yuvish mashinasi", false))
        list.add(CheckBoxModel(0, "Televizor", false))
        list.add(CheckBoxModel(0, "Muzlatgich", false))
        adapter.list = list
    }
    private fun BinoHolati() {
        val items = listOf("Yaxshi", "Yomon")
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
                urilist.add((Uri.parse(images[i].path)))
                imagelist.add(images[i].name.toString())
                var uri = Uri.parse(images[i].path)
                i++
            }
            binding.carouselView.setImageListener(imageListener)
            binding.carouselView.pageCount = urilist.size
        }
    }

    var imageListener = ImageListener { position, imageView ->
        imageView.setImageURI(urilist[position])
    }

}

