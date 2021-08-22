package uz.creator.userpanel.fragments.home._addElon._viewPager

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.darsh.multipleimageselect.activities.AlbumSelectActivity
import com.darsh.multipleimageselect.helpers.Constants
import uz.creator.adminpanel.R
import uz.creator.adminpanel.databinding.FragmentPictureAddVPBinding


class PictureAddVPFragment : Fragment() {
    private var _binding: FragmentPictureAddVPBinding? = null
    val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPictureAddVPBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
      binding.ChooseImage.setOnClickListener {
          val intent = Intent(activity, AlbumSelectActivity::class.java)
          intent.putExtra(Constants.INTENT_EXTRA_LIMIT, 10)
          startActivityForResult(intent, Constants.REQUEST_CODE)
      }
    }
}