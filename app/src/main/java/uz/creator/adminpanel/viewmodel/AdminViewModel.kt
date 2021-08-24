package uz.creator.adminpanel.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import uz.creator.adminpanel.models.User
import uz.creator.adminpanel.repository.AdminRepository

class AdminViewModel(
    app: Application,
    private val adminRepository: AdminRepository
) : AndroidViewModel(app) {

    fun addAdmin(user: User) = adminRepository.addAdmin(user)

    fun checkAdminByPhoneNumber(phoneNumber: String) =
        adminRepository.checkAdminByPhoneNumber(phoneNumber)

    fun getAllUsers() = adminRepository.getAllUsers()

    fun addOrSetUser(user: User) = adminRepository.addOrSetUser(user)
}