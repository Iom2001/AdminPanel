package uz.creator.adminpanel.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import uz.creator.adminpanel.R
import uz.creator.adminpanel.databinding.ItemUserBinding
import uz.creator.adminpanel.models.User

class UserAdapter(var list: List<User>) : RecyclerView.Adapter<UserAdapter.Vh>() {

    inner class Vh(var binding: ItemUserBinding) : RecyclerView.ViewHolder(binding.root) {

        fun onBind(user: User) {
            if (!user.imageUri.isNullOrBlank()) {
                Picasso.get().load(user.imageUri).placeholder(R.drawable.person)
                    .error(R.drawable.errorplaceholder).into(binding.imagePerson)
            }
            if (!user.username.isNullOrBlank()) {
                binding.username.visibility = View.VISIBLE
                binding.username.text = user.username
            } else {
                binding.username.visibility = View.GONE
            }
            binding.phoneNumber.text = user.phoneNumber
            if (user.pin?.length == 4) {
                binding.pinTv.text = "Pin: " + user.pin
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
}