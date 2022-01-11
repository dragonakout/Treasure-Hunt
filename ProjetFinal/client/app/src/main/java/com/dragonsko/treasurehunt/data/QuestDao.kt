package com.dragonsko.treasurehunt.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface QuestDao {
    @Insert
    fun createQuest(quest: Quest )

    @Delete
    fun deleteQuest(quest: Quest)

    @Query("SELECT * FROM quest")
    fun getAllQuests() : List<Quest>
}