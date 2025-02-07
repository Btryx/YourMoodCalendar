package com.btryx.yourmoodcalendar.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.btryx.yourmoodcalendar.database.entities.Mood
import kotlinx.coroutines.flow.Flow
import java.util.Date

@Dao
interface MoodDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: Mood)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(vararg items: Mood)

    @Update
    suspend fun update(item: Mood)

    @Delete
    suspend fun delete(item: Mood)

    @Query("SELECT * FROM mood where day= :day LIMIT 1")
    suspend fun getByDate(day: String): Mood

    @Query("SELECT * FROM mood WHERE SUBSTR(day, 6, 2) = :month and type = :type")
    suspend fun getMoodsByMonthAndType(month: String, type: Mood.MoodType): List<Mood>

    @Query("SELECT * FROM mood WHERE type = :type")
    suspend fun getMoodsByType(type: Mood.MoodType): List<Mood>

    @Query("SELECT * FROM mood")
    suspend fun getAll(): List<Mood>
}