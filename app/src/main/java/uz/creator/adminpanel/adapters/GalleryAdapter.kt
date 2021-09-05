package uz.creator.adminpanel.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import uz.creator.adminpanel.databinding.HomeItemLayoutBinding
import uz.creator.adminpanel.models.CheckBoxModel

class GalleryAdapter(val items: List<CheckBoxModel>) : RecyclerView.Adapter<GalleryAdapter.ItemHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemHolder {
        var binding =
            HomeItemLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return ItemHolder(binding)
    }

    override fun getItemCount(): Int {
        return items.count()
    }

    override fun onBindViewHolder(holder: ItemHolder, position: Int) {
        val item = items[position]



        with(holder) {

            if (item.check) {

            }

            binding.tvUyQavati.setText(item.title)
            binding.tvUyTuri.setText("Hovli")
            binding.tvXonaSoni.setText("4")

        }


    }

    inner class ItemHolder(var binding: HomeItemLayoutBinding) :
        RecyclerView.ViewHolder(binding.root)

}