package uz.creator.adminpanel

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import uz.creator.adminpanel.R
import uz.creator.adminpanel.databinding.ActivityMainBinding
import uz.creator.adminpanel.repository.AdminRepository
import uz.creator.adminpanel.viewmodel.AdminViewModel
import uz.creator.adminpanel.viewmodel.AdminViewModelProviderFactory

class MainActivity : AppCompatActivity() {

    lateinit var viewModel: AdminViewModel
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val newsRepository = AdminRepository()
        val viewModelProviderFactory = AdminViewModelProviderFactory(application, newsRepository)
        viewModel =
            ViewModelProvider(this, viewModelProviderFactory)[AdminViewModel::class.java]

        val navController = findNavController(R.id.fragmentContainerView)
        //  setLanguage(Language.getLanguage())
        binding.bottomNavigationView.setupWithNavController(navController)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.loginFragment -> {
                 hideBottomNav()
                }
                R.id.newAddElonFragment->{
                    hideBottomNav() }
                else->{showBottomNav()}
            }
        }
    }

    private fun showBottomNav() {
       binding.bottomNavigationView.visibility = View.VISIBLE
    }

    private fun hideBottomNav() {
        binding.bottomNavigationView.visibility = View.GONE
    }


}