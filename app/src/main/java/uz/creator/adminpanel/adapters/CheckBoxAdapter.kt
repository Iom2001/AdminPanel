package uz.creator.adminpanel.adapters

import android.annotation.SuppressLint
import android.util.SparseBooleanArray
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import uz.creator.adminpanel.databinding.AdapterCheckboxBinding
import uz.creator.adminpanel.models.CheckBoxModel

class CheckBoxAdapter(
    private val context: FragmentActivity,
    private val click: OnCheckBoxListener
) : RecyclerView.Adapter<CheckBoxAdapter.MyHolderView>() {

    var layoutInflater = LayoutInflater.from(context)
    var checkBoxStateArray = SparseBooleanArray()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyHolderView {
        var binding = AdapterCheckboxBinding.inflate(layoutInflater, parent, false)
        return MyHolderView(binding)
    }

    override fun onBindViewHolder(holder: MyHolderView, position: Int) {
        with(holder) {
            binding.title.text = list[position].title
            checkBoxStateArray.get(position, true)
        }
    }

    override fun getItemCount(): Int = list.size

    inner class MyHolderView(var binding: AdapterCheckboxBinding) :
        RecyclerView.ViewHolder(binding.root) {
        init {
            binding.CBcheck.setOnClickListener {
                if (!checkBoxStateArray.get(adapterPosition, false)) {
                    binding.CBcheck.isChecked = true
                    checkBoxStateArray.put(adapterPosition, true)
                    click.OnclickListener(list[adapterPosition], true)
                } else {
                    binding.CBcheck.isChecked = false
                    checkBoxStateArray.put(adapterPosition, false)
                    click.OnclickListener(list[adapterPosition], false)
                }

            }
        }
    }

    private val diffCallback = object : DiffUtil.ItemCallback<CheckBoxModel>() {
        override fun areItemsTheSame(
            oldItem: CheckBoxModel,
            newItem: CheckBoxModel
        ): Boolean {
            return oldItem.title == newItem.title
        }

        @SuppressLint("DiffUtilEquals")
        override fun areContentsTheSame(
            oldItem: CheckBoxModel,
            newItem: CheckBoxModel
        ): Boolean {
            return newItem == oldItem
        }
    }
    private val differ = AsyncListDiffer(this, diffCallback)

    var list: List<CheckBoxModel>
        get() = differ.currentList
        set(value) {
            differ.submitList(value)
        }


    interface OnCheckBoxListener {
        fun OnclickListener(model: CheckBoxModel, ad: Boolean)
    }
}