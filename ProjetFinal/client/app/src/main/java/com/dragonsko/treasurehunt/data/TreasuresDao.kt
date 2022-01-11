package com.dragonsko.treasurehunt.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface TreasuresDao {
    @Insert
    fun createTreasure(treasure: Treasure )

    @Delete
    fun deleteTreasure(treasure: Treasure)

    @Query("SELECT * FROM treasure")
    fun getAllTreasure() : List<Treasure>
}