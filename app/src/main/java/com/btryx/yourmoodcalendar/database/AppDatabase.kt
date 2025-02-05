package com.btryx.yourmoodcalendar.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.btryx.yourmoodcalendar.database.dao.MoodDao
import com.btryx.yourmoodcalendar.database.entities.Mood
import java.util.Date

@Database(entities = [Mood::class], version = 1)
@TypeConverters(DateConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun moodDao(): MoodDao
}

class DateConverter {
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }
}