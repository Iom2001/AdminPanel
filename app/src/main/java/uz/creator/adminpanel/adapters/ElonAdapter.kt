package uz.creator.adminpanel.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import uz.creator.adminpanel.R
import uz.creator.adminpanel.databinding.ItemElonBinding
import uz.creator.adminpanel.models.Advertise

class ElonAdapter(var list: List<Advertise>, val elonClick: ElonClick) :
    RecyclerView.Adapter<ElonAdapter.Vh>() {

    private val storage = FirebaseStorage.getInstance()
    private val storageReference = storage.getReference("elonImages")

    inner class Vh(var binding: ItemElonBinding) : RecyclerView.ViewHolder(binding.root) {

        fun onBind(advertise: Advertise, position: Int) {
            storageReference.child("${advertise.phoneNumber}${advertise.createdTime}")
                .child("0.jpg").downloadUrl.addOnSuccessListener {
                    Picasso.get().load(it).error(R.drawable.errorplaceholder)
                        .placeholder(R.drawable.home_placeholder).into(binding.homeImage)
                }.addOnFailureListener {
                    Picasso.get().load(R.drawable.home_placeholder)
                        .error(R.drawable.errorplaceholder)
                        .placeholder(R.drawable.home_placeholder).into(binding.homeImage)
                }
            binding.name.text = advertise.name
            binding.createdTime.text = advertise.createdTime
            binding.price.text = advertise.price

            binding.root.setOnClickListener {
                elonClick.onElonClick(advertise, position)
            }

            binding.imageMore.setOnClickListener {
                elonClick.onElonItemClick(advertise, position, it)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Vh {
        return Vh(ItemElonBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: Vh, position: Int) {
        holder.onBind(list[position], position)
    }

    override fun getItemCount(): Int = list.size

    interface ElonClick {

        fun onElonClick(advertise: Advertise, position: Int)

        fun onElonItemClick(advertise: Advertise, position: Int, view: View)

    }
}