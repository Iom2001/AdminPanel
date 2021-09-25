package uz.creator.adminpanel.models

data class User(
    var username: String? = null,
    var phoneNumber: String? = null,
    var imageUri: String? = null,
    var pin: String? = null,
    var deviceId: String? = null,
    var isActive: Boolean? = null
)