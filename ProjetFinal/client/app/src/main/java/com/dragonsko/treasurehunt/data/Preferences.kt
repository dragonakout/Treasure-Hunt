package com.dragonsko.treasurehunt.data

data class Preferences(      
    val prefers_distance_measured_in_time: Boolean = true,
                             
    val prefers_walking: Boolean = true,
                             
    val assume_go_back: Boolean = true,
                             
    val average_distance: Int = 1,
    val nb_daily_quests: Int = 2
)
