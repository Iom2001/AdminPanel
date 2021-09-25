package uz.creator.adminpanel.adapters.viewholder

import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.storage.FirebaseStorage
import uz.creator.adminpanel.R
import uz.creator.adminpanel.databinding.ItemGalleryHomeBinding
import uz.creator.adminpanel.models.Advertise

class Vh(val binding: ItemGalleryHomeBinding, private val galleryClick: GalleryClick) :
    RecyclerView.ViewHolder(binding.root) {

    private val storageReference = FirebaseStorage.getInstance().getReference("elonImages")

    fun onBind(advertise: Advertise, position: Int) {
        storageReference.child("${advertise.phoneNumber}${advertise.createdTime}")
            .child("0.jpg").downloadUrl.addOnSuccessListener {
                Glide.with(binding.root.context).load(it).error(R.drawable.errorplaceholder)
                    .placeholder(R.drawable.home_placeholder).into(binding.homeImage)
            }.addOnFailureListener {
                Glide.with(binding.root.context).load(R.drawable.home_placeholder)
                    .error(R.drawable.errorplaceholder)
                    .placeholder(R.drawable.home_placeholder).into(binding.homeImage)
            }
        binding.name.text = advertise.name
        binding.createdTime.text = advertise.createdTime
        binding.price.text = advertise.price.toString()

        binding.root.setOnClickListener {
            galleryClick.onGalleryClick(advertise, position)
        }

    }

    interface GalleryClick {
        fun onGalleryClick(advertise: Advertise, position: Int)
    }
}
