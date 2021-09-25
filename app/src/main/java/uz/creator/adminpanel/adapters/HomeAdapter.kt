package uz.creator.adminpanel.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.storage.FirebaseStorage
import uz.creator.adminpanel.R
import uz.creator.adminpanel.databinding.ItemGalleryHomeBinding
import uz.creator.adminpanel.models.Advertise
import uz.creator.adminpanel.utils.CyrillicLatinConverter
import uz.creator.adminpanel.utils.Permanent

class HomeAdapter(val list: List<Advertise>, val homeClick: HomeClick) :
    RecyclerView.Adapter<HomeAdapter.Vh>() {

    private val storage = FirebaseStorage.getInstance()
    private val storageReference = storage.getReference("elonImages")

    inner class Vh(var binding: ItemGalleryHomeBinding) : RecyclerView.ViewHolder(binding.root) {

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
            if (Permanent.isKiril) {
                binding.name.text = advertise.name?.let { CyrillicLatinConverter.ltc(it) }
            } else {
                binding.name.text = advertise.name
            }
            binding.createdTime.text = advertise.createdTime
            binding.price.text = advertise.price.toString()

            binding.root.setOnClickListener {
                homeClick.onHomeClick(advertise, position)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Vh {
        return Vh(
            ItemGalleryHomeBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: Vh, position: Int) {
        holder.onBind(list[position], position)
    }

    override fun getItemCount(): Int = list.size

    interface HomeClick {

        fun onHomeClick(advertise: Advertise, position: Int)

    }
}