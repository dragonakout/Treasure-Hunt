package com.treasure.hunt.data

data class Treasure(
    val id: Int,
    val size: String,
    val adjective: String,
    val name: String,
    val latitude: Double,
    val longitude: Double,
    val estimated_value: Int,
    val actual_value: Double,
    val quest_length: String,
    var collected_timestamp: String
)
