package com.btryx.yourmoodcalendar.database.repository

import com.btryx.yourmoodcalendar.database.dao.MoodDao
import com.btryx.yourmoodcalendar.database.entities.Mood
import javax.inject.Inject

class MoodRepository @Inject constructor(
    private val moodDao: MoodDao
) {

    suspend fun getMoodByDate(day: String): Mood {
        return moodDao.getByDate(day)
    }

    suspend fun getMoodsByType(type: Mood.MoodType): List<Mood> {
        return moodDao.getMoodsByType(type)
    }

    suspend fun getMoodsByMonthAndType(month: String, type: Mood.MoodType): List<Mood> {
        return moodDao.getMoodsByMonthAndType(month, type)
    }

    suspend fun getAllMoods(): List<Mood> {
        return moodDao.getAll()
    }

    suspend fun insertMood(mood: Mood) {
        moodDao.insert(mood)
    }
}