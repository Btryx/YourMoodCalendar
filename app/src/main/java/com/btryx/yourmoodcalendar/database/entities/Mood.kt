package com.btryx.yourmoodcalendar.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "mood")
data class Mood(
    @PrimaryKey val day: String, //yyyy-MM-dd
    val description: String,
    val type: MoodType
) {
    enum class MoodType {
        HAPPY,
        CONFIDENT,
        FINE,
        BORED,
        STRESSED,
        ANGRY,
        SAD,
    }
}