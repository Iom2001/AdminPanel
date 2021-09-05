package uz.creator.adminpanel.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import kotlinx.coroutines.NonDisposableHandle.parent
import uz.creator.adminpanel.R
import uz.creator.adminpanel.databinding.FragmentFilterBinding

class FilterFragment : Fragment() {

    lateinit var binding: FragmentFilterBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFilterBinding.inflate(layoutInflater , container , false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        filtrManzili()
        filtrMulkTuri()
        filtrUyturi()

    }

    companion object {

        @JvmStatic
        fun newInstance() = FilterFragment()
    }

    private fun filtrManzili(){
        val items = listOf("Andijon" , "Buxoro" , "Farg'ona" , "Jizzax" , "Xorazm" , "Namangan"
            , "Navoiy" , "Qashqadaryo" , "Samarqand" , "Sirdaryo" , "Surxondaryo" , "Toshkent")
        val adapter = ArrayAdapter(requireContext() , R.layout.support_simple_spinner_dropdown_item , items)
        (binding.filtrManzil.editText as? AutoCompleteTextView)?.setAdapter(adapter)
    }

    private fun filtrMulkTuri(){
        val items = listOf("Sotuv" , "Ijara")
        val adapter = ArrayAdapter(requireContext() , R.layout.support_simple_spinner_dropdown_item , items)
        (binding.filtrMulkTuri.editText as? AutoCompleteTextView)?.setAdapter(adapter)
    }

    private fun filtrUyturi() {
        val items = listOf("Uy", "Kvartira" , "Hovli")
        val adapter =
            ArrayAdapter(requireContext(), R.layout.support_simple_spinner_dropdown_item, items)
        (binding.filtrUyTuri.editText as? AutoCompleteTextView)?.setAdapter(adapter)
    }



}