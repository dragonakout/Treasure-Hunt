package com.treasure.hunt.data

data class Treasure(
    val id: Int,
    val size: String,
    val adjective: String,
    val name: String,
    val lattitude: Float,
    val longitude: Float,
    val estimated_value: Int,
    val actual_value: Double,
    val quest_length: String,
)


/*data class Treasure(
    val id: Int,
    val name: String,
    val estimated_value: Int,
    val actual_value: Int,
    val quest_length: Float,
    val lattitude: Float,
    val longitude: Float,
)*/