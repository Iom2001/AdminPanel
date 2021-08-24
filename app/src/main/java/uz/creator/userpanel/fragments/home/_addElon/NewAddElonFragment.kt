package uz.creator.userpanel.fragments.home._addElon

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.darsh.multipleimageselect.activities.AlbumSelectActivity
import com.darsh.multipleimageselect.helpers.Constants
import com.darsh.multipleimageselect.models.Image
import com.synnapps.carouselview.ImageListener
import uz.creator.adminpanel.R
import uz.creator.adminpanel.databinding.FragmentNewAddElonBinding
import uz.creator.userpanel.fragments.home._addElon.adapter.CheckBoxAdapter
import uz.creator.userpanel.fragments.home._addElon.model.CheckBoxModel

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

