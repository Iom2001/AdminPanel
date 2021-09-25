package uz.creator.adminpanel.models

import java.io.Serializable

data class FilterModel(
    var searchText: String? = null,
    var type: String? = null,
    var homeType: String? = null,
    var startPrice: String? = null,
    var endPrice: String? = null,
    var startRoom: Int? = null,
    var endRoom: Int? = null,
    var startFloor: Int? = null,
    var endFloor: Int? = null,
    var startTotalFloor: Int? = null,
    var endTotalFloor: Int? = null,
) : Serializable