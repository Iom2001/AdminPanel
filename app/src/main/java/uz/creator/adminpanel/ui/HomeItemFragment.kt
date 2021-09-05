package uz.creator.adminpanel.ui

import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ListResult
import com.google.firebase.storage.StorageReference
import uz.creator.adminpanel.R
import uz.creator.adminpanel.databinding.FragmentHomeItemBinding
import uz.creator.adminpanel.models.Advertise
import uz.creator.adminpanel.utils.Permanent
import java.util.zip.Inflater

class HomeItemFragment : Fragment() {

    private lateinit var binding: FragmentHomeItemBinding
    private var date = ""
    private lateinit var firestore: FirebaseFirestore
    private lateinit var storage: FirebaseStorage
    private var imageUriList = ArrayList<Uri>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            it.getString("date")?.let {
                date = it
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeItemBinding.inflate(layoutInflater, container, false)
        firestore = FirebaseFirestore.getInstance()
        storage = FirebaseStorage.getInstance()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getAdvertise()
        getImageUriList(0)
    }

    private fun getImageUriList(i: Int) {
        val listRef = storage.reference.child("elonImages/${Permanent.phoneNumber}${date}/${i}.jpg")
        listRef.downloadUrl.addOnSuccessListener {
            imageUriList.add(it)
            getImageUriList(1 + i)
        }.addOnFailureListener {
            imageUriList
        }
    }

    private fun getAdvertise() {
        firestore.collection("elonlar").document("${Permanent.phoneNumber}${date}").get()
            .addOnSuccessListener {
                val advertise = it.toObject(Advertise::class.java)
            }.addOnFailureListener {

            }
    }


    companion object {

        @JvmStatic
        fun newInstance() = HomeItemFragment()
    }
}