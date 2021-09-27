package uz.creator.adminpanel.models

import com.google.firebase.firestore.GeoPoint
import java.io.Serializable

data class Advertise(
    var type: String? = null,
    var name: String? = null,
    var homeDesc: String? = null,
    var address: String? = null,
    var price: Int? = null,
    var isActive: Boolean? = null,
    var condition: String? = null,
    var homeType: String? = null,
    var phoneNumber: String? = null,
    var homePhoneNumber: String? = null,
    var geoPoint: GeoPoint? = null,
    var roomCount: Int? = null,
    var floor: Int? = null,
    var totalFloor: Int? = null,
    var foundation: String? = null,
    var createdTime: String? = null,
    var totalArea: String? = null,
    var livingArea: String? = null,
    var checkedItemsHave: List<Boolean>? = null,
    var checkedItemsNear: List<Boolean>? = null,
) : Serializable