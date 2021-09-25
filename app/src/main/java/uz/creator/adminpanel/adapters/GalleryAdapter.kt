package uz.creator.adminpanel.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.storage.FirebaseStorage
import uz.creator.adminpanel.R
import uz.creator.adminpanel.databinding.ItemGalleryHomeBinding
import uz.creator.adminpanel.models.Advertise
import uz.creator.adminpanel.models.FilterModel
import uz.creator.adminpanel.utils.CyrillicLatinConverter
import uz.creator.adminpanel.utils.Permanent

class GalleryAdapter(val galleryClick: GalleryClick) :
    RecyclerView.Adapter<GalleryAdapter.Vh>() {

    private val storage = FirebaseStorage.getInstance()
    private val storageReference = storage.getReference("elonImages")
    private val list = ArrayList<Advertise>()

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
                binding.name.text =
                    CyrillicLatinConverter.ltc(advertise.homeType + ": " + advertise.type + " uchun.")
            } else {
                binding.name.text = advertise.homeType + ": " + advertise.type + " uchun."
            }
            binding.createdTime.text = advertise.createdTime
            binding.price.text = advertise.price.toString()

            binding.root.setOnClickListener {
                galleryClick.onGalleryClick(advertise, position)
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

    fun addData(newAdvertiseList: ArrayList<Advertise>) {
        list.addAll(newAdvertiseList)
    }

    fun filterList(totalList: ArrayList<Advertise>, filterModel: FilterModel) {
        if (filterModel != null) {
            filter(totalList, filterModel)
            notifyDataSetChanged()
        }
    }

    private fun filter(totalList: ArrayList<Advertise>, filterModel: FilterModel) {
        list.clear()
        for (i in totalList) {
            if (!filterModel.searchText.isNullOrBlank()) {
                val searchText =
                    CyrillicLatinConverter.ctl(filterModel.searchText!!.trim().lowercase())
                if (!i.homeDesc!!.lowercase().contains(searchText) && !i.address!!.lowercase()
                        .contains(searchText)
                ) {
                    continue
                }
            }
            if (!filterModel.type.isNullOrBlank() && i.type != filterModel.type) {
                continue
            }
            if (!filterModel.homeType.isNullOrBlank() && i.homeType != filterModel.homeType) {
                continue
            }
            if (!filterModel.startPrice.isNullOrBlank() && filterModel.startPrice!!.toDouble() > i.price!!.toDouble()) {
                continue
            }
            if (!filterModel.endPrice.isNullOrBlank() && filterModel.endPrice!!.toDouble() < i.price!!.toDouble()) {
                continue
            }
            if (filterModel.startRoom != null && filterModel.startRoom!! < i.roomCount!!) {
                continue
            }
            if (filterModel.endRoom != null && filterModel.endRoom!! > i.roomCount!!) {
                continue
            }
            if (filterModel.startFloor != null && filterModel.startFloor!! < i.floor!!) {
                continue
            }
            if (filterModel.endFloor != null && filterModel.endFloor!! > i.floor!!) {
                continue
            }
            if (filterModel.startTotalFloor != null && filterModel.startTotalFloor!! < i.totalFloor!!) {
                continue
            }
            if (filterModel.endTotalFloor != null && filterModel.endTotalFloor!! > i.floor!!) {
                continue
            }
            list.add(i)
        }
    }

    interface GalleryClick {

        fun onGalleryClick(advertise: Advertise, position: Int)

    }
}