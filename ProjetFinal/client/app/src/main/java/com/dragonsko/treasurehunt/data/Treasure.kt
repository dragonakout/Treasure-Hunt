package com.dragonsko.treasurehunt.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable
import org.json.JSONObject


@Serializable
@Entity
data class Treasure(
    @PrimaryKey(autoGenerate = true)  val id: Int,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "actual_value") val actual_value: Int,
    @ColumnInfo(name = "collected_timestamp") var collected_timestamp: String,
) {
    constructor(json: JSONObject) : this(
        json.getInt("id"),
        json.getString("name"),
        json.getInt("actual_value"),
        json.getString("collected_timestamp"),
    )
    constructor(quest: Quest, timestamp: String) : this(
        quest.id,
        quest.name,
        quest.actual_value,
        timestamp
    )
}