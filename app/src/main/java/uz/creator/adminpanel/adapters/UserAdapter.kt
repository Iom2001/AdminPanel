package uz.creator.adminpanel.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FirebaseFirestore
import uz.creator.adminpanel.R
import uz.creator.adminpanel.databinding.ItemUserBinding
import uz.creator.adminpanel.models.User
import uz.creator.adminpanel.utils.CyrillicLatinConverter
import uz.creator.adminpanel.utils.Permanent

class UserAdapter(var list: List<User>, val userClick: UserClick) :
    RecyclerView.Adapter<UserAdapter.Vh>() {

    inner class Vh(var binding: ItemUserBinding) : RecyclerView.ViewHolder(binding.root) {

        private val query = FirebaseFirestore.getInstance().collection("admin")
        fun onBind(user: User) {
            query.document(user.phoneNumber!!).get().addOnSuccessListener {
                if (it.exists()) {
                    binding.imageMore.visibility = View.GONE
                }
            }
            if (!user.imageUri.isNullOrBlank()) {
                Glide.with(binding.root.context).load(user.imageUri).placeholder(R.drawable.person)
                    .error(R.drawable.errorplaceholder).into(binding.imagePerson)
            }
            if (!user.username.isNullOrBlank()) {
                binding.username.visibility = View.VISIBLE
                if (Permanent.isKiril) {
                    user.username?.let {
                        binding.username.text = CyrillicLatinConverter.ltc(it)
                    }
                } else {
                    binding.username.text = user.username
                }
            } else {
                binding.username.visibility = View.GONE
            }
            binding.phoneNumber.text = user.phoneNumber
            if (user.pin?.length == 4) {
                if (Permanent.isKiril) {
                    binding.pinTv.text = CyrillicLatinConverter.ltc("Pin: " + user.pin)
                } else {
                    binding.pinTv.text = "Pin: " + user.pin
                }
            }

            binding.imageMore.setOnClickListener {
                userClick.moreItemClick(user, it)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Vh {
        return Vh(ItemUserBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: Vh, position: Int) {
        holder.onBind(list[position])
    }

    override fun getItemCount(): Int = list.size

    interface UserClick {
        fun moreItemClick(user: User, view: View)
    }
}