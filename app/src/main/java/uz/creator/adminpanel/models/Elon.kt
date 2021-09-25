package uz.creator.adminpanel.models

import com.google.firebase.firestore.GeoPoint
import java.io.Serializable

data class Elon(
    var type: String? = null,
    var homeDesc: String? = null,
    var price: String? = null,
    var condition: String? = null,
    var homeType: String? = null,
    var phoneNumber: String? = null,
    var geoPoint: GeoPoint? = null,
    var roomCount: String? = null,
    var floor: String? = null,
    var totalFloor: String? = null,
    var foundation: String? = null,
    var createdTime: String? = null,
    var checkedItemsHave: List<Boolean>? = null,
    var checkedItemsNear: List<Boolean>? = null,
) : Serializable