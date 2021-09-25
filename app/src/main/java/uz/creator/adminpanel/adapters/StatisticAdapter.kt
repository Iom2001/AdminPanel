package uz.creator.adminpanel.adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import uz.creator.adminpanel.databinding.ItemStatisticBinding
import uz.creator.adminpanel.models.Advertise
import uz.creator.adminpanel.utils.CyrillicLatinConverter
import uz.creator.adminpanel.utils.Permanent

class StatisticAdapter(var list: ArrayList<Advertise>, val onItemClick: StatisticClick) :
    RecyclerView.Adapter<StatisticAdapter.Vh>() {

    inner class Vh(val binding: ItemStatisticBinding) : RecyclerView.ViewHolder(binding.root) {

        fun onBind(advertise: Advertise, position: Int) {
            if (Permanent.isKiril) {
                binding.homeType.text = advertise.homeType?.let { CyrillicLatinConverter.ltc(it) }
                binding.address.text = advertise.address?.let { CyrillicLatinConverter.ltc(it) }
                binding.type.text = CyrillicLatinConverter.ltc("Qo'shildi")
            } else {
                binding.homeType.text = advertise.homeType
                binding.address.text = advertise.address
                binding.type.text = "Qo'shildi"
            }
            binding.createdTime.text = advertise.createdTime
            binding.priceTv.text = "${advertise.price} $"
            if (!advertise.isActive!!) {
                if (advertise.type == "Sotuv") {
                    if (Permanent.isKiril) {
                        binding.type.text = CyrillicLatinConverter.ltc("Sotildi")
                    } else {
                        binding.type.text = "Sotildi"
                    }
                } else if (advertise.type == "Ijara") {
                    if (Permanent.isKiril) {
                        binding.type.text = CyrillicLatinConverter.ltc("Ijara")
                    } else {
                        binding.type.text = "Ijara"
                    }
                }
                binding.type.setTextColor(Color.RED)
                binding.layoutItem.setBackgroundColor(Color.parseColor("#FF7878"))
            }
            binding.imageMore.setOnClickListener {
                onItemClick.onItemClick(advertise, position)
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Vh {
        return Vh(ItemStatisticBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: Vh, position: Int) {
        holder.onBind(list[position], position)
    }

    override fun getItemCount(): Int = list.size

    interface StatisticClick {
        fun onItemClick(advertise: Advertise, position: Int)
    }
}