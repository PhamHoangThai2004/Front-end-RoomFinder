package com.pht.roomfinder.model

import java.io.Serializable

class Location(
    val locationId: Int?,
    val address: String?,
    val longitude: Double?,
    val latitude: Double?
) : Serializable