package uz.creator.adminpanel.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import uz.creator.adminpanel.repository.AdminRepository

class AdminViewModelProviderFactory(
    val app: Application,
    private val adminRepository: AdminRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return AdminViewModel(app, adminRepository) as T
    }
}