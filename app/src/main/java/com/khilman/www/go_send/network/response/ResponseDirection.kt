package com.khilman.www.go_send.network.response

data class ResponseDirection(
	val routes: List<RoutesItem?>? = null,
	val geocodedWaypoints: List<GeocodedWaypointsItem?>? = null,
	val status: String? = null
)
