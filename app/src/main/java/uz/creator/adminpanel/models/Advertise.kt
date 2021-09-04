package uz.creator.adminpanel.models

import com.google.firebase.firestore.GeoPoint

data class Advertise(
    var type: String,
    var isActive: Boolean,
    var condition: String,
    var propertyType: String,
    var phoneNumber: String,
    var geoPoint: GeoPoint,
    var roomCount: Int,
    var livingArea: Double,
    var floor: Int,
    var totalFloor: Int,
    var foundation: String,
    var createdTime: String,
    var checkList: List<CheckBoxModel>
)