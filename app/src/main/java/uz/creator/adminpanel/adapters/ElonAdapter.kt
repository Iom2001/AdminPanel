package uz.creator.adminpanel.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import uz.creator.adminpanel.R
import uz.creator.adminpanel.databinding.ItemElonBinding
import uz.creator.adminpanel.models.Elon
import uz.creator.adminpanel.utils.CyrillicLatinConverter
import uz.creator.adminpanel.utils.Permanent

class ElonAdapter(var list: List<Elon>, val elonClick: ElonClick) :
    RecyclerView.Adapter<ElonAdapter.Vh>() {

    inner class Vh(var binding: ItemElonBinding) : RecyclerView.ViewHolder(binding.root) {

        fun onBind(elon: Elon, position: Int) {
            Glide.with(binding.root.context).load(R.drawable.home_placeholder)
                .error(R.drawable.errorplaceholder)
                .placeholder(R.drawable.home_placeholder).into(binding.homeImage)
            if (Permanent.isKiril) {
                binding.type.text =
                    CyrillicLatinConverter.ltc(elon.homeType + ": " + elon.type + " uchun.")
            } else {
                binding.type.text = elon.homeType + ": " + elon.type + " uchun."
            }
            binding.createdTime.text = elon.createdTime
            binding.price.text = elon.price.toString()

            var info = "   "
            if (!elon.condition.isNullOrBlank()) {
                info = info + "Uy xolati: " + elon.condition + ".  "
            }
            if (!elon.roomCount.toString().isNullOrBlank()) {
                info = info + "Xonalar soni: " + elon.roomCount.toString() + ".  "
            }
            if (!elon.totalFloor.toString().isNullOrBlank()) {

                info = info + "Bino qavatliligi: " + elon.totalFloor.toString() + ".  "
            }
            if (!elon.floor.toString().isNullOrBlank()) {
                info = info + "Uy qavati: " + elon.floor.toString() + ".  "
            }
            if (!elon.foundation.isNullOrBlank()) {
                info = info + "Qurulish turi: " + elon.foundation + "."
            }
            if (info.isBlank()) {
                binding.infoTv.visibility = View.GONE
            } else {
                if (Permanent.isKiril) {
                    binding.infoTv.text = CyrillicLatinConverter.ltc(info)
                } else {
                    binding.infoTv.text = info
                }
            }
            val listHave = arrayOf(
                "Konditsioner",
                "Kir yuvish mashinasi",
                "Muzlatkich",
                "Televizor",
                "Internet",
                "Kabelli TV",
                "Telefon",
                "Oshxona",
                "Balkon"
            )
            val listNear = arrayOf(
                "Kasalxona",
                "Bolalar maydoni",
                "Bolalar bog'chasi",
                "Bekatlar",
                "Park, yashil zona",
                "Restoran, kafe",
                "Turargoh",
                "Supermaret, do'kon",
                "Maktab"
            )
            var isHave = "   Uyda bor: "
            for (i in elon.checkedItemsHave?.indices!!) {
                if (elon.checkedItemsHave!![i]) {
                    isHave += if (isHave.length == 13) {
                        listHave[i]
                    } else {
                        "," + listHave[i]
                    }
                }
            }
            if (isHave.length > 13) {
                if (Permanent.isKiril) {
                    binding.haveTv.text = CyrillicLatinConverter.ltc("$isHave.")
                } else {
                    binding.haveTv.text = "$isHave."
                }
            } else {
                binding.haveTv.visibility = View.GONE
            }
            var isNear = "   Uyga yaqin: "
            for (i in elon.checkedItemsNear?.indices!!) {
                if (elon.checkedItemsNear!![i]) {
                    isNear += if (isNear.length == 15) {
                        listNear[i]
                    } else {
                        "," + listNear[i]
                    }
                }
            }
            if (isNear.length > 15) {
                if (Permanent.isKiril) {
                    binding.nearTv.text = CyrillicLatinConverter.ltc("$isNear.")
                } else {
                    binding.nearTv.text = "$isNear."
                }
            } else {
                binding.nearTv.visibility = View.GONE
            }

            if (!elon.homeDesc.isNullOrBlank()) {
                if (Permanent.isKiril) {
                    binding.descTv.text = CyrillicLatinConverter.ltc("   " + elon.homeDesc)
                } else {
                    binding.descTv.text = "   " + elon.homeDesc
                }
            } else {
                binding.descTv.visibility = View.GONE
            }

            binding.root.setOnClickListener {
                elonClick.onElonClick(elon, position)
            }

            binding.imageMore.setOnClickListener {
                elonClick.onElonItemClick(elon, position, it)
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

        fun onElonClick(elon: Elon, position: Int)

        fun onElonItemClick(elon: Elon, position: Int, view: View)

    }
}