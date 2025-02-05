package com.btryx.yourmoodcalendar

import android.content.Context
import androidx.room.Room
import com.btryx.yourmoodcalendar.database.AppDatabase
import com.btryx.yourmoodcalendar.database.dao.MoodDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext appContext: Context): AppDatabase {
        return Room.databaseBuilder(
            appContext,
            AppDatabase::class.java,
            "room_database"
        ).build()
    }
    @Provides
    @Singleton
    fun provideMoodDao(appDatabase: AppDatabase) : MoodDao{
        return appDatabase.moodDao()
    }
}