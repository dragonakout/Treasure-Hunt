package com.treasure.hunt.data

import kotlinx.serialization.Serializable
import org.json.JSONObject


// You can now serialize an instance of this class by calling Json.encodeToString().
//                                                              - kotlinlang.com
@Serializable
data class Treasure(
    val id: Int,
    val name: String,
    val estimated_value: Int,
    val actual_value: Double,
    val latitude: Float,
    val longitude: Float,
    val collected_timestamp: String,
) {
    constructor(json: JSONObject) : this(
        json.getInt("id"),
        json.getString("name"),
        json.getInt("estimated_value"),
        json.getDouble("actual_value"),
        json.getDouble("latitude").toFloat(),
        json.getDouble("longitude").toFloat(),
        json.getString("collected_timestamp")
    )
}