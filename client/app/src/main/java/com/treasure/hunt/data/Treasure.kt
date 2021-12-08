package com.treasure.hunt.data

data class Treasure(
    val id: Int,
    val size: String,
    val adjective: String,
    val name: String,
    val lattitude: Double,
    val longitude: Double,
    val estimated_value: Int,
    val actual_value: Double,
    val quest_length: String,
)
