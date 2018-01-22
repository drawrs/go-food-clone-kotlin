package com.khilman.www.go_send.network.response

data class GeocodedWaypointsItem(
	val types: List<String?>? = null,
	val geocoderStatus: String? = null,
	val placeId: String? = null
)
