package uz.creator.adminpanel.models

import com.google.firebase.Timestamp
import com.google.firebase.firestore.GeoPoint

data class Advertise(
    var type: String? = null,
    var name: String? = null,
    var price: String? = null,
    var isActive: Boolean? = null,
    var condition: String? = null,
    var propertyType: String? = null,
    var phoneNumber: String? = null,
    var geoPoint: GeoPoint? = null,
    var roomCount: Int? = null,
    var livingArea: Double? = null,
    var floor: Int? = null,
    var totalFloor: Int? = null,
    var foundation: String? = null,
    var createdTime: String? = null,
    var checkList: List<CheckBoxModel>? = null
)