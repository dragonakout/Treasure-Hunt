package com.dragonsko.treasurehunt.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable
import org.json.JSONObject


// You can now serialize an instance of this class by calling Json.encodeToString().
//                                                              - kotlinlang.com
@Serializable
@Entity
data class Quest(
    @PrimaryKey(autoGenerate = true) val id: Int,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "distance") val distance: Int,
    @ColumnInfo(name = "estimated_value") val estimated_value: Int,
    @ColumnInfo(name = "actual_value") val actual_value: Int,
    @ColumnInfo(name = "latitude") val latitude: Double,
    @ColumnInfo(name = "longitude") val longitude: Double,
    @ColumnInfo(name = "is_new") val is_new: Boolean
)