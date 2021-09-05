package uz.creator.adminpanel.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
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
        btnFilter()
    }

    private fun btnFilter() {
        binding.btnFiltr.setOnClickListener {
            Toast.makeText(requireContext() , binding.textView3.text , Toast.LENGTH_LONG).show()
            Toast.makeText(requireContext() , binding.autoComplete.text , Toast.LENGTH_LONG).show()
            Toast.makeText(requireContext() , binding.textView2.text , Toast.LENGTH_LONG).show()
        }
    }

    companion object {

        @JvmStatic
        fun newInstance() = FilterFragment()
    }

    private fun filtrManzili(){
        val items = listOf("Arnasoy" , "Baxmal" , "Do'stlik" , "Forish" , "G'allaorol" , "Jizzax shahri"
            , "Jizzax tumani" , "Mirzacho'l" , "Samarqand" , "Paxtakor" , "Yangiobod" , "Zafarobod" , "Zarband" , "Zomin")
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