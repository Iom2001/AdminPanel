package uz.creator.adminpanel.models

import com.google.firebase.firestore.GeoPoint

data class Advertise(
    var type: Type,
    var propertyType: PropertyType,
    var phoneNumber: String,
    var geoPoint: GeoPoint,
    var roomCount: Int,
    var livingArea: Double,
    var floor: Int,
    var totalFloor: Int,
    var foundation: Foundation
)